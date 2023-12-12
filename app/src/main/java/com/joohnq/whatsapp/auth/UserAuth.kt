package com.joohnq.whatsapp.auth

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.joohnq.whatsapp.db.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserAuth {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { UserDatabase() }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String, Exception) -> Unit
    ) = withContext(Dispatchers.IO) {
        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess("Sucesso ao criar usuário")
                db.createUser(
                    name,
                    email,
                    onSuccess = {
                        onSuccess("Sucesso ao salvar usuário no db")
                    },
                    onFailure = {}
                )
            }
            .addOnFailureListener { exception ->
                onFailure("Erro ao criar usuário", exception)
            }
    }

    suspend fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = withContext(Dispatchers.IO) {
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun getUserLoggedId(): String? {
        val user = auth.currentUser
        return user?.uid ?: ""
    }

    fun hasUserLogged(): Boolean {
        val user = auth.currentUser
        return user != null
    }
}