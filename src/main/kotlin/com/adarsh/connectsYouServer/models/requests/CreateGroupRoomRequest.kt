package com.adarsh.connectsYouServer.models.requests

data class CreateGroupRoomRequest(
    val name: String?,
    val description: String?,
    val logoUrl: String?,
    val selfEncryptedRoomSecretKey: String,
    val otherUsersEncryptedRoomSecretKeys: List<OtherUserEncryptedSharedKey>,
) {
    data class OtherUserEncryptedSharedKey(
        val userId: String,
        val encryptedRoomSecretKey: String,
    )
}