package com.adarsh.connects_you_server.utils.deserializers

import com.adarsh.connects_you_server.models.common.KafkaMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class KafkaMessageDeserializer : Deserializer<KafkaMessage> {
    private val objectMapper = ObjectMapper()

    override fun deserialize(topic: String?, data: ByteArray?): KafkaMessage {
        return try {
            if (data == null) {
                throw SerializationException("Null received at deserializing")
            }
            val readValue =
                objectMapper.readValue(String(data, charset("UTF-8")), KafkaMessage::class.java)
            readValue
        } catch (e: Exception) {
            println(e)
            throw SerializationException("Error when deserializing byte[] to MessageDto")
        }
    }
}