package com.example.foodapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminCategoriesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var btnAddCategory: Button
    private lateinit var etCategoryName: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_categories)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Categories")

        recyclerView = findViewById(R.id.recyclerViewCategories)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        etCategoryName = findViewById(R.id.etCategoryName)

        categoriesAdapter = CategoriesAdapter(categories) { category ->
            // Здесь можно добавить логику для работы с подкатегориями
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = categoriesAdapter

        btnAddCategory.setOnClickListener {
            addCategory()
        }

        loadCategories()
    }

    private fun addCategory() {
        val categoryName = etCategoryName.text.toString().trim()
        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Введите название категории", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryId = database.push().key ?: return
        val category = Category(id = categoryId, name = categoryName)
        database.child(categoryId).setValue(category).addOnCompleteListener {
            Toast.makeText(this, "Категория добавлена", Toast.LENGTH_SHORT).show()
            etCategoryName.text.clear()
            loadCategories()
        }
    }

    private fun loadCategories() {
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                categories.clear()
                task.result.children.forEach { snapshot ->
                    val category = snapshot.getValue(Category::class.java)
                    category?.let { categories.add(it) }
                }
                categoriesAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show()
            }
        }
    }
}