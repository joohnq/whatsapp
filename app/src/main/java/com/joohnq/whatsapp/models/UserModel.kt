package com.joohnq.whatsapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val id: String = "",
    val name: String? = "",
    val email: String = "",
    val profilePhoto: String? = ""
): Parcelable
