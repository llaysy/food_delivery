package com.example.foodapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var btnLogoutUser: Button
    private lateinit var icAvatar: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val categories = mutableListOf<Category>()
    private lateinit var categoriesAdapter: CategoriesAdapter // Адаптер для категорий

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
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories)

        btnLogoutUser.setOnClickListener {
            logout()
        }

        icAvatar.setOnClickListener {
            showUserInfo()
        }

        loadUserInfo()
        loadCategories()  // Загрузка категорий
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

    private fun loadCategories() {
        val categoriesDatabase = FirebaseDatabase.getInstance().getReference("Categories")
        categoriesDatabase.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                categories.clear()
                task.result.children.forEach { snapshot ->
                    val category = snapshot.getValue(Category::class.java)
                    category?.let { categories.add(it) }
                }
                setupCategoriesRecyclerView() // Настройка RecyclerView после загрузки категорий
            } else {
                Toast.makeText(this, "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCategoriesRecyclerView() {
        categoriesAdapter = CategoriesAdapter(categories) { category ->
            // Переход к экрану с продуктами выбранной категории
            val intent = Intent(this, ProductsByCategoryActivity::class.java).apply {
                putExtra("category_id", category.id)
                putExtra("category_name", category.name)
            }
            startActivity(intent)
        }
        recyclerViewCategories.layoutManager = LinearLayoutManager(this)
        recyclerViewCategories.adapter = categoriesAdapter
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