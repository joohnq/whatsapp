package com.joohnq.whatsapp.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.joohnq.whatsapp.auth.UserAuth

class UserStorage {
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val auth by lazy { UserAuth() }

    fun updateProfilePhoto(
        uri: Uri,
        onSuccess: (String, String) -> Unit,
        onFailure: (String, Exception) -> Unit,
    ) {
        val userUid = auth.getUserLoggedId()
        if (userUid != null) {
            storage
                .getReference("photos")
                .child("users")
                .child(userUid)
                .child("profilePhoto")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener {url ->
                        onSuccess("Sucesso ao salvar imagem no Storage", url.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure("Erro ao salvar imagem no Storage", exception)
                }
        }
    }
}