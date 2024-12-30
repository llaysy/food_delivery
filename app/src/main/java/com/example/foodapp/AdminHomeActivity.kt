package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var btnLogoutAdmin: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)

        auth = FirebaseAuth.getInstance()

        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin)
        btnLogoutAdmin.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        auth.signOut() // Выход из аккаунта
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Закрыть AdminHomeActivity
    }
}