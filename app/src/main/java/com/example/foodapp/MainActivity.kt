package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnSwitchToAdminRegistration: Button
    private lateinit var tvSwitchToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация Firebase Auth и Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Инициализация элементов интерфейса
        btnSwitchToAdminRegistration = findViewById(R.id.btnSwitchToAdminRegistration)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)
        tvSwitchToLogin = findViewById(R.id.tvSwitchToLogin)

        // Установка слушателей
        btnRegister.setOnClickListener { registerUser() }
        tvSwitchToLogin.setOnClickListener {
            // Переход на экран логина
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btnSwitchToAdminRegistration.setOnClickListener {
            // Переход на экран регистрации администратора
            startActivity(Intent(this, AdminRegistrationActivity::class.java))
        }

        // Проверка на наличие сохраненного типа пользователя
        checkUserType()
    }

    private fun checkUserType() {
        val sharedPreferences = getSharedPreferences("UserTypePref", MODE_PRIVATE)
        val userType = sharedPreferences.getString("userType", null)

        if (userType == "admin") {
            // Переход на главный экран для администратора
            startActivity(Intent(this, AdminHomeActivity::class.java))
            finish() // Закрыть MainActivity
        } else if (userType == "buyer") {
            // Переход на главный экран для покупателя
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Закрыть MainActivity
        }
    }

    private fun registerUser() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val name = etName.text.toString()
        val phone = etPhone.text.toString()
        val address = etAddress.text.toString()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = User(userId, email, name, phone, address)
                    database.child(userId!!).setValue(user)
                        .addOnCompleteListener {
                            // Сохранение типа пользователя в SharedPreferences
                            val sharedPreferences = getSharedPreferences("UserTypePref", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("userType", "buyer") // Сохраняем тип пользователя как покупатель
                            editor.apply()

                            Toast.makeText(this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show()
                            // Переход на главный экран
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish() // Закрыть MainActivity
                        }
                } else {
                    Toast.makeText(this, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

// Данные пользователя
data class User(val userId: String?, val email: String, val name: String, val phone: String, val address: String)