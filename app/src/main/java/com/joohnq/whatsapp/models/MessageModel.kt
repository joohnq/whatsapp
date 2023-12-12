package com.joohnq.whatsapp.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class MessageModel(
    val idUserSender: String = "",
    val message: String = "",
    @ServerTimestamp
    val date: Date? = null
)
