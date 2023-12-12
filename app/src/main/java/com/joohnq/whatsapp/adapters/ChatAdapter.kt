package com.joohnq.whatsapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.joohnq.whatsapp.databinding.ItemChatsBinding
import com.joohnq.whatsapp.models.ChatModel
import com.squareup.picasso.Picasso

class ChatAdapter(
    private val onClick: (ChatModel) -> Unit
): Adapter<ChatAdapter.ChatViewHolder>() {
    private var chatList = emptyList<ChatModel?>()

    @SuppressLint("NotifyDataSetChanged")
    fun addList(list: List<ChatModel?>) {
        chatList = list
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(private val binding: ItemChatsBinding) :
        ViewHolder(binding.root) {
        fun bind(chat: ChatModel) {
            with(binding) {
                tvChatItemName.text = chat.name
                tvChatItemLastMessage.text = chat.lastMessage
                Picasso.get().load(chat.photo).into(ivChatItemProfilePhoto)
                clChatItem.setOnClickListener {
                    onClick(chat)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        if (chat != null) {
            holder.bind(chat)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = ItemChatsBinding.inflate(layoutInflater, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun getItemCount(): Int = chatList.size
}