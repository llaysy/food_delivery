package com.example.foodapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var btnLogoutAdmin: Button
    private lateinit var btnManageProducts: Button
    private lateinit var btnCategories: Button // Новая кнопка для управления категориями
    private lateinit var auth: FirebaseAuth
    private lateinit var tvWelcomeMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)

        auth = FirebaseAuth.getInstance()

        // Инициализация виджетов
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin)
        btnManageProducts = findViewById(R.id.btnManageProducts)
        btnCategories = findViewById(R.id.btnCategories) // Инициализация новой кнопки
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)

        // Обработчик кнопки выхода
        btnLogoutAdmin.setOnClickListener {
            logout()
        }

        // Обработчик кнопки управления продуктами
        btnManageProducts.setOnClickListener {
            openProductManagement()
        }

        // Обработчик кнопки управления категориями
        btnCategories.setOnClickListener {
            openCategoriesManagement() // Новый метод для открытия управления категориями
        }

        // Установка приветственного сообщения
        setupWelcomeMessage()
    }

    private fun setupWelcomeMessage() {
        val user = auth.currentUser
        val welcomeMessage = if (user != null) {
            "Добро пожаловать, ${user.displayName ?: user.email}!"
        } else {
            "Добро пожаловать на главный экран администратора!"
        }
        tvWelcomeMessage.text = welcomeMessage
    }

    private fun logout() {
        auth.signOut() // Выход из аккаунта

        // Сброс состояния входа
        val preferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        preferences.edit().putBoolean("isLoggedIn", false).apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Закрыть AdminHomeActivity
    }

    private fun openProductManagement() {
        val intent = Intent(this, AdminProductsActivity::class.java)
        startActivity(intent) // Переход к управлению продуктами
    }

    private fun openCategoriesManagement() {
        val intent = Intent(this, AdminCategoriesActivity::class.java)
        startActivity(intent) // Переход к управлению категориями
    }
}