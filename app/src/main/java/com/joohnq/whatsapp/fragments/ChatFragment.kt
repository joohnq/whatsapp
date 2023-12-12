package com.joohnq.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import com.joohnq.whatsapp.activities.MessagesActivity
import com.joohnq.whatsapp.adapters.ChatAdapter
import com.joohnq.whatsapp.databinding.FragmentChatBinding
import com.joohnq.whatsapp.db.UserDatabase
import com.joohnq.whatsapp.models.UserModel

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val db by lazy { UserDatabase() }
    private lateinit var snapshotEvent: ListenerRegistration
    private val chatAdapter by lazy {
        ChatAdapter { chat ->
            val user = UserModel(
                id = chat.uidUserRecipient,
                name = chat.name,
                profilePhoto = chat.photo,
            )
            Log.i("user_info_2",user.toString() )
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra("recipientData", user)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        addChatListener()
    }

    private fun addChatListener() {
        snapshotEvent = db.getChats(
            onSuccess = { listChats ->
                Log.i("users_info", listChats.toString())
                if (listChats.isNotEmpty()) {
                    chatAdapter.addList(listChats)
                }
            },
            onFailure = {exception ->
                Log.i("users_info", exception.toString())
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        with(binding) {
            rvChats.adapter = chatAdapter
            rvChats.layoutManager = LinearLayoutManager(context)
            rvChats.addItemDecoration(
                DividerItemDecoration(
                    context, LinearLayoutManager.VERTICAL
                )
            )
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }
}