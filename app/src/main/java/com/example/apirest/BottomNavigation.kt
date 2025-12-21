package com.example.apirest

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

// Echo con IA (hasta return@setOnItemSelectedListener true)
class BottomNavigation(private val context: Context) {

    fun setup(bottomNavigationView: BottomNavigationView, currentItemId: Int) {
        bottomNavigationView.selectedItemId = currentItemId


        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == currentItemId) {
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {
                R.id.nav_profile -> {
                    context.startActivity(Intent(context, MainMenu::class.java))
                    true
                }
                R.id.nav_characters -> {
                    context.startActivity(Intent(context, Character::class.java))
                    true
                }
                R.id.nav_weapons -> {
                    context.startActivity(Intent(context, Weapons::class.java))
                    true
                }
                R.id.nav_chests -> {
                    context.startActivity(Intent(context, Chest::class.java))
                    true
                }
                else -> false
            }
        }
    }
}