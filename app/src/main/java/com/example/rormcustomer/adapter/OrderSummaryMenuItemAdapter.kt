package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.databinding.CardViewOrderSummaryItemBinding

class OrderSummaryMenuItemAdapter(
    private val items: List<String>,
    private val prices: List<Double>,
    private val quantities: List<Int>
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
        holder.bind(item, price, quantity)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class OrderSummaryViewHolder(private val binding: CardViewOrderSummaryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, price: Double, quantity: Int) {
            binding.menuItemName.text = item
            binding.price.text = String.format("%.2f", price * quantity)
            binding.quantity.text = "$quantity x"
        }
    }
}
