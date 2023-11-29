package com.adarsh.chat_server.configs

import io.confluent.ksql.api.client.ClientOptions
import org.springframework.context.annotation.Configuration
import io.confluent.ksql.api.client.Client as KSQLClient


@Configuration
class KSQL {
    private val ksqlServer = "localhost"
    private var ksqlClient: KSQLClient? = null

    fun ksqlClient(): KSQLClient {
        if (ksqlClient != null) {
            return ksqlClient!!
        }
        val options: ClientOptions = ClientOptions.create().setHost(ksqlServer).setPort(8088)
        return KSQLClient.create(options)
    }
}