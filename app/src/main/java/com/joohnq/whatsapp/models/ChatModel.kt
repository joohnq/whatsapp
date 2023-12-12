package com.joohnq.whatsapp.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ChatModel(
    @ServerTimestamp
    val date: Date? = null,
    val photo: String? = "",
    val lastMessage: String = "",
    val name: String? = "",
    val uidUserRecipient: String = "",
    val uidUserSender: String = "",
)
