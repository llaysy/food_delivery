package com.example.foodapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvSwitchToRegistration: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверка, если пользователь уже авторизован, перенаправляем на HomeActivity
        val preferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        if (preferences.getBoolean("isLoggedIn", false)) {
            navigateToHome(preferences.getString("userType", "buyer"))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // Инициализация Firebase Auth и Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Инициализация элементов интерфейса
        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvSwitchToRegistration = findViewById(R.id.tvSwitchToRegistration)

        // Установка слушателей
        btnLogin.setOnClickListener { loginUser() }
        tvForgotPassword.setOnClickListener { resetPassword() }
        tvSwitchToRegistration.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("LoginActivity", "Attempting to log in user with email: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "User signed in successfully.")
                    val user = auth.currentUser
                    user?.let {
                        checkUserRole(it.uid) // Передаем UID для проверки роли
                    } ?: run {
                        Log.e("LoginActivity", "Ошибка: пользователь не найден")
                        Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("LoginActivity", "Ошибка входа: ${task.exception?.message}")
                    Toast.makeText(this, "Ошибка входа: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRole(uid: String) {
        // Проверяем наличие пользователя в узле Admins
        database.parent?.child("Admins")?.child(uid)?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    // Это администратор
                    Toast.makeText(this, "Успешный вход как администратор", Toast.LENGTH_SHORT).show()
                    saveUserType("admin")
                    startActivity(Intent(this, AdminHomeActivity::class.java))
                    finish()
                } else {
                    // Проверяем наличие пользователя в узле Users
                    database.child(uid).get().addOnCompleteListener { userTask ->
                        if (userTask.isSuccessful) {
                            if (userTask.result.exists()) {
                                // Это покупатель
                                Toast.makeText(this, "Успешный вход как покупатель", Toast.LENGTH_SHORT).show()
                                saveUserType("buyer")
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                Log.e("LoginActivity", "Пользователь не найден в узле Users.")
                                Toast.makeText(this, "Пользователь не найден.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("LoginActivity", "Ошибка при проверке роли пользователя: ${userTask.exception?.message}")
                            Toast.makeText(this, "Ошибка при проверке роли пользователя: ${userTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Log.e("LoginActivity", "Ошибка при проверке роли администратора: ${task.exception?.message}")
                Toast.makeText(this, "Ошибка при проверке роли администратора: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserType(userType: String) {
        val preferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        preferences.edit().putBoolean("isLoggedIn", true).putString("userType", userType).apply()
    }

    private fun resetPassword() {
        val email = etLoginEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите ваш email", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Ссылка для сброса пароля отправлена на email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Ошибка: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome(userType: String?) {
        if (userType == "admin") {
            startActivity(Intent(this, AdminHomeActivity::class.java))
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}