package com.example.foodapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var database: DatabaseReference

    private val products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        database = FirebaseDatabase.getInstance().getReference("Products")

        recyclerView = findViewById(R.id.recyclerViewProducts)
        productsAdapter = ProductsAdapter(products) { product ->
            // Здесь можно добавить логику добавления товара в корзину
            Toast.makeText(this, "Добавлено в корзину: ${product.name}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productsAdapter

        loadProducts()
    }

    private fun loadProducts() {
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                products.clear()
                task.result.children.forEach { snapshot ->
                    val product = snapshot.getValue(Product::class.java)
                    product?.let { products.add(it) }
                }
                productsAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Ошибка загрузки продуктов", Toast.LENGTH_SHORT).show()
            }
        }
    }
}