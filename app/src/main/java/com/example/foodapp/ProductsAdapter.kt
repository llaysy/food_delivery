package com.example.foodapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductsAdapter(private val products: List<Product>, private val onProductClick: (Product) -> Unit) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvProductDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        private val tvProductCategory: TextView = itemView.findViewById(R.id.tvProductCategory)
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)

        fun bind(product: Product) {
            tvProductName.text = product.name
            tvProductPrice.text = "${product.price} ₽"
            tvProductDescription.text = product.description
            tvProductCategory.text = product.category

            // Загрузка изображения с помощью Glide
            Glide.with(itemView.context)
                .load(product.imageUrl) // URL изображения
                .placeholder(R.drawable.placeholder_image) // Замените на ваше изображение по умолчанию
                .error(R.drawable.error_image) // Изображение в случае ошибки
                .into(ivProductImage)

            itemView.setOnClickListener { onProductClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}