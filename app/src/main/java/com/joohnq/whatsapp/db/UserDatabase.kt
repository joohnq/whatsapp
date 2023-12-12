package com.joohnq.whatsapp.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.joohnq.whatsapp.auth.UserAuth
import com.joohnq.whatsapp.models.ChatModel
import com.joohnq.whatsapp.models.MessageModel
import com.joohnq.whatsapp.models.UserModel

class UserDatabase {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { UserAuth() }

    companion object {
        const val DATABASE_COLLECTION_USER = "users"
        const val DATABASE_COLLECTION_MESSAGES = "messages"
        const val DATABASE_COLLECTION_CHATS = "chats"
        const val DATABASE_COLLECTION_LASTCHATS = "last_chats"
    }

    fun createUser(
        name: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.getUserLoggedId()
        if (uid != null) {
            val data = UserModel(
                uid,
                name,
                email,
                null
            )
            db
                .collection(DATABASE_COLLECTION_USER)
                .document(uid)
                .set(data)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }

    fun getUser(
        onSuccess: (UserModel) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val uid = auth.getUserLoggedId()
        if (uid != null) {
            db
                .collection(DATABASE_COLLECTION_USER)
                .document(uid)
                .get()
                .addOnSuccessListener {
                    val data = it.data
                    if (data != null) {
                        val user = UserModel(
                            uid,
                            data["name"].toString(),
                            data["email"].toString(),
                            data["profilePhoto"].toString()
                        )
                        onSuccess(user)
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }

    fun saveUserProfilePhoto(
        url: String,
        onSuccess: (String) -> Unit,
        onFailure: (String, Exception) -> Unit
    ) {
        val userUid: String? = auth.getUserLoggedId()
        if (userUid != null) {
            db
                .collection(DATABASE_COLLECTION_USER)
                .document(userUid)
                .update("profilePhoto", url)
                .addOnSuccessListener {
                    onSuccess("Imagem atualizada no banco de dados")
                }
                .addOnFailureListener { exception ->
                    onFailure("Erro ao atualizar no banco de dados", exception)
                }
        }
    }

    fun updateUserName(
        name: String,
        onSuccess: (String) -> Unit,
        onFailure: (String, Exception) -> Unit
    ) {
        val userUid: String? = auth.getUserLoggedId()
        if (userUid != null) {
            db
                .collection(DATABASE_COLLECTION_USER)
                .document(userUid)
                .update("name", name)
                .addOnSuccessListener {
                    onSuccess("Nome atualizado no banco de dados")
                }
                .addOnFailureListener { exception ->
                    onFailure("Erro ao atualizar nome no banco de dados", exception)
                }
        }
    }

    fun getUsers(
        onSuccess: (List<UserModel?>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return db
            .collection(DATABASE_COLLECTION_USER)
            .addSnapshotListener { querySnapshot, error ->
                if (error == null) {
                    val documents = querySnapshot?.documents
                    val users = mutableListOf<UserModel>()
                    documents?.forEach { documentSnapshot ->
                        val user = documentSnapshot.toObject(UserModel::class.java)
                        if (user != null) {
                            val userUidLogged = auth.getUserLoggedId()
                            if (userUidLogged != null && userUidLogged != user.id) {
                                users.add(user)
                            }
                        }
                    }
                    onSuccess(users)
                } else {
                    onFailure(error)
                }

            }
    }

    fun getChats(
        onSuccess: (List<ChatModel?>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        val idUserLogged = auth.getUserLoggedId() ?: ""

        return db
            .collection(DATABASE_COLLECTION_CHATS)
            .document(idUserLogged)
            .collection(DATABASE_COLLECTION_LASTCHATS)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error == null) {
                    val documents = querySnapshot?.documents
                    val chats = mutableListOf<ChatModel>()
                    documents?.forEach { documentSnapshot ->
                        val chat = documentSnapshot.toObject(ChatModel::class.java)
                        if (chat != null) {
                            chats.add(chat)
                        }
                    }
                    onSuccess(chats)
                } else {
                    onFailure(error)
                }
            }
    }

    fun saveMessage(
        messageText: String,
        userUidSender: String,
        userUidRecipient: String,
        onFailure: (Exception) -> Unit
    ) {
        val message = MessageModel(userUidSender, messageText, null)
        db
            .collection(DATABASE_COLLECTION_MESSAGES)
            .document(userUidSender)
            .collection(userUidRecipient)
            .add(message)
            .addOnFailureListener { exception ->
                onFailure(exception)
            }

        db
            .collection(DATABASE_COLLECTION_MESSAGES)
            .document(userUidRecipient)
            .collection(userUidSender)
            .add(message)
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun messagesListener(
        userUidSender: String,
        userUidRecipient: String,
        onSuccess: (List<MessageModel>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return db
            .collection(DATABASE_COLLECTION_MESSAGES)
            .document(userUidSender)
            .collection(userUidRecipient)
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, error ->
                val documents = querySnapshot?.documents
                val messageList = mutableListOf<MessageModel>()
                if (error != null) {
                    onFailure(error)
                } else {
                    documents?.forEach { documentSnapshot ->
                        val message = documentSnapshot.toObject(MessageModel::class.java)
                        if (message != null) {
                            messageList.add(message)
                            Log.i("messages_info", message.toString())
                        }
                    }
                }

                if (messageList.isNotEmpty()) {
                    onSuccess(messageList)
                }
            }
    }

    fun saveChat(
        chat: ChatModel,
        onFailure: (Exception) -> Unit
    ) {
        db
            .collection(DATABASE_COLLECTION_CHATS)
            .document(chat.uidUserSender)
            .collection(DATABASE_COLLECTION_LASTCHATS)
            .document(chat.uidUserRecipient)
            .set(chat)
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}