package com.natth.fixingmanapp.activities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.natth.fixingmanapp.fragment.HomeFragment
import com.natth.fixingmanapp.fragment.ListFragment
import com.natth.fixingmanapp.fragment.NotiFragment
import com.natth.fixingmanapp.fragment.*

internal class PagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {

        return when (position) {
            0 -> {
                HomeFragment()
            }
            1 -> {
                ListFragment()
            }
            2 -> {
                NotiFragment()
            }

            else -> null
        }
    }

    override fun getCount(): Int {

        return 3
    }

}

