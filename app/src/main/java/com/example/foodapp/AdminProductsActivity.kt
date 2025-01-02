package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminProductsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var btnAddProduct: Button
    private lateinit var btnRefreshProducts: Button // Новая кнопка для обновления списка продуктов

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_products)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Products")

        recyclerView = findViewById(R.id.recyclerViewProducts)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        btnRefreshProducts = findViewById(R.id.btnRefreshProducts) // Инициализация кнопки обновления

        productsAdapter = ProductsAdapter(products) { product ->
            val intent = Intent(this, AddEditProductActivity::class.java).apply {
                putExtra("product_id", product.id)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productsAdapter

        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddEditProductActivity::class.java))
        }

        btnRefreshProducts.setOnClickListener {
            loadProducts() // Загрузка продуктов при нажатии на кнопку обновления
        }

        loadProducts() // Начальная загрузка продуктов
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