package com.example.foodapp

import android.content.Intent
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
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users") // Ссылка на пользователей

        btnLogoutUser = findViewById(R.id.btnLogoutUser)
        btnMessage = findViewById(R.id.btnMessage)
        btnFavorite = findViewById(R.id.btnFavorite)
        btnWishlist = findViewById(R.id.btnWishlist)
        btnHelp = findViewById(R.id.btnHelp)
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
                        val userName = task.result.child("name").value.toString() // Предполагается, что имя хранится под ключом "name"
                        val userRole = task.result.child("role").value.toString() // Предполагается, что роль хранится под ключом "role"
                        tvUserName.text = userName
                        tvUserRole.text = userRole
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
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Закрыть HomeActivity
    }

    private fun showUserInfo() {
        // Здесь можно открыть новое окно или диалог с информацией о пользователе
        Toast.makeText(this, "Информация о пользователе", Toast.LENGTH_SHORT).show()
    }
}