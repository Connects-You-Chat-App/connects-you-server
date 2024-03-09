package com.adarsh.connectsYouServer.listeners

import com.adarsh.connectsYouServer.models.common.KafkaMessage
import com.adarsh.connectsYouServer.utils.KafkaUtils
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.concurrent.thread

@Component
class KafkaConsumers(
    private val kafkaUtils: KafkaUtils,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val adminClient = kafkaUtils.createAdminClient()
    private val threads = mutableListOf<Thread>()
    private val groupIds = mutableListOf<String>()

    private fun <T> consume(
        topic: String,
        onMessage: (T) -> Unit,
    ) {
        threads.add(
            thread {
                val consumer = kafkaUtils.createConsumer<T>(topic)
                groupIds.add(consumer.groupMetadata().groupId())
                consumer.use {
                    while (true) {
                        val records = consumer.poll(Duration.ofMillis(250))
                        records.iterator().forEach {
                            onMessage(it.value())
                        }
                    }
                }
            },
        )
    }

    fun consumeBroadcast(onMessage: (KafkaMessage) -> Unit) {
        consume("broadcast", onMessage)
    }

    fun consumeRoom(onMessage: (KafkaMessage) -> Unit) {
        consume("room", onMessage)
    }

    fun consumeCommon(onMessage: (KafkaMessage) -> Unit) {
        consume("common", onMessage)
    }

    @PreDestroy
    fun stopListening() {
        threads.forEach {
            it.interrupt()
            it.join()
        }
        threads.clear()
        adminClient.deleteConsumerGroups(groupIds).all().get()
        adminClient.close()
    }
}