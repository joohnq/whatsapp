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
import com.joohnq.whatsapp.adapters.ContactsAdapter
import com.joohnq.whatsapp.databinding.FragmentContactsBinding
import com.joohnq.whatsapp.db.UserDatabase

class ContactsFragment : Fragment() {
    private lateinit var binding: FragmentContactsBinding
    private val db by lazy { UserDatabase() }
    private lateinit var snapshotEvent: ListenerRegistration
    private val contactAdapter by lazy {
        ContactsAdapter {user ->
            val intent = Intent(context, MessagesActivity::class.java )
            intent.putExtra("recipientData", user)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        with(binding) {
            rvContacts.adapter = contactAdapter
            rvContacts.layoutManager = LinearLayoutManager(context)
            rvContacts.addItemDecoration(
                DividerItemDecoration(
                    context, LinearLayoutManager.VERTICAL
                )
            )
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addListenerContacts()
    }

    private fun addListenerContacts() {
        snapshotEvent = db.getUsers(
            onSuccess = { listUsers ->
                if (listUsers.isNotEmpty()) {
                    contactAdapter.addList(listUsers)
                }
            },
            onFailure = {exception ->
                Log.i("users_info", exception.toString())
            },
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }
}