package com.yogaap.onlineshop.Helper

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yogaap.onlineshop.R
import com.yogaap.onlineshop.activity.ListActivity
import com.yogaap.onlineshop.activity.LoginActivity
import com.yogaap.onlineshop.activity.ProfileActivity

fun BottomNavigationView.setupNavigation(context: Context) {
    val sessionManager = SessionManager(context)

    findViewById<View>(R.id.navExplorerLayout).setOnClickListener{
        val intent = Intent(context, ListActivity::class.java).apply {
            putExtra("TYPE", "RECOMMEND")
        }
        context.startActivity(intent)
    }

    findViewById<View>(R.id.navCartLayout).setOnClickListener{
        if (sessionManager.getUserSession() == null) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        } else {
            Toast.makeText(context, "Cart", Toast.LENGTH_SHORT).show()
        }
    }

    findViewById<View>(R.id.navFavoriteLayout).setOnClickListener{
        if (sessionManager.getUserSession() == null) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        } else {
            Toast.makeText(context, "Favorite", Toast.LENGTH_SHORT).show()
        }
    }

    findViewById<View>(R.id.navOrderLayout).setOnClickListener{
        if (sessionManager.getUserSession() == null) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        } else {
            Toast.makeText(context, "Order", Toast.LENGTH_SHORT).show()
        }
    }

    findViewById<View>(R.id.navProfileLayout).setOnClickListener{
        if (sessionManager.getUserSession() == null) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        } else {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }
    }
}
