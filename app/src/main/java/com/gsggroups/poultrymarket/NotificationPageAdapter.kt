package com.gsggroups.poultrymarket

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gsggroups.poultrymarket.DashboardView.FarmerFragment
import com.gsggroups.poultrymarket.DashboardView.ShopkeeperFragment
import com.gsggroups.poultrymarket.DashboardView.TraderFragment

class NotificationPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0-> {
                FarmerFragment()
            }
            1-> {
                TraderFragment()
            }
            else-> {
                ShopkeeperFragment()
            }
        }
    }


}