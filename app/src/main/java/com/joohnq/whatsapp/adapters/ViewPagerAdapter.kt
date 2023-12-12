package com.joohnq.whatsapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.joohnq.whatsapp.fragments.ChatFragment
import com.joohnq.whatsapp.fragments.ContactsFragment

class ViewPagerAdapter(
    private val tabs: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return ContactsFragment()
        }
        return ChatFragment()
    }
}