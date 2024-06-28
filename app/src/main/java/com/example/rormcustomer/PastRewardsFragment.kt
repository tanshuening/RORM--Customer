package com.example.rormcustomer


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.PastRewardsAdapter
import com.example.rormcustomer.databinding.FragmentPastRewardsBinding

class PastRewardsFragment : Fragment() {

    private lateinit var binding: FragmentPastRewardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPastRewardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        val pastRewardName = listOf("RM5 off", "RM10 off", "10% off")
        val expiredDate = listOf("20/06/2024", "21/06/2024", "22/06/2024")
        val pastRewardStatus = listOf("Expired", "Expired", "Used")
        val pastRewardImage = listOf(R.drawable.voucher2, R.drawable.voucher2, R.drawable.voucher2)
        val pastRewardsAdapter = PastRewardsAdapter(pastRewardName, expiredDate, pastRewardStatus, pastRewardImage)

        binding.pastRewardsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = pastRewardsAdapter
        }
    }

    companion object {
    }
}