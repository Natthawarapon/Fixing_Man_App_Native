package com.natth.fixingmanapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.natth.fixingmanapp.R

class BottomNavActivity : AppCompatActivity() {

    private lateinit var mViewPager:ViewPager
    private lateinit var homeBtn:ImageButton
    private lateinit var listBtn:ImageButton
    private lateinit var notiBtn:ImageButton
    private lateinit var mPagerAdapter:PagerAdapter
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAtZJdJEc:APA91bF-9MMxHXSvmxTvMsvafW122aSDeWGtAAuLzs5f4Sq8aTzJydt82CBNat5QaW7iUiNKZgIGbNoCQ2IYdHqpUEBQHAXOPnpEDdwdbQ9HB6zQqlW6HXc4B_kY5lb4XezzuOBxQBq0"
    private val contentType = "application/json"
    private val requestQueue: RequestQueue by lazy {

        Volley.newRequestQueue(this.applicationContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

//        FirebaseMessaging.getInstance().subscribeToTopic("test")

        // init views
        mViewPager = findViewById(R.id.mViewPager)
        homeBtn = findViewById(R.id.homeBtn)
        listBtn = findViewById(R.id.listBtn)
        notiBtn = findViewById(R.id.notiBtn)


        //onclick listner

        homeBtn.setOnClickListener {
            mViewPager.currentItem = 0

        }

        listBtn.setOnClickListener {

            mViewPager.currentItem = 1

        }

        notiBtn.setOnClickListener {
            mViewPager.currentItem = 2

        }

        mPagerAdapter = PagerAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerAdapter
        mViewPager.offscreenPageLimit = 3

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
            override fun onPageSelected(position: Int) {
                changeTabs(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        homeBtn.setImageResource(R.drawable.ic_home_pink)
        listBtn.setImageResource(R.drawable.ic_receipt_black_24dp)
        notiBtn.setImageResource(R.drawable.ic_person_black_24dp)
    }

    private fun changeTabs(position: Int) {

        if (position == 0) {
            homeBtn.setImageResource(R.drawable.ic_home_pink)
            listBtn.setImageResource(R.drawable.ic_receipt_black_24dp)
            notiBtn.setImageResource(R.drawable.ic_person_black_24dp)

        }
        if (position == 1) {
            homeBtn.setImageResource(R.drawable.ic_home_black_24dp)
            listBtn.setImageResource(R.drawable.ic_receipt_pink_24dp)
            notiBtn.setImageResource(R.drawable.ic_person_black_24dp)
        }

        if (position == 2) {
            homeBtn.setImageResource(R.drawable.ic_home_black_24dp)
            listBtn.setImageResource(R.drawable.ic_receipt_black_24dp)
            notiBtn.setImageResource(R.drawable.ic_person_pink_24dp)
        }

    }
}
