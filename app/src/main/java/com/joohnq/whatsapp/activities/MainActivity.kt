package com.joohnq.whatsapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.joohnq.whatsapp.R
import com.joohnq.whatsapp.adapters.ViewPagerAdapter
import com.joohnq.whatsapp.auth.UserAuth
import com.joohnq.whatsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val auth by lazy { UserAuth() }
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        initNavigationTabs()
    }

    private fun initNavigationTabs() {
        with(binding) {
            val tabLayout = tlMain
            val viewPager = vpMain
            tabLayout.isTabIndicatorFullWidth = true

            val tabs = listOf("Conversas", "Contatos")
            viewPager.adapter = ViewPagerAdapter(tabs,supportFragmentManager, lifecycle)

            TabLayoutMediator(tabLayout, viewPager){ tab, position ->
                tab.text = tabs[position]
            }.attach()

        }
    }

    private fun initToolbar() {
        val toolbar = binding.includeToolbar.mtMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Whatsapp"
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.miProfile -> {
                            val intent = Intent(context, ProfileActivity::class.java)
                            startActivity(intent)
                        }

                        R.id.miExit -> {
                            singOut()
                        }
                    }
                    return true
                }

            })
        }
    }

    private fun singOut() {
        AlertDialog
            .Builder(context)
            .setTitle("Deslogar?")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Cancelar") { _, _ -> }
            .setPositiveButton("Confirmar") { _, _ ->
                auth.logoutUser()
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
            .create()
            .show()
    }
}