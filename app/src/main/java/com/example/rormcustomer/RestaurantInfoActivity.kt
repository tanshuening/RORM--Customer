package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.rormcustomer.databinding.ActivityRestaurantInfoBinding
import com.example.rormcustomer.restaurantFragment.BookingFragment
import com.example.rormcustomer.restaurantFragment.DetailsFragment
import com.example.rormcustomer.restaurantFragment.MenuFragment
import com.example.rormcustomer.restaurantFragment.ReviewsFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*

class RestaurantInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantInfoBinding
    private lateinit var restaurantInfo: DrawerLayout
    private lateinit var viewPager: ViewPager
    private lateinit var restaurantId: String
    private lateinit var databaseReference: DatabaseReference

    fun getRestaurantId(): String {
        return restaurantId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restaurantId = intent.getStringExtra("RestaurantId") ?: ""
        Log.d("RestaurantInfoActivity", "Retrieved RestaurantId: $restaurantId")

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.back)
            setDisplayHomeAsUpEnabled(true)
        }

        setupCollapsingToolbar()
        setupNavigationDrawer()
        setupViewPagerAndTabs()
        fetchRestaurantDetails()
    }

    private fun setupCollapsingToolbar() {
        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        val appBarLayout: AppBarLayout = findViewById(R.id.appBar)

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            supportActionBar?.setDisplayShowTitleEnabled(
                collapsingToolbar.height + verticalOffset <= 2 * ViewCompat.getMinimumHeight(collapsingToolbar)
            )
        })
    }

    private fun setupNavigationDrawer() {
        restaurantInfo = findViewById(R.id.restaurant_Info)
        val navView: NavigationView = findViewById(R.id.navigation_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true

            when (menuItem.itemId) {
                R.id.bookingFragment -> viewPager.currentItem = 0
                R.id.menuFragment -> viewPager.currentItem = 1
                R.id.reviewsFragment -> viewPager.currentItem = 2
                R.id.detailsFragment -> viewPager.currentItem = 3
            }

            restaurantInfo.closeDrawers()
            true
        }
    }

    private fun setupViewPagerAndTabs() {
        try {
            viewPager = findViewById(R.id.viewPager)
            val adapter = ViewPagerAdapter(supportFragmentManager, restaurantId)

            adapter.addFrag(BookingFragment(), "Booking")

            val menuFragment = MenuFragment().apply {
                arguments = Bundle().apply { putString("RestaurantId", restaurantId) }
            }
            adapter.addFrag(menuFragment, "Menu")

            adapter.addFrag(ReviewsFragment(), "Review")

            val detailsFragment = DetailsFragment().apply {
                arguments = Bundle().apply { putString("RestaurantId", restaurantId) }
            }
            adapter.addFrag(detailsFragment, "Details")

            viewPager.adapter = adapter

            val tabLayout: TabLayout = findViewById(R.id.tabLayout)
            tabLayout.setupWithViewPager(viewPager)
            Log.d("RestaurantInfoActivity", "ViewPager and Tabs setup successfully. RestaurantId: $restaurantId")
        } catch (e: Exception) {
            Log.e("RestaurantInfoActivity", "Error in setupViewPagerAndTabs", e)
        }
    }

    private fun fetchRestaurantDetails() {
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurantId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val restaurantName = dataSnapshot.child("name").value.toString()
                val restaurantPrice = dataSnapshot.child("price").value.toString()
                val restaurantLocation = dataSnapshot.child("location").value.toString()

                binding.apply {
                    restaurantNameInfo.text = restaurantName
                    restaurantPriceInfo.text = restaurantPrice
                    location.text = restaurantLocation
                    supportActionBar?.title = restaurantName
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("RestaurantInfoActivity", "Failed to load restaurant details", databaseError.toException())
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class ViewPagerAdapter(manager: FragmentManager, private val restaurantId: String) : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment = mFragmentList[position]

        override fun getCount(): Int = mFragmentList.size

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? = mFragmentTitleList[position]
    }
}
