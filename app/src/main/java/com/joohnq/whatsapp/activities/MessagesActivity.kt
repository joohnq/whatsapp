package com.joohnq.whatsapp.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import com.joohnq.whatsapp.adapters.MessageAdapter
import com.joohnq.whatsapp.auth.UserAuth
import com.joohnq.whatsapp.databinding.ActivityMessagesBinding
import com.joohnq.whatsapp.db.UserDatabase
import com.joohnq.whatsapp.models.ChatModel
import com.joohnq.whatsapp.models.UserModel
import com.joohnq.whatsapp.util.showMessage
import com.squareup.picasso.Picasso


class MessagesActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMessagesBinding.inflate(layoutInflater) }
    private var recipientUserData: UserModel? = null
    private var senderUserData: UserModel? = null
    private val auth by lazy { UserAuth() }
    private var senderUserDataUid: String? = auth.getUserLoggedId()
    private val picasso by lazy { Picasso.get() }
    private val db by lazy { UserDatabase() }
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        manageUsersData()
        initToolbar()
        initEventsClick()
        initReciclerView()
        initListeners()
    }

    private fun initReciclerView() {
        with(binding) {
            messageAdapter = MessageAdapter()
            rvMessages.adapter = messageAdapter
            rvMessages.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun initListeners() {
        listenerRegistration = db.messagesListener(
            senderUserDataUid!!,
            recipientUserData!!.id,
            onSuccess = { messageList ->
                messageAdapter.addList(messageList)
            },
            onFailure = { exception ->
                Log.i("listener_info", exception.toString())
            }
        )
    }

    private fun initEventsClick() {
        with(binding) {
            fabSend.setOnClickListener {
                val message = tietMessage.text.toString()
                if (message.isNotEmpty()) {
                    Log.i("user_data", senderUserData.toString())

                    val chatSender = ChatModel(
                        null,
                        recipientUserData!!.profilePhoto,
                        message,
                        recipientUserData!!.name,
                        recipientUserData!!.id,
                        senderUserData!!.id
                    )
                    db.saveChat(
                        chatSender,
                        onFailure = { exception ->
                            Log.i("chat_save", exception.toString())
                        }
                    )
                    val chatRecipient = ChatModel(
                        null,
                        senderUserData!!.profilePhoto,
                        message,
                        senderUserData!!.name,
                        senderUserData!!.id,
                        recipientUserData!!.id,
                    )
                    db.saveChat(
                        chatRecipient,
                        onFailure = { exception ->
                            Log.i("chat_save", exception.toString())
                        }
                    )

                    db.saveMessage(
                        message,
                        senderUserDataUid!!,
                        recipientUserData!!.id,
                        onFailure = { exception ->
                            showMessage("Erro ao enviar menssagem")
                            Log.i("chat_save", exception.toString())
                        }
                    )
                    tietMessage.setText("")
                }
            }
        }
    }

    private fun manageUsersData() {
        db.getUser(
            onSuccess = { user ->
                senderUserData = user
                Log.i("user_data", user.toString())
            },
            onFailure = { exception ->
                Log.i("user_data", exception.toString())
            }
        )

        val extras = intent.extras
        if (extras != null) {
            recipientUserData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("recipientData", UserModel::class.java)
            } else {
                extras.getParcelable("recipientData")
            }
        }
    }

    private fun initToolbar() {
        val toolbar = binding.mtMessageToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)

            if (recipientUserData != null) {
                with(binding) {
                    val name: String? = recipientUserData!!.name
                    val profilePhoto: String? = recipientUserData!!.profilePhoto
                    tvUserNameToolbar.text = name
                    if (profilePhoto != null) {
                        picasso.load(profilePhoto).into(ivUserPhotoToolbar)
                    }
                }
            }
        }
    }
}