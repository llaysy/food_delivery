package com.example.foodapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddEditProductActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var etProductDiscount: EditText
    private lateinit var etProductImageUrl: EditText // Для ссылки на изображение
    private lateinit var spinnerCategories: Spinner // Для выбора категории
    private lateinit var btnSaveProduct: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var productId: String? = null // Для редактирования товара
    private val categories = mutableListOf<Category>() // Переменная для хранения категорий

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_product)

        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductDescription = findViewById(R.id.etProductDescription)
        etProductDiscount = findViewById(R.id.etProductDiscount)
        etProductImageUrl = findViewById(R.id.etProductImageUrl)
        spinnerCategories = findViewById(R.id.spinnerCategories)
        btnSaveProduct = findViewById(R.id.btnSaveProduct)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Products")

        productId = intent.getStringExtra("product_id")

        if (productId != null) {
            loadProductData(productId!!)
        }

        loadCategories() // Загрузка категорий при создании активности

        btnSaveProduct.setOnClickListener {
            saveProduct()
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

                // Создание адаптера и установка его в Spinner
                val categoryNames = categories.map { it.name } // Получаем только названия категорий
                val adapter = SpinnerCategoriesAdapter(this, categoryNames)
                spinnerCategories.adapter = adapter

                // Проверьте, что категории загружены
                if (categories.isEmpty()) {
                    Toast.makeText(this, "Нет доступных категорий", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProductData(productId: String) {
        database.child(productId).get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.exists()) {
                val product = task.result.getValue(Product::class.java)
                product?.let {
                    etProductName.setText(it.name)
                    etProductPrice.setText(it.price.toString())
                    etProductDiscount.setText(it.discount.toString())
                    etProductDescription.setText(it.description)
                    etProductImageUrl.setText(it.imageUrl)

                    // Установка категории в spinner
                    val categoryPosition = categories.indexOfFirst { it.name == product.category }
                    if (categoryPosition != -1) {
                        spinnerCategories.setSelection(categoryPosition)
                    } else {
                        if (it.category.isNotEmpty()) {
                            Toast.makeText(this, "Категория '${it.category}' не найдена", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProduct() {
        val name = etProductName.text.toString().trim()
        val price = etProductPrice.text.toString().toDoubleOrNull()
        val discount = etProductDiscount.text.toString().toDoubleOrNull() ?: 0.0
        val description = etProductDescription.text.toString().trim()
        val imageUrl = etProductImageUrl.text.toString().trim()
        val category = spinnerCategories.selectedItem.toString()

        if (name.isEmpty() || price == null || description.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(
            id = productId ?: database.push().key ?: "",
            name = name,
            price = price,
            description = description,
            discount = discount,
            imageUrl = imageUrl,
            category = category
        )

        if (productId != null) {
            database.child(productId!!).setValue(product).addOnCompleteListener {
                Toast.makeText(this, "Товар обновлен", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            database.child(product.id).setValue(product).addOnCompleteListener {
                Toast.makeText(this, "Товар добавлен", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}