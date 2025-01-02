package com.example.foodapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductsByCategoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var database: DatabaseReference

    private val products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_by_category)

        val categoryId = intent.getStringExtra("category_id") ?: return
        val categoryName = intent.getStringExtra("category_name")

        database = FirebaseDatabase.getInstance().getReference("Products")

        recyclerView = findViewById(R.id.recyclerViewProducts)
        productsAdapter = ProductsAdapter(products, { product ->
            // Логика для обработки клика на продукте
        }, { product ->
            // Логика для обработки удаления продукта
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productsAdapter

        loadProductsByCategory(categoryId) // Загрузка продуктов по категории
    }

    private fun loadProductsByCategory(categoryId: String) {
        database.orderByChild("category").equalTo(categoryId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                products.clear()
                task.result.children.forEach { snapshot ->
                    val product = snapshot.getValue(Product::class.java)
                    Log.d("ProductsByCategory", "Loaded product: $product") // Логирование загруженного продукта
                    product?.let { products.add(it) }
                }
                productsAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Ошибка загрузки продуктов: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}