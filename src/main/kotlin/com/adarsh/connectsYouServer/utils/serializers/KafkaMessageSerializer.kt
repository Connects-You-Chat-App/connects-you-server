package com.adarsh.connectsYouServer.utils.serializers

import com.adarsh.connectsYouServer.models.common.KafkaMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class KafkaMessageSerializer : Serializer<KafkaMessage> {
    private val objectMapper = ObjectMapper()

    override fun serialize(
        topic: String?,
        data: KafkaMessage?,
    ): ByteArray {
        try {
            if (data == null) {
                throw SerializationException("Null received at serializing")
            }
            return objectMapper.writeValueAsBytes(data)
        } catch (error: Exception) {
            println(error)
            throw SerializationException("Error when serializing KafkaMessage to byte[]")
        }
    }
}
