package com.adarsh.connects_you_server.utils

import com.adarsh.connects_you_server.models.common.KafkaMessage
import com.adarsh.connects_you_server.utils.deserializers.KafkaMessageDeserializer
import com.adarsh.connects_you_server.utils.serializers.KafkaMessageSerializer
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaUtils(
    @Value("\${kafka.bootstrap.server}") private val bootstrapServer: String,
) {
    var producer: KafkaProducer<String, KafkaMessage>? = null
        get() {
            if (field == null) {
                field = KafkaProducer(createProducerConfig())
            }
            return field
        }


    private fun createProducerConfig(): Properties {
        val properties = Properties()
        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServer
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaMessageSerializer::class.java.name
        return properties
    }

    private fun createConsumerConfig(): Properties {
        val properties = Properties()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServer
        properties[ConsumerConfig.GROUP_ID_CONFIG] = "connects_you_server_${UUID.randomUUID()}"
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaMessageDeserializer::class.java.name
        return properties
    }

    fun <T> createConsumer(topic: String): KafkaConsumer<String, T> {
        val properties = createConsumerConfig()
        val consumer = KafkaConsumer<String, T>(properties)
        consumer.subscribe(listOf(topic))
        return consumer
    }

    fun createRoomProducerRecord(
        roomId: String,
        message: KafkaMessage
    ): ProducerRecord<String, KafkaMessage> {
        return ProducerRecord("room", message)
    }

    fun createBroadcastProducerRecord(message: KafkaMessage): ProducerRecord<String, KafkaMessage> {
        return ProducerRecord("broadcast", message)
    }

    fun createCommonProducerRecord(message: KafkaMessage): ProducerRecord<String, KafkaMessage> {
        return ProducerRecord("common", message)
    }

    fun createAdminClient(): AdminClient {
        return AdminClient.create(createConsumerConfig())
    }
}