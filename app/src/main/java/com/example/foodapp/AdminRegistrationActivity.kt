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

class AdminRegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var etAdminEmail: EditText
    private lateinit var etAdminFullName: EditText
    private lateinit var etAdminId: EditText
    private lateinit var etAdminPhone: EditText
    private lateinit var etAdminPassword: EditText
    private lateinit var btnRegisterAdmin: Button
    private lateinit var tvSwitchToLoginAdmin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_registration)

        // Инициализация Firebase Auth и Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Admins")

        // Инициализация элементов интерфейса
        etAdminEmail = findViewById(R.id.etAdminEmail)
        etAdminFullName = findViewById(R.id.etAdminFullName)
        etAdminId = findViewById(R.id.etAdminId)
        etAdminPhone = findViewById(R.id.etAdminPhone)
        etAdminPassword = findViewById(R.id.etAdminPassword)
        btnRegisterAdmin = findViewById(R.id.btnRegisterAdmin)
        tvSwitchToLoginAdmin = findViewById(R.id.tvSwitchToLoginAdmin)

        // Установка слушателя на кнопку регистрации
        btnRegisterAdmin.setOnClickListener { registerAdmin() }

        // Установка слушателя на текст для перехода на экран логина
        tvSwitchToLoginAdmin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerAdmin() {
        // Получение данных из полей ввода
        val email = etAdminEmail.text.toString()
        val password = etAdminPassword.text.toString()
        val fullName = etAdminFullName.text.toString()
        val workerId = etAdminId.text.toString()
        val phone = etAdminPhone.text.toString()

        // Проверка на заполненность всех полей
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || workerId.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        // Регистрация нового пользователя в Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val adminId = auth.currentUser?.uid // Получение ID администратора
                    if (adminId != null) { // Проверка, что ID не равен null
                        // Создание объекта Admin
                        val admin = Admin(adminId, email, fullName, workerId, phone)
                        // Сохранение данных администратора в Firebase Database
                        database.child(adminId).setValue(admin)
                            .addOnCompleteListener {
                                // Сохранение типа пользователя в SharedPreferences
                                val sharedPreferences = getSharedPreferences("UserTypePref", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("userType", "admin") // Сохраняем тип пользователя как администратор
                                editor.apply()

                                Toast.makeText(this, "Регистрация администратора прошла успешно", Toast.LENGTH_SHORT).show()
                                // Переход на главный экран для администратора
                                startActivity(Intent(this, AdminHomeActivity::class.java))
                                finish() // Закрыть AdminRegistrationActivity
                            }
                    } else {
                        Toast.makeText(this, "Ошибка получения ID администратора", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

// Данные администратора
data class Admin(val adminId: String?, val email: String, val fullName: String, val workerId: String, val phone: String)