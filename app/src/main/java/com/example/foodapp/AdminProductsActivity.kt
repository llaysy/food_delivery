package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var btnRefreshProducts: Button

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
        btnRefreshProducts = findViewById(R.id.btnRefreshProducts)

        productsAdapter = ProductsAdapter(products, { product ->
            val intent = Intent(this, AddEditProductActivity::class.java).apply {
                putExtra("product_id", product.id)
            }
            startActivity(intent)
        }, { product ->
            showDeleteConfirmationDialog(product) // Показать диалог подтверждения
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productsAdapter

        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddEditProductActivity::class.java))
        }

        btnRefreshProducts.setOnClickListener {
            loadProducts()
        }

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

    private fun deleteProduct(product: Product) {
        database.child(product.id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Продукт удален", Toast.LENGTH_SHORT).show()
                loadProducts() // Обновляем список после удаления
            } else {
                Toast.makeText(this, "Ошибка удаления продукта", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Подтверждение удаления")
        builder.setMessage("Вы уверены, что хотите удалить продукт '${product.name}'?")

        builder.setPositiveButton("Да") { dialog, which ->
            deleteProduct(product) // Удаление продукта при подтверждении
        }

        builder.setNegativeButton("Нет") { dialog, which ->
            dialog.dismiss() // Закрытие диалога при отказе
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}