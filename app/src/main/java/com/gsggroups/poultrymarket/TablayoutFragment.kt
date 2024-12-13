package com.gsggroups.poultrymarket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.DashboardView.FarmerFragment
import com.gsggroups.poultrymarket.DashboardView.PageAdapter
import com.gsggroups.poultrymarket.DashboardView.ShopkeeperFragment
import com.gsggroups.poultrymarket.DashboardView.TraderFragment
import com.gsggroups.poultrymarket.databinding.FragmentTablayoutBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TablayoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TablayoutFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var pagerAdapter: PageAdapter
    private var _binding: FragmentTablayoutBinding? = null
    private val binding get() = _binding!!

    private var fragmentIdentifiers: List<String> = listOf()
    private var titleList: List<String> = listOf()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentIdentifiers = it.getStringArrayList(ARG_FRAGMENT_IDENTIFIERS) ?: listOf()
            titleList = it.getStringArrayList(ARG_TITLE_LIST) ?: listOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTablayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        setupViewPager()

    }

    private fun setupViewPager() {
        val fragments = fragmentIdentifiers.map { createFragment(it) }
        pagerAdapter = PageAdapter(
            childFragmentManager,
            lifecycle,
            fragments,
            titleList
        )
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titleList[position]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
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
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    private fun createFragment(identifier: String): Fragment {
        return when (identifier) {
            "Farmer" -> FarmerFragment()
            "Trader" -> TraderFragment()
            "Shopkeeper" -> ShopkeeperFragment()
            else -> throw IllegalArgumentException("Unknown fragment identifier: $identifier")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.unregisterOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            // Unregister the callback to prevent memory leaks
        })
        _binding = null // Clear binding reference to avoid memory leaks
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Tablayout.
         */
        // TODO: Rename and change types and number of parameters
        private const val ARG_FRAGMENT_IDENTIFIERS = "ARG_FRAGMENT_IDENTIFIERS"
        private const val ARG_TITLE_LIST = "ARG_TITLE_LIST"

        @JvmStatic
        fun newInstance(titles: List<String>, fragmentIdentifiers: List<String>) =
            TablayoutFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_TITLE_LIST, ArrayList(titles))
                    putStringArrayList(ARG_FRAGMENT_IDENTIFIERS, ArrayList(fragmentIdentifiers))
                }
            }
    }

}