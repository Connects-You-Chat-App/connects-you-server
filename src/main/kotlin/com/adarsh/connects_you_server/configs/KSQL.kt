package com.adarsh.connects_you_server.configs

import com.adarsh.connects_you_server.models.common.KafkaMessage
import io.confluent.ksql.api.client.ClientOptions
import io.confluent.ksql.api.client.KsqlObject
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.time.Duration
import java.util.*
import io.confluent.ksql.api.client.Client as KSQLClient


@Configuration
@Lazy
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
            logger.error("error creating stream $streamName: ${e.message}")
            e.message?.contains("A stream with the same name already exists") == true
        }
    }

    private fun createUserMessageStream(): Boolean {
        val streamName = "USER_MESSAGES"
        val streamDefinition = """(
                ID VARCHAR,
                ROOM_ID VARCHAR, 
                SENDER_USER_ID VARCHAR, 
                RECEIVER_USER_ID VARCHAR, 
                MESSAGE VARCHAR, 
                TYPE VARCHAR, 
                SEND_AT VARCHAR, 
                BELONGS_TO_THREAD_ID VARCHAR
            )
            WITH (
                KAFKA_TOPIC='USER_MESSAGES', 
                VALUE_FORMAT='JSON', 
                TIMESTAMP_FORMAT = 'yyyy-MM-dd HH:mm:ss', 
                PARTITIONS=2
            );
        """.trimIndent()
        return createStream(streamName, streamDefinition)
    }

    private fun createUserFilteredMessageStream(userId: String): Boolean {
        val streamName = "FILTERED_USER_MESSAGES_${userId.replace("-", "_")}"
        val streamDefinition =
            """ AS SELECT * FROM USER_MESSAGES WHERE SENDER_USER_ID = '$userId' OR RECEIVER_USER_ID = '$userId';""".trimIndent()
        return createStream(streamName, streamDefinition)
    }

    private fun createUserStatusStream(): Boolean {
        val streamName = "USER_STATUS"
        val streamDefinition = """(
                USER_ID VARCHAR,
                STATUS VARCHAR
            )
            WITH (
                KAFKA_TOPIC='USER_STATUS', 
                VALUE_FORMAT='JSON', 
                TIMESTAMP_FORMAT = 'yyyy-MM-dd HH:mm:ss', 
                PARTITIONS=2
            );
        """.trimIndent()
        return createStream(streamName, streamDefinition)
    }

    private fun createUserFilteredStatusStream(userId: String): Boolean {
        val streamName = "FILTERED_USER_STATUS_${userId.replace("-", "_")}"
        val streamDefinition =
            """ AS SELECT * FROM USER_STATUS WHERE USER_ID = '$userId';""".trimIndent()
        return createStream(streamName, streamDefinition)
    }

    fun produceUserMessage(message: KafkaMessage): Boolean {
        val insertOperation = ksqlClient.insertInto("USER_MESSAGES", message.toKafkaObject())
        return try {
            insertOperation.get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun produceUserStatus(userId: String, status: String): Boolean {
        val data = KsqlObject()
            .put("USER_ID", userId)
            .put("STATUS", status)
        val insertOperation = ksqlClient.insertInto("USER_STATUS", data)
        return try {
            insertOperation.get()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getConsumerProperties(userId: String, deviceId: String): Properties {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServer
        props[ConsumerConfig.GROUP_ID_CONFIG] =
            "ksql_${userId.replace("-", "_")}_${deviceId.replace("-", "_")}_consumer"

        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] =
            StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] =
            StringDeserializer::class.java.name
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest" // only read new messages
        return props
    }

    suspend fun consumeUserMessages(
        userId: String,
        deviceId: String
    ): Pair<Sequence<KafkaMessage>, KafkaConsumer<String, String>> {
        val streamName = "FILTERED_USER_MESSAGES_${userId.replace("-", "_")}".uppercase()
        createUserFilteredMessageStream(userId)
        val props = getConsumerProperties(userId, deviceId)
        val consumer = KafkaConsumer<String, String>(props)
        consumer.subscribe(listOf(streamName))

        return Pair(sequence {
            consumer.use { consumer ->
                while (true) {
                    val records = consumer.poll(Duration.ofMillis(500))
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