package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewReviewBinding

class ReviewAdapter(
    private var customerNames: ArrayList<String>,
    private var profilePictures: ArrayList<String>, // Update if you have profile pictures
    private var dates: ArrayList<String>,
    private var reviews: ArrayList<String>,
    private var ratings: ArrayList<Int>,
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = CardViewReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val customerName = customerNames[position]
        val profilePicture = profilePictures[position] // Update if you have profile pictures
        val date = dates[position]
        val review = reviews[position]
        val rating = ratings[position]
        holder.bind(customerName, profilePicture, date, review, rating)
    }

    override fun getItemCount(): Int {
        return customerNames.size
    }

    class ReviewViewHolder(private val binding: CardViewReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(customerName: String, profilePicture: String, date: String, review: String, rating: Int) {
            binding.customerName.text = customerName
            binding.date.text = date
            binding.review.text = review
            Glide.with(binding.profilePicture.context).load(profilePicture).into(binding.profilePicture)
            setStars(rating)
        }

        private fun setStars(rating: Int) {
            val stars = listOf(binding.ratingStar1, binding.ratingStar2, binding.ratingStar3, binding.ratingStar4, binding.ratingStar5)
            stars.forEachIndexed { index, imageView ->
                if (index < rating) {
                    imageView.setImageResource(com.example.rormcustomer.R.drawable.star_filled)
                } else {
                    imageView.setImageResource(com.example.rormcustomer.R.drawable.star_default)
                }
            }
        }
    }

    fun updateData(customerNames: ArrayList<String>, profilePictures: ArrayList<String>, dates: ArrayList<String>, reviews: ArrayList<String>, ratings: ArrayList<Int>) {
        this.customerNames = customerNames
        this.profilePictures = profilePictures // Update if you have profile pictures
        this.dates = dates
        this.reviews = reviews
        this.ratings = ratings
        notifyDataSetChanged()
    }
}
