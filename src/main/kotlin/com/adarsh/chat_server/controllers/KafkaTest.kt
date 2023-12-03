package com.adarsh.chat_server.controllers

import com.adarsh.chat_server.configs.KSQL
import com.adarsh.chat_server.models.KafkaMessage
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


//
//import com.adarsh.chat_server.configs.KSQL
//import com.adarsh.chat_server.models.Message
//import io.confluent.ksql.api.client.KsqlObject
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.clients.consumer.KafkaConsumer
//import org.apache.kafka.clients.producer.KafkaProducer
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.slf4j.LoggerFactory
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import java.time.Duration
//import java.util.*
//import java.util.concurrent.ConcurrentHashMap
//
//
////@RestController
////@RequestMapping("/kafka")
//class KafkaTest(
////    private val kafkaListenerContainerFactory: KafkaListenerContainerFactory<*>,
//    private val KSQL: KSQL
//) {
//    //    private val kafkaListenerEndpointRegistry = KafkaListenerEndpointRegistry()
//    private val userIdConsumerMap = mutableMapOf<String, Any?>()
//    private val isConsumerRunning = ConcurrentHashMap<String, Boolean>()
//    private val bootstrapServers = "localhost:29092"
//    private lateinit var producer: KafkaProducer<String, String>
//    private val logger = LoggerFactory.getLogger(javaClass)
//
////    @PostMapping("/send/{userId}")
////    fun sendKafkaMessage(@RequestBody message: Message, @PathVariable userId: String): ResponseEntity<String> {
//////        if (!(::producer.isInitialized)) {
//////            val props = Properties()
//////            props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
//////            props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.getName()
//////            props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.getName()
//////
//////            producer = KafkaProducer<String, String>(props)
//////
//////        }
////
////
////        KSQL.ksqlClient().executeStatement(
////            "CREATE STREAM user_messages (USER_ID VARCHAR, MESSAGE VARCHAR)\n WITH (KAFKA_TOPIC='messages', VALUE_FORMAT='JSON');",
////        ).whenComplete { result, error ->
////            if (error != null) {
////                logger.error("Error creating stream user_messages {}", error.message)
////            } else {
////                logger.info("After creating stream user_messages {}", result)
////            }
////        }
//////        val record = ProducerRecord<String, String>("messages", message.toString())
//////        producer.send(record)
////        return ResponseEntity.ok("Sent message to Kafka for user $userId")
////    }
//
//    //    @PostMapping("/send/{userId}")
//    fun sendKafkaMessage(@RequestBody message: Message, @PathVariable userId: String): ResponseEntity<String> {
//        val streamCreateQuery = KSQL.ksqlClient().executeStatement(
//            "CREATE STREAM user_messages (USER_ID VARCHAR, MESSAGE VARCHAR)\n WITH (KAFKA_TOPIC='messages', VALUE_FORMAT = 'JSON',\n" +
//                    "        TIMESTAMP_FORMAT = 'yyyy-MM-dd HH:mm:ss',\n" +
//                    "        PARTITIONS = 2);",
//        )
//        try {
//            streamCreateQuery.get()
//        } catch (e: Exception) {
//            logger.error("Error creating stream user_messages {}", e.message)
//        }
//
//        val data = KsqlObject().put("USER_ID", userId).put("MESSAGE", message.toString())
//        val insertOperation = KSQL.ksqlClient().insertInto("user_messages", data)
//        try {
//            insertOperation.get()
//            logger.info("After inserting into stream user_messages {}", insertOperation)
//        } catch (e: Exception) {
//            logger.error("Error inserting into stream user_messages {}", e.message)
//        }
//        return ResponseEntity.ok("Sent message to Kafka for user $userId")
//    }
//
//    //    @PostMapping("/start/{userId}")
//    fun startKafkaListener(@PathVariable userId: String): ResponseEntity<String> {
//        val streamCreateQuery = KSQL.ksqlClient().executeStatement(
//            "CREATE STREAM user_messages (USER_ID VARCHAR, MESSAGE VARCHAR)\n WITH (KAFKA_TOPIC='messages', VALUE_FORMAT = 'JSON',\n" +
//                    "        TIMESTAMP_FORMAT = 'yyyy-MM-dd HH:mm:ss',\n" +
//                    "        PARTITIONS = 1);",
//        )
//        try {
//            streamCreateQuery.get()
//        } catch (e: Exception) {
//            logger.error("Error creating stream user_messages {}", e.message)
//        }
//        val userSpecificStreamName = "filtered_user_messages_$userId"
//        val userSpecificStream = KSQL.ksqlClient().executeStatement(
//            "CREATE STREAM $userSpecificStreamName AS\n" +
//                    "  SELECT * FROM user_messages\n" +
//                    "  WHERE USER_ID = '$userId';"
//        )
//        try {
//            userSpecificStream.get()
//        } catch (e: Exception) {
//            logger.error("Error creating stream user_messages {}", e.message)
//        }
//
//        val props = Properties()
//        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
//        props[ConsumerConfig.GROUP_ID_CONFIG] = "ksql_filtered_messages_$userId"
//
//        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] =
//            StringDeserializer::class.java.getName()
//        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] =
//            StringDeserializer::class.java.getName()
//        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest" // only read new messages
////        val consumer = KSQL.ksqlClient().streamQuery(
////            "SELECT * FROM $userSpecificStreamName EMIT CHANGES;"
////        ).get()
//
//        val consumer = KafkaConsumer<String, String>(props)
//        consumer.subscribe(listOf(userSpecificStreamName.uppercase()))
//        var count = 0
//        do {
//            val records = consumer.poll(Duration.ofMillis(1000))
//            logger.info("Received {} records", records.count())
//            records.forEach { record ->
//                // Process the received message
//                logger.info("Received message for user {}: {}", userId, record.value())
//                count++
//            }
//
//        } while (count < 10)
//        consumer.close()
//        return ResponseEntity.ok("Started Kafka listener for user $userId")
//    }
//
////    @PostMapping("/stop/{userId}")
////    fun stopKafkaListener(@PathVariable userId: String): ResponseEntity<String> {
////        isConsumerRunning[userId] = false
////        return ResponseEntity.ok("Stopped Kafka listener for user $userId")
////    }
//
//
////    @PostMapping("/start/{userId}")
////    fun startKafkaListener(@PathVariable userId: String): ResponseEntity<String> {
////        if (isConsumerRunning[userId] == true) {
////            return ResponseEntity.ok("Kafka listener already running for user $userId")
////        }
//////        val consumer = kafkaListenerEndpointRegistry.getListenerContainer("consumer")
//////        val container = kafkaListenerContainerFactory.createContainer("messages")
//////        container.set
//////        container.start()
//////        val consumer = MethodKafkaListenerEndpoint<String, Message>()
//////        consumer.setTopics("messages")
//////        consumer.setGroupId(userId)
//////        consumer.setAutoStartup(true)
//////        consumer.setMessageHandlerMethodFactory(DefaultMessageHandlerMethodFactory())
//////        userIdConsumerMap[userId] = consumer
//////        return ResponseEntity.ok("Started Kafka listener for user $userId")
////        val props = Properties()
////        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
////        props[ConsumerConfig.GROUP_ID_CONFIG] = userId
////        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.getName()
////        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.getName()
////
////        val consumer = KafkaConsumer<String, Message>(props)
////        userIdConsumerMap[userId] = consumer
////        consumer.subscribe(listOf("messages"))
////        isConsumerRunning[userId] = true
////        try {
////            while (isConsumerRunning[userId] == true) {
////                val records: ConsumerRecords<String, Message> = consumer.poll(Duration.ofMillis(1000))
////                records.forEach(Consumer<ConsumerRecord<String?, Message?>> { record: ConsumerRecord<String?, Message?> ->
////                    // Process the received message
////                    System.out.printf("Received message for user %s: %s%n", userId, record.value())
////                })
////            }
////        } catch (e: Exception) {
////            consumer.close()
////        }
//////        consumer.use { consumerInstance ->
//////            while (isConsumerRunning[userId] == true) {
//////                val records: ConsumerRecords<String, Message> = consumerInstance.poll(Duration.ofMillis(1000))
//////                records.forEach(Consumer<ConsumerRecord<String?, Message?>> { record: ConsumerRecord<String?, Message?> ->
//////                    // Process the received message
//////                    System.out.printf("Received message for user %s: %s%n", userId, record.value())
//////                })
//////            }
//////        }
////        return ResponseEntity.ok("Started Kafka listener for user $userId")
////    }
////
////    @PostMapping("/stop/{userId}")
////    fun stopKafkaListener(@PathVariable userId: String): ResponseEntity<String> {
////        val consumer = userIdConsumerMap[userId]
////        consumer?.unsubscribe()
////        isConsumerRunning[userId] = false
////        return ResponseEntity.ok("Stopped Kafka listener for user $userId")
////    }
//}


@RestController
@RequestMapping("/kafka")
class KafkaTest(
    private val ksql: KSQL
) {
    @PostMapping("/send/{userId}")
    fun sendKafkaMessage(@PathVariable userId: UUID): ResponseEntity<String> {
        println("Sending message to Kafka for user $userId")
        ksql.produceUserMessage(
            KafkaMessage(
                id = UUID.randomUUID(),
                room_id = UUID.randomUUID(),
                sender_user_id = UUID.randomUUID(),
                receiver_user_id = userId,
                message = "Hello from Kafka",
                type = "text",
                send_at = Date(),
                belongs_to_thread_id = null,
                belongs_to_message_id = null
            )
        )
        return ResponseEntity.ok("Sent message to Kafka for user $userId")
    }

    @PostMapping("/start/{userId}")
    suspend fun startKafkaListener(@PathVariable userId: UUID): ResponseEntity<String> {
        val (value, consumer) = ksql.consumeUserMessages(userId.toString())
        var counter = 0
        value.takeWhile { counter < 10 }.forEach {
            println("Received message for user $userId: $it")
            counter++
        }
        println("Closing consumer for user $userId")
        consumer.close()
        return ResponseEntity.ok("Started Kafka listener for user $userId")
    }
}
