package com.adarsh.connects_you_server.utils

import com.fasterxml.jackson.databind.ObjectMapper
import java.text.SimpleDateFormat
import java.util.*

class JSON {
    companion object {
        private val mapper = ObjectMapper()

        init {
            mapper.setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
            mapper.setTimeZone(TimeZone.getTimeZone("Etc/UTC"))
        }

        fun <T> fromJson(json: String, clazz: Class<T>): T {
            return mapper.readValue(json, clazz)
        }

        fun toJson(obj: Any): String {
            return mapper.writeValueAsString(obj)
        }
        
        fun toMap(obj: Any): Map<*, *> {
            return mapper.convertValue(obj, Map::class.java)
        }
    }
}