package com.example.rormcustomer.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewMenuItemBinding
import com.example.rormcustomer.models.MenuItem

class MenuItemAdapter(
    private val menuItems: List<MenuItem>,
    private val context: Context,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(menuItem: MenuItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = CardViewMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuItemViewHolder(private val binding: CardViewMenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem) {
            binding.menuItemName.text = menuItem.foodName
            binding.menuItemPrice.text = menuItem.foodPrice

            Glide.with(context).load(Uri.parse(menuItem.foodImage)).into(binding.menuItemImage)

            binding.root.setOnClickListener {
                itemClickListener.onItemClick(menuItem)
            }
        }
    }
}
