//package com.adarsh.connects_you_server.controllers
//
//import com.adarsh.connects_you_server.configs.KSQL
//import com.adarsh.connects_you_server.models.common.KafkaMessage
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//import java.util.*
//
//@RestController
//@RequestMapping("/kafka")
//class KafkaTest(
//    private val ksql: KSQL
//) {
//    @PostMapping("/send/{userId}")
//    fun sendKafkaMessage(@PathVariable userId: UUID): ResponseEntity<String> {
//        println("Sending message to Kafka for user $userId")
//        ksql.produceUserMessage(
//            KafkaMessage(
//                id = UUID.randomUUID(),
//                room_id = UUID.randomUUID(),
//                sender_user_id = UUID.randomUUID(),
//                receiver_user_id = userId,
//                message = "Hello from Kafka",
//                type = "text",
//                send_at = Date(),
//                belongs_to_thread_id = null,
//                belongs_to_message_id = null
//            )
//        )
//        return ResponseEntity.ok("Sent message to Kafka for user $userId")
//    }
//
//    @PostMapping("/start/{userId}/{deviceId}")
//    suspend fun startKafkaListener(@PathVariable userId: UUID, @PathVariable deviceId: String): ResponseEntity<String> {
//        val (value, consumer) = ksql.consumeUserMessages(userId.toString(), deviceId)
//        var counter = 0
//        value.takeWhile { counter < 10 }.forEach {
//            println("Received message from Kafka for user $userId: $it")
//            counter++
//        }
//        println("Closing consumer for user $userId")
//        consumer.close()
//        return ResponseEntity.ok("Started Kafka listener for user $userId")
//    }
//}