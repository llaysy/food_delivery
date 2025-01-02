package com.example.foodapp

data class Product(
    var id: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var description: String = "",
    var discount: Double = 0.0,
    var imageUrl: String = "", // URL изображения
    var category: String = "" // Это поле должно быть здесь
)