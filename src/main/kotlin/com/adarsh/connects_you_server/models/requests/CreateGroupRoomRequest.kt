package com.adarsh.connects_you_server.models.requests


data class CreateGroupRoomRequest(
    val name: String?,
    val description: String?,
    val logoUrl: String?,
    val selfEncryptedRoomSecretKey: String,
    val otherUsersEncryptedRoomSecretKeys: List<OtherUserEncryptedSharedKey>
) {
    data class OtherUserEncryptedSharedKey(
        val userId: String,
        val encryptedRoomSecretKey: String
    )
}