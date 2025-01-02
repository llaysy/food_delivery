package com.example.foodapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductsAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit // Новый параметр для обработки удаления
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete) // Кнопка удаления

        fun bind(product: Product) {
            tvProductName.text = product.name
            tvProductPrice.text = "${product.price} ₽"

            // Загрузка изображения с помощью Glide
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(ivProductImage)

            itemView.setOnClickListener { onProductClick(product) }
            btnDelete.setOnClickListener {
                // Показать окно подтверждения удаления
                onDeleteClick(product) // Передаем продукт для удаления
            }
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