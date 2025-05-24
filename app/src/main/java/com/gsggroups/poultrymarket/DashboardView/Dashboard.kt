package com.gsggroups.poultrymarket.DashboardView

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gsggroups.poultrymarket.R
import com.gsggroups.poultrymarket.databinding.ActivityDashboardBinding

import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gsggroups.poultrymarket.BatchReadyFragment
import com.gsggroups.poultrymarket.ChickenRatesFragment
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Common.UserRoles
import com.gsggroups.poultrymarket.EditProfile
import com.gsggroups.poultrymarket.EggRatesFragment
import com.gsggroups.poultrymarket.EmployeeTabLayout
import com.gsggroups.poultrymarket.GoingForLoadFragment
import com.gsggroups.poultrymarket.Login
import com.gsggroups.poultrymarket.NeedEmployee
import com.gsggroups.poultrymarket.NotificationTabLayoutFragment
import com.gsggroups.poultrymarket.RegisterSingup
import com.gsggroups.poultrymarket.TablayoutFragment
import com.gsggroups.poultrymarket.UserInfo
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var adapter: PageAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var load_switch: Switch
    private lateinit var load_textView: TextView
    private lateinit var going_switch: Switch
    private lateinit var going_textView: TextView
    private lateinit var toggle: ActionBarDrawerToggle

    private var currentFragment: Fragment? = null

    private lateinit var loader: LoaderUtils
    var userRoleId: Int = 0
    var userRoleName: String = ""

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = binding.drawerLayout
        navView = binding.navigationView

        userRoleId = SharedPreferencesManager.getRoleId(this)?.toInt() ?: 0
        userRoleName = UserRoles.getRoleNameById(userRoleId).toString()

        loader = LoaderUtils(this)
        loader.show()
        Handler(Looper.getMainLooper()).postDelayed({
            loader.hide()
        }, 1000)

        // Set up ActionBarDrawerToggle to sync the state of the drawer with the hamburger icon
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            handleNavigationItemSelected(menuItem)
            true
        }

        val menu = navView.menu
        val navBatchReadyItem: MenuItem = menu.findItem(R.id.nav_BatchReady)
            if (userRoleName == UserRoles.ROLE_FARMER) {
                navBatchReadyItem.title = "Batch Ready"
            } else if (userRoleName == UserRoles.ROLE_TRADER) {
                navBatchReadyItem.title = "Need Load"
            } else {
                navBatchReadyItem.title = "Need Load"
            }


        // Handle Hamburger Icon click to open drawer
        val hamburgerIcon = findViewById<ImageView>(R.id.toolbar_hamburger)
        hamburgerIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        if (userRoleName != null || userRoleName != "null") {
            val tabs = getTabsBasedOnUserRole(userRoleName)
            navigateToTablayoutFragment(tabs.first, tabs.second)
        }

        //------------------------------------------------
        val userInfoLayout = UserInfo(this)
        userInfoLayout.updateUserInfo(
            userName = "User 1",
            userEmail = "1234567890",
            userImage = ContextCompat.getDrawable(this, R.drawable.profile_side),
        )
        userInfoLayout.view.setPadding(30, 20, 0, 0)
        navView.addHeaderView(userInfoLayout.view)


        //-----------------------------------------------------------------
       load_switch = findViewById(R.id.toolbar_switch1)
       load_textView = findViewById(R.id.toolbar_switch1_text)

        if(userRoleId == UserRoles.ID_FARMER) {
            val lastSubmitTime = SharedPreferencesManager.getLastSubmitTimeNeed(this)
            val isFirstSubmission = lastSubmitTime == "0"
            val isWithin24Hours = if (!isFirstSubmission) {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val lastTime = LocalDateTime.parse(lastSubmitTime, formatter)
                val currentTime = LocalDateTime.now()
                Duration.between(lastTime, currentTime).toHours() < 24
            } else {
                false
            }

            val shouldEnableSwitch = isFirstSubmission || isWithin24Hours
            load_switch.isChecked = shouldEnableSwitch
            load_switch.isEnabled = !shouldEnableSwitch
        } else if(userRoleId == UserRoles.ID_TRADER) {
            val lastSubmitTime = SharedPreferencesManager.getLastSubmitTimeNeed(this)
            val isFirstSubmission = lastSubmitTime == "0"
            val isWithin24Hours = if (!isFirstSubmission) {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val lastTime = LocalDateTime.parse(lastSubmitTime, formatter)
                val currentTime = LocalDateTime.now()
                Duration.between(lastTime, currentTime).toHours() < 24
            } else {
                false
            }

            val shouldEnableSwitch = isFirstSubmission || isWithin24Hours
            load_switch.isChecked = shouldEnableSwitch
            load_switch.isEnabled = !shouldEnableSwitch
        }

        // Set initial text color based on the default state of the switch
        load_textView.setTextColor(
            if (load_switch.isChecked) {
                // Dark Green color when switch is ON
                Color.parseColor("#006400") // Dark Green
            } else {
                // Red color when switch is OFF (Normal)
                Color.RED
            }
        )
        // Set up a listener to change text color based on switch state
        load_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                load_textView.setTextColor(Color.parseColor("#006400"))
                load_textView.setTypeface(null, Typeface.BOLD)
                loadFragment(BatchReadyFragment())
                highlightMenuItem(R.id.nav_BatchReady)
            } else {
                load_textView.setTextColor(Color.RED)
                load_textView.setTypeface(null, Typeface.NORMAL)
            }
        }


        going_switch = findViewById(R.id.toolbar_switch2)
        going_textView = findViewById(R.id.toolbar_switch2_text)

        if (userRoleId == UserRoles.ID_TRADER) {
            val lastSubmitTimeGoing = SharedPreferencesManager.getLastSubmitTimeGoing(this)
            val isFirstSubmission = lastSubmitTimeGoing == "0"
            val isWithin24Hours = if (!isFirstSubmission) {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val lastTime = LocalDateTime.parse(lastSubmitTimeGoing, formatter)
                val currentTime = LocalDateTime.now()
                Duration.between(lastTime, currentTime).toHours() < 24
            } else {
                false
            }

            val shouldEnableSwitch = isFirstSubmission || isWithin24Hours
            going_switch.isChecked = shouldEnableSwitch
            going_switch.isEnabled = !shouldEnableSwitch
        }

        // Set initial text color based on the default state of the switch
        going_textView.setTextColor(
            if (going_switch.isChecked) {
                Color.parseColor("#006400") // Dark Green
            } else {
                Color.RED
            }
        )
        // Set up a listener to change text color based on switch state
        going_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                going_textView.setTextColor(Color.parseColor("#006400"))
                going_textView.setTypeface(null, Typeface.BOLD)
                loadFragment(GoingForLoadFragment())
                highlightMenuItem(R.id.nav_GoingForLoad)
            } else {
                going_textView.setTextColor(Color.RED)
                going_textView.setTypeface(null, Typeface.NORMAL)
            }
        }


        if (userRoleName != null || userRoleName != "null") {
            setTextViewBasedOnRole(userRoleName, load_textView, going_textView, load_switch, going_switch)
        }

        if(userRoleId == UserRoles.ID_TRADER) {
            menu.findItem(R.id.nav_GoingForLoad).isVisible = true
        } else {
            menu.findItem(R.id.nav_GoingForLoad).isVisible = false
        }
    }

    fun setTextViewBasedOnRole(
        role: String,
        loadTextView: TextView,
        goingTextView: TextView,
        loadSwitch: Switch,
        goingSwitch: Switch
    ) {
        when (role) {
            UserRoles.ROLE_FARMER -> {
                    // For Farmer: If status is Batch Ready
                    loadTextView.text = "Batch Ready"
                    loadTextView.visibility = View.VISIBLE
                    loadSwitch.visibility = View.VISIBLE
                    goingTextView.visibility = View.GONE
                    goingSwitch.visibility = View.GONE
            }
            UserRoles.ROLE_TRADER -> {
                    // For Trader: If status is Need Load
                    loadTextView.text = "Need Load"
                    loadTextView.visibility = View.VISIBLE
                    loadSwitch.visibility = View.VISIBLE
                    goingTextView.visibility = View.VISIBLE
                    goingSwitch.visibility = View.VISIBLE

            }
            UserRoles.ROLE_SHOPKEEPER -> {
                    // For Shopkeeper: If status is Need Load
                    loadTextView.text = "Need Load"
                    loadTextView.visibility = View.VISIBLE
                    loadSwitch.visibility = View.VISIBLE
                    goingTextView.visibility = View.GONE
                    goingSwitch.visibility = View.GONE
            }
            else -> {
                // Default case for other roles
            }
        }
    }


    fun navigateToTablayoutFragment(titles: List<String>, fragmentIdentifiers: List<String>) {
        val fragmentTag = "TablayoutFragment" // Unique tag for this fragment

        // Check if the fragment is already in FragmentManager
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        if (existingFragment != null) {
            // If fragment exists, show it
            supportFragmentManager.beginTransaction()
                .show(existingFragment)
                .commit()
        } else {
            // If fragment doesn't exist, create it
            val tablayoutFragment = TablayoutFragment.newInstance(titles, fragmentIdentifiers)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, tablayoutFragment, fragmentTag)
                .commit()
        }
    }

    fun getTabsBasedOnUserRole(userRole: String): Pair<List<String>, List<String>> {
        // Mock API response
        val availableTabs = listOf(
            "Farmer" to "Farmer",
            "Trader" to "Trader",
            "Shopkeeper" to "Shopkeeper"
        )

        // Filter tabs based on user role
        val filteredTabs = when (userRole) {
            UserRoles.ROLE_FARMER -> availableTabs.filter { it.first == "Trader" }
            UserRoles.ROLE_TRADER -> availableTabs.filter { it.first in listOf("Farmer", "Shopkeeper") }
            UserRoles.ROLE_SHOPKEEPER -> availableTabs.filter { it.first == "Trader" }
            UserRoles.ROLE_ADMIN -> availableTabs // Admin sees all tabs
            else -> listOf() // Default case, no tabs
        }
        // Extract titles and fragment identifiers into separate lists
        val titles = filteredTabs.map { it.first }
        val fragmentIdentifiers = filteredTabs.map { it.second }

        // Return as a Pair
        return Pair(titles, fragmentIdentifiers)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun highlightMenuItem(menuItemId: Int) {
        navView.menu.findItem(menuItemId).isChecked = true
    }

    private fun handleNavigationItemSelected(menuItem: MenuItem) {
        val selectedFragment: Fragment = when (menuItem.itemId) {
            R.id.nav_List -> {
                val tabs = userRoleName?.let { getTabsBasedOnUserRole(it) }
                if (tabs != null) {
                    navigateToTablayoutFragment(tabs.first, tabs.second)
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                return
            }
            R.id.nav_BatchReady -> BatchReadyFragment()
            R.id.nav_GoingForLoad -> GoingForLoadFragment()
            R.id.nav_NeedEmployee -> NeedEmployee()
            R.id.nav_Profile -> EditProfile()
            R.id.nav_EmployeeList -> EmployeeTabLayout()
            R.id.nav_Notification -> NotificationTabLayoutFragment()
            R.id.nav_ChickenRates -> ChickenRatesFragment()
            R.id.nav_EggRates -> EggRatesFragment()
            R.id.nav_Logout -> {
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clears the back stack
                SharedPreferencesManager.clearSignedIn(this)
                SharedPreferencesManager.clearUserRole(this)
                SharedPreferencesManager.clearRoleId(this)
                startActivity(intent)
                return
            }
            else -> return // Handle other cases if necessary
        }

        selectedFragment?.let {
            // Check the currently displayed fragment
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

            if (currentFragment?.javaClass != it.javaClass) {
                // Replace with the selected fragment if it's different
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            } else {
                // Optional: Reload the current fragment if needed
                reloadFragment(currentFragment)
            }
        }

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun reloadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .detach(fragment) // Detach the fragment
            .attach(fragment) // Re-attach the same fragment
            .commit()
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(shouldAllowBack()) {
            super.onBackPressed()
        }
        // Do nothing or show a toast if needed
        // Toast.makeText(this, "Back disabled on Dashboard", Toast.LENGTH_SHORT).show()
    }

    // Example condition method
    private fun shouldAllowBack(): Boolean {
        // Replace with your condition
        return false
    }

}


