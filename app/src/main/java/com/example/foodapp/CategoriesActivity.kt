package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CategoriesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var database: DatabaseReference

    private val categories = mutableListOf<Category>() // Измените на список Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        recyclerView = findViewById(R.id.recyclerViewCategories)
        categoriesAdapter = CategoriesAdapter(categories) { category ->
            val intent = Intent(this, ProductsActivity::class.java).apply {
                putExtra("category_name", category.name) // Измените на category.name
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = categoriesAdapter

        loadCategories() // Загрузка категорий
    }

    private fun loadCategories() {
        database = FirebaseDatabase.getInstance().getReference("Categories") // Инициализация базы данных
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                categories.clear()
                task.result.children.forEach { snapshot ->
                    val category = snapshot.getValue(Category::class.java)
                    category?.let { categories.add(it) }
                }
                categoriesAdapter.notifyDataSetChanged() // Обновление адаптера
            } else {
                Toast.makeText(this, "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show()
            }
        }
    }
}