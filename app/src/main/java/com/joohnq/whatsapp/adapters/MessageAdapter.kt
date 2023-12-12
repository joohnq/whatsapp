package com.joohnq.whatsapp.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.joohnq.whatsapp.auth.UserAuth
import com.joohnq.whatsapp.databinding.ItemMessagesRecipientBinding
import com.joohnq.whatsapp.databinding.ItemMessagesSenderBinding
import com.joohnq.whatsapp.models.MessageModel

class MessageAdapter() : Adapter<ViewHolder>() {
    private var messageList = emptyList<MessageModel?>()
    private val auth by lazy { UserAuth() }

    companion object {
        const val SENDER_TYPE = 0
        const val RECIPIENT_TYPE = 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(list: List<MessageModel?>) {
        messageList = list
        notifyDataSetChanged()
    }

    class MessageSenderViewHolder(private val binding: ItemMessagesSenderBinding) :
        ViewHolder(binding.root) {
        companion object {
            fun inflate(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessagesSenderBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return MessageSenderViewHolder(itemView)
            }
        }

        fun bind(message: MessageModel) {
            binding.tvMessageSender.text = message.message
        }
    }

    class MessageRecipientViewHolder(private val binding: ItemMessagesRecipientBinding) :
        ViewHolder(binding.root) {
        companion object {
            fun inflate(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessagesRecipientBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return MessageRecipientViewHolder(itemView)
            }
        }

        fun bind(message: MessageModel) {
            binding.tvMessageRecipient.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == SENDER_TYPE) {
            MessageSenderViewHolder.inflate(parent)
        } else {
            MessageRecipientViewHolder.inflate(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        val userUidLogged = auth.getUserLoggedId()
        Log.i("user_logged", userUidLogged.toString())
        return if (userUidLogged == message?.idUserSender ) {
            SENDER_TYPE
        } else {
            RECIPIENT_TYPE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        if (message != null) {
            when (holder) {
                is MessageSenderViewHolder -> holder.bind(message)
                is MessageRecipientViewHolder -> holder.bind(message)
            }

        }
    }

    override fun getItemCount(): Int = messageList.size
}