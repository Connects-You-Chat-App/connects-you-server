import com.adarsh.chat_server.configs.KSQL
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ApplicationListener(
    private val KSQL: KSQL
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun runAfterStartup() {
        logger.info("Application started")
        KSQL.ksqlClient().executeQuery(
            "CREATE STREAM  user_messages (USER_ID VARCHAR, MESSAGE VARCHAR)\n WITH (KAFKA_TOPIC='messages', VALUE_FORMAT='JSON');"
        ).thenAccept { result ->
            logger.info("After creating stream user_messages {}", result)
        }.runCatching {
            logger.error("Error creating stream user_messages {}", this)
        }
    }
}
