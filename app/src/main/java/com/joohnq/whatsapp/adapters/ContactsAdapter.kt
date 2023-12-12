package com.joohnq.whatsapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.firestore.auth.User
import com.joohnq.whatsapp.databinding.ItemContactBinding
import com.joohnq.whatsapp.models.UserModel
import com.squareup.picasso.Picasso

class ContactsAdapter(
    private val onClick: (UserModel) -> Unit
) : Adapter<ContactsAdapter.ContactsViewHolder>() {
    private var contactList = emptyList<UserModel?>()

    @SuppressLint("NotifyDataSetChanged")
    fun addList(list: List<UserModel?>) {
        contactList = list
        notifyDataSetChanged()
    }

    inner class ContactsViewHolder(private val binding: ItemContactBinding) :
        ViewHolder(binding.root) {
        fun bind(user: UserModel) {
            with(binding) {
                tvContactItemName.text = user.name
                Picasso.get().load(user.profilePhoto).into(ivContactItemProfilePhoto)
                clContactItem.setOnClickListener {
                    onClick(user)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val user = contactList[position]
        if (user != null) {
            holder.bind(user)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = ItemContactBinding.inflate(layoutInflater, parent, false)
        return ContactsViewHolder(itemView)
    }

    override fun getItemCount(): Int = contactList.size

}