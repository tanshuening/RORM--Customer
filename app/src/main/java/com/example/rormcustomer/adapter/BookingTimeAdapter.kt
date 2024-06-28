package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.databinding.CardViewTimeBinding

class BookingTimeAdapter(
    private val timeSlots: List<String>,
    private val onTimeSelected: (String) -> Unit
) : RecyclerView.Adapter<BookingTimeAdapter.BookingTimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingTimeViewHolder {
        val binding = CardViewTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingTimeViewHolder(binding, onTimeSelected)
    }

    override fun onBindViewHolder(holder: BookingTimeViewHolder, position: Int) {
        val timeSlot = timeSlots[position]
        holder.bind(timeSlot)
    }

    override fun getItemCount(): Int {
        return timeSlots.size
    }

    class BookingTimeViewHolder(
        private val binding: CardViewTimeBinding,
        private val onTimeSelected: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(time: String) {
            binding.timeSlot.text = time
            binding.root.setOnClickListener {
                onTimeSelected(time)
            }
        }
    }
}
