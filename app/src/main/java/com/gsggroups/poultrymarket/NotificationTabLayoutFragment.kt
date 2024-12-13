package com.gsggroups.poultrymarket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.gsggroups.poultrymarket.databinding.FragmentNotificationTabLayoutBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationTabLayoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationTabLayoutFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var pagerAdapter: NotificationPageAdapter
    private var _binding: FragmentNotificationTabLayoutBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationTabLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.notification_tab_layout)

        setupViewPager()

        addBadgeToTab(tabLayout, 0, 7)
        addBadgeToTab(tabLayout, 1, 5)
        addBadgeToTab(tabLayout, 2, 2)  // Badge for Farmer tab with number 5
    }

    private fun setupViewPager() {
        pagerAdapter = NotificationPageAdapter(
            childFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = pagerAdapter

        binding.notificationTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.notificationTabLayout.selectTab(binding.notificationTabLayout.getTabAt(position))
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.unregisterOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            // Unregister the callback to prevent memory leaks
        })
        _binding = null // Clear binding reference to avoid memory leaks
    }


    // Function to add a badge to a specific tab
    private fun addBadgeToTab(tabLayout: TabLayout, tabIndex: Int, badgeNumber: Int) {
        val tab = tabLayout.getTabAt(tabIndex)
        val badgeDrawable = tab?.orCreateBadge
        badgeDrawable?.apply {
            number = badgeNumber
            badgeGravity = BadgeDrawable.TOP_END // Place it at the top right corner
            verticalOffset = 20 // Adjust the vertical position upwards (in dp)
            horizontalOffset = 10 // Adjust the horizontal position closer to the text (in dp)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationTabLayoutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}