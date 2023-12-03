package com.adarsh.chat_server.configs

import com.adarsh.chat_server.models.KafkaMessage
import io.confluent.ksql.api.client.ClientOptions
import io.confluent.ksql.api.client.KsqlObject
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.*
import io.confluent.ksql.api.client.Client as KSQLClient


@Configuration
class KSQL(
    @Value("\${kafka.bootstrap.server}") private val bootstrapServer: String,
    @Value("\${kafka.ksql.host}") private val ksqlServerHost: String,
    @Value("\${kafka.ksql.port}") private val ksqlServerPort: Int,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var cachedKsqlClient: KSQLClient

    val ksqlClient: KSQLClient
        get() {
            if (::cachedKsqlClient.isInitialized) {
                return cachedKsqlClient
            }
            val options: ClientOptions = ClientOptions.create().setHost(ksqlServerHost).setPort(ksqlServerPort)
            cachedKsqlClient = KSQLClient.create(options)
            if (createStreams()) {
                return cachedKsqlClient
            } else {
                throw Exception("Failed to create streams")
            }
        }

    private fun createStreams(): Boolean {
        val userMessageStreamCreated = createUserMessageStream()
        val userStatusStreamCreated = createUserStatusStream()
        return userMessageStreamCreated && userStatusStreamCreated
    }

    private fun createStream(streamName: String, streamDefinition: String): Boolean {
        val statement = "CREATE STREAM $streamName $streamDefinition"
        val createStreamResponse = ksqlClient.executeStatement(
            statement
        )
        return try {
            createStreamResponse.get()
            true
        } catch (e: Exception) {
            println("error creating stream $streamName: ${e.message}")
            e.message?.contains("A stream with the same name already exists") == true
        }
    }

    private fun createUserMessageStream(): Boolean {
        val streamName = "user_messages"
        val streamDefinition = """(
                id VARCHAR,
                room_id VARCHAR, 
                sender_user_id VARCHAR, 
                receiver_user_id VARCHAR, 
                message VARCHAR, 
                type VARCHAR, 
                send_at VARCHAR, 
                belongs_to_thread_id VARCHAR
            )
            WITH (
                KAFKA_TOPIC='user_messages', 
                VALUE_FORMAT='JSON', 
                TIMESTAMP_FORMAT = 'yyyy-MM-dd HH:mm:ss', 
                PARTITIONS=2
            );
        """.trimIndent()
        return createStream(streamName, streamDefinition)
    }

    private fun createUserFilteredMessageStream(userId: String): Boolean {
        val streamName = "filtered_user_messages_${userId.replace("-", "_")}"
        val streamDefinition =
            """ AS SELECT * FROM user_messages WHERE sender_user_id = '$userId' OR receiver_user_id = '$userId';""".trimIndent()
        return createStream(streamName, streamDefinition)
    }

    private fun createUserStatusStream(): Boolean {
        val streamName = "user_status"
        val streamDefinition = """(
                user_id VARCHAR,
                status VARCHAR
            )
            WITH (
                KAFKA_TOPIC='user_status', 
                VALUE_FORMAT='JSON', 
                TIMESTAMP_FORMAT = 'yyyy-MM-dd HH:mm:ss', 
                PARTITIONS=2
            );
        """.trimIndent()
        return createStream(streamName, streamDefinition)
    }

    private fun createUserFilteredStatusStream(userId: String): Boolean {
        val streamName = "filtered_user_status_${userId.replace("-", "_")}"
        val streamDefinition =
            """ AS SELECT * FROM user_status WHERE userId = '$userId';""".trimIndent()
        return createStream(streamName, streamDefinition)
    }

    fun produceUserMessage(message: KafkaMessage): Boolean {
        val insertOperation = ksqlClient.insertInto("user_messages", message.toKafkaObject())
        return try {
            insertOperation.get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun produceUserStatus(userId: String, status: String): Boolean {
        val data = KsqlObject()
            .put("userId", userId)
            .put("status", status)
        val insertOperation = ksqlClient.insertInto("user_status", data)
        return try {
            insertOperation.get()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getConsumerProperties(userId: String): Properties {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServer
        props[ConsumerConfig.GROUP_ID_CONFIG] = "ksql_${userId.replace("-", "_")}_consumer"

        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] =
            StringDeserializer::class.java.getName()
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] =
            StringDeserializer::class.java.getName()
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest" // only read new messages
        return props
    }

    suspend fun consumeUserMessages(userId: String): Pair<Sequence<KafkaMessage>, KafkaConsumer<String, String>> {
        val streamName = "filtered_user_messages_${userId.replace("-", "_")}"
        createUserFilteredMessageStream(userId)
        val props = getConsumerProperties(userId)
        val consumer = KafkaConsumer<String, String>(props)
        consumer.subscribe(listOf(streamName.uppercase()))

        return Pair(sequence {
            consumer.use { consumer ->
                while (true) {
                    val records = consumer.poll(Duration.ofMillis(1000))
                    records.forEach { record ->
                        val value = record.value() ?: return@forEach
                        val message = KafkaMessage.fromKafkaResponse(value)
                        yield(message)
                    }
                }
            }
        }, consumer)
    }
}