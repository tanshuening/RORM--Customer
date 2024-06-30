package com.example.rormcustomer.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.MenuItemInfoActivity
import com.example.rormcustomer.databinding.CardViewOrderSummaryItemBinding
import com.example.rormcustomer.models.OrderItem

class OrderSummaryMenuItemAdapter(
    private val items: List<String>,
    private val prices: List<Double>,
    private val quantities: List<Int>,
    private val restaurantId: String
) : RecyclerView.Adapter<OrderSummaryMenuItemAdapter.OrderSummaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder {
        return OrderSummaryViewHolder(
            CardViewOrderSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) {
        val item = items[position]
        val price = prices[position]
        val quantity = quantities[position]
        holder.bind(item, price, quantity, restaurantId)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class OrderSummaryViewHolder(private val binding: CardViewOrderSummaryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, price: Double, quantity: Int, restaurantId: String) {
            binding.menuItemName.text = item
            binding.price.text = String.format("%.2f", price * quantity)
            binding.quantity.text = "$quantity x"
            binding.edit.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, MenuItemInfoActivity::class.java).apply {
                    putExtra("menuItemName", item)
                    putExtra("restaurantId", restaurantId)
                    putExtra("quantity", quantity)
                    putExtra("isEdit", true)
                }
                context.startActivity(intent)
            }
        }
    }
}
