package com.example.foodapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var btnLogoutUser: Button
    private lateinit var btnMessage: Button
    private lateinit var btnFavorite: Button
    private lateinit var btnWishlist: Button
    private lateinit var btnHelp: Button
    private lateinit var icAvatar: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var tvUserName: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверка, если пользователь не авторизован, перенаправляем на LoginActivity
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_home)

        // Сохранение последней активности
        val preferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        preferences.edit().putString("lastActivity", "HomeActivity").apply()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        btnLogoutUser = findViewById(R.id.btnLogoutUser)
        icAvatar = findViewById(R.id.icAvatar)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserRole = findViewById(R.id.tvUserRole)

        btnLogoutUser.setOnClickListener {
            logout()
        }

        icAvatar.setOnClickListener {
            showUserInfo()
        }

        loadUserInfo()
    }

    private fun loadUserInfo() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child(userId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.exists()) {
                        val userName = task.result.child("name").value.toString()
                        tvUserName.text = userName
                    } else {
                        Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Ошибка загрузки данных: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logout() {
        auth.signOut() // Выход из аккаунта

        // Сброс состояния входа
        val preferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        preferences.edit().putBoolean("isLoggedIn", false).apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Закрыть HomeActivity
    }

    private fun showUserInfo() {
        Toast.makeText(this, "Информация о пользователе", Toast.LENGTH_SHORT).show()
    }
}

//llaysy