package com.adarsh.connects_you_server.listeners

import com.adarsh.connects_you_server.models.enums.MessageTypeEnum
import com.adarsh.connects_you_server.models.enums.RoomTypeEnum
import com.adarsh.connects_you_server.models.enums.RoomUserRoleEnum
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class JpaListener(
    private val createAndAssignType: CreateAndAssignType,
) {
    @PostConstruct
    private fun run() {
        createAndAssignType.createType()
    }
}

@Component
class CreateAndAssignType {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private fun createTypeString(
        enumClass: Class<out Enum<*>>,
        typeName: String,
        afterCreateTypeAlterCommand: String = "",
    ): String {
        val enumString = enumClass.enumConstants.joinToString(", ") { "'${it.name}'" }
        return """
            DO ${'$'}${'$'}
            DECLARE
                new_values TEXT[] \:= ARRAY[$enumString];
                existing_values TEXT[];
                value TEXT;
        
            BEGIN
                CREATE TYPE $typeName AS ENUM ($enumString);
                $afterCreateTypeAlterCommand
            EXCEPTION
                WHEN duplicate_object THEN
                    SELECT ARRAY(SELECT enumlabel FROM pg_enum WHERE enumtypid = '$typeName'\:\:regtype)
                    INTO existing_values;
                    
                    FOREACH value IN ARRAY new_values
                        LOOP
                            IF value = ANY(existing_values) THEN
                            ELSE
                                EXECUTE format('ALTER TYPE $typeName ADD VALUE %L', value);
                            END IF;
                        END LOOP;
            END ${'$'}${'$'};
        """.trimIndent()
    }

    @Transactional
    fun createType() {
        entityManager.createNativeQuery(
            createTypeString(
                MessageTypeEnum::class.java,
                "message_type_enum",
                """
                  ALTER TABLE messages ALTER COLUMN type TYPE message_type_enum USING type\:\:message_type_enum;
                  ALTER table messages drop constraint messages_type_check;
                """.trimIndent(),
            )
        )?.executeUpdate()
        entityManager.createNativeQuery(
            createTypeString(
                RoomTypeEnum::class.java,
                "room_type_enum",
                """
                    ALTER TABLE rooms ALTER COLUMN type TYPE room_type_enum USING type\:\:room_type_enum;
                    ALTER table rooms drop constraint rooms_type_check;
                """.trimIndent()
            )
        )?.executeUpdate()
        entityManager.createNativeQuery(
            createTypeString(
                RoomUserRoleEnum::class.java,
                "room_user_role_enum",
                """
                    ALTER TABLE room_users ALTER COLUMN user_role TYPE room_user_role_enum USING user_role\:\:room_user_role_enum;
                    ALTER table room_users drop constraint room_users_user_role_check;
                """.trimIndent()
            )
        )?.executeUpdate()
    }
}