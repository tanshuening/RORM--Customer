package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.databinding.CardViewTimeBinding

class BookingAdapter(
    private var timeSlots: ArrayList<String>
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    private var onItemClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = CardViewTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val timeSlot = timeSlots[position]
        holder.bind(timeSlot)
    }

    override fun getItemCount(): Int {
        return timeSlots.size
    }

    fun updateTimeSlots(newTimeSlots: ArrayList<String>) {
        timeSlots = newTimeSlots
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    inner class BookingViewHolder(private val binding: CardViewTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(timeSlot: String) {
            binding.timeSlot.text = timeSlot
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(timeSlot)
            }
        }
    }
}
