package com.gsggroups.poultrymarket.DashboardView

import Person
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Common.UserRoles
import com.gsggroups.poultrymarket.Employement.EmployeDetails
import com.gsggroups.poultrymarket.Model.GetUserList
import com.gsggroups.poultrymarket.Model.UserItematList
import com.gsggroups.poultrymarket.Model.UserModel
import com.gsggroups.poultrymarket.R
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.Utils.getRoleName
import com.gsggroups.poultrymarket.base.ApiClient

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShopkeeperFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShopkeeperFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var personAdapter: RecyclerAdapter
    private lateinit var personList: ArrayList<Person>
    private lateinit var userList: ArrayList<UserModel>
    private lateinit var searchView: SearchView
    private lateinit var filterButton: Button
    private lateinit var stateSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private var userList_1 = ArrayList<UserModel>()
    var userRoleId: Int = 0
    var userRoleName: String = ""
    var selectedStatePosition = 0
    var selectedDistrictPosition = 0
    var fromFilterClick: Boolean = false
    private lateinit var loader: LoaderUtils
    private var searchRunnable: Runnable? = null
    private val searchHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var initialUserList = ArrayList<UserModel>() // first loaded common list
    private val masterUserList = ArrayList<UserModel>()  // Full dataset for filtering

    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private val pageSizeInitial = 10
    private val pageSizeLoadMore = 10
    private val searchCharLimit = 1

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
        return inflater.inflate(R.layout.fragment_shopkeeper, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loader = LoaderUtils(context)
    }

    fun setupDropDown() {
        // Get full list once for reuse
        val states = DropDownManager.getStates()

        val stateAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            states
        )
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = stateAdapter

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                // Store selected state position
                selectedStatePosition = position

                // Get the districts for this state
                val selectedState = states[position]
                val districts = DropDownManager.getDistrictsForState(selectedState)

                if (filterButton.text == "Clear") {
                    filterButton.text = "Filter"
                }

                // Set up district adapter
                val districtAdapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_item, districts
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                districtSpinner.adapter = districtAdapter

                // Reset district position
                selectedDistrictPosition = 1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedStatePosition = 0
                selectedDistrictPosition = 0

            }
        }

// District selection listener
        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                // Store district position
                if (selectedStatePosition == 0) {
                    selectedDistrictPosition = 0
                } else {
                    selectedDistrictPosition = position + 1

                }
                if (filterButton.text == "Clear") {
                    filterButton.text = "Filter"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShopkeeperFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShopkeeperFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = view.findViewById(R.id.searchViewShopkeeper)
        stateSpinner = view.findViewById(R.id.shopkeeper_state_dropdown)
        districtSpinner = view.findViewById(R.id.shopkeeper_district_dropdown)
        recyclerView = view.findViewById(R.id.dashboardRecyclerView)
        filterButton = view.findViewById(R.id.shopkeeper_filter_button)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        // Inflate the layout for this fragment
        userRoleId = SharedPreferencesManager.getRoleId(requireContext())?.toInt() ?: 0
        userRoleName = UserRoles.getRoleNameById(userRoleId).toString()

        setupDropDown()

        val userId = SharedPreferencesManager.getUserId(requireContext())
        userList_1 = ArrayList()
        // Set up the adapter and handle the row click
        personAdapter = RecyclerAdapter(userList_1, "ShopkeeperFragment") { person ->
            val intent = Intent(requireContext(), EmployeDetails::class.java).apply {
                putExtra("user_data", person)
                putExtra("source", "ShopkeeperFragment")

            }
            startActivity(intent)
        }
        recyclerView.adapter = personAdapter

        setupSearchView()
        getUserList(userId)


        filterButton.setOnClickListener {
            val userId = SharedPreferencesManager.getUserId(requireContext())
            if (filterButton.text == "Clear") {
                // Reset spinners and reload default list
                stateSpinner.setSelection(0)
                districtSpinner.setSelection(0)
                selectedStatePosition = 0
                selectedDistrictPosition = 0

//                getUserList(userId, reset = true)
                filterButton.text = "Filter"
                fromFilterClick=false
                if (searchView.query.toString().isEmpty()) {
                    getUserList(userId, reset = true)
                    fromFilterClick = true
                }
                searchView.setQuery("", false)
            } else {
                fromFilterClick=true
                if (stateSpinner.selectedItemPosition == 0) {
                    selectedDistrictPosition = 0
                    selectedStatePosition = 0
                } else {
                    selectedDistrictPosition = districtSpinner.selectedItemPosition + 1
                    selectedStatePosition = stateSpinner.selectedItemPosition
                }
                // Apply filter
                getUserList(userId, reset = true)
                // ✅ Change button text to Clear
                filterButton.text = "Clear"
            }

        }


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    //     && firstVisibleItemPosition >= 0
                    //   && totalItemCount >= pageSizeLoadMore
                    ) {
                        personAdapter.showLoadingFooter(true)
                        loadMoreItems()
                    }
                }
            }
        })

    }

//    private fun setupSearchView() {
//        // Ensure the SearchView is expanded by default and shows typing interface
//        searchView.setIconifiedByDefault(false)
//
//        // Set up listener for text changes in SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                personAdapter.filter(newText ?: "")
//                return true
//            }
//        })
//    }
private var currentQuery: String = ""

    private fun setupSearchView() {
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val userId = SharedPreferencesManager.getUserId(requireContext())
                if (!query.isNullOrBlank()) {
                    val q = query.trim().lowercase()
                    if (q.length >= searchCharLimit || q == "batchready" || q == "needload" || q == "goingforload") {
                        getUserList(userId, reset = true) //
                    }
                }
                hideKeyboard(searchView)
                searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                searchRunnable = Runnable {
                    val query = newText?.trim()?.lowercase() ?: ""
                    val userId = SharedPreferencesManager.getUserId(requireContext())

                    when {
                        /*    query.isEmpty() -> {
                                // Reset to initial state (API call)
                                getUserList(userId, reset = true)
                                // Do NOT hide keyboard here, let user keep typing
                            }

                            query == "batchready" || query == "needload" || query == "goingforload" -> {
                                getUserList(userId, reset = true)
                                // Keep keyboard open
                            }

                            query.length == 4 || query.length == 7 -> {
                                // Auto API call at length 4 or 7
                                getUserList(userId, reset = true)
                                // Do NOT hide keyboard here, let user continue typing
                            }

                            query.length >= 1 -> {
                                // Local filter only
                                personAdapter.filter(newText ?: "")
                            }

                            else -> {
                                // If nothing matches, clear list
                                personAdapter.updateList(arrayListOf())
                            }*/
                        query.isEmpty() && !fromFilterClick -> {
                            getUserList(userId, reset = true)
                        }

                        query == "batchready" || query == "needload" || query == "goingforload" -> {
                            getUserList(userId, reset = true)   // API call
                        }

                        query.length == 4 || query.length == 7 -> {
                            getUserList(userId, reset = true)   // API call
                        }

                        query.length >= 1 -> {
                            filterLocally(query.toString())   // purely local
                        }
                    }
                }

                searchHandler.postDelayed(searchRunnable!!, 500)
                return true
            }

        })

        val searchEditText =
            searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setOnTouchListener { v, event ->
            val drawableEnd = searchEditText.compoundDrawables[2]
            if (drawableEnd != null && event.action == MotionEvent.ACTION_UP) {
                val drawableWidth = drawableEnd.bounds.width()
                if (event.rawX >= (searchEditText.right - drawableWidth)) {
                    showHistoryPopup(searchEditText)
                    return@setOnTouchListener true
                }
            }
            false
        }

        searchView.setOnClickListener {
            showHistoryPopup(searchView)
        }

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistoryPopup(searchView)
            }
        }
    }
    private fun filterLocally(query: String) {
        if (query.isEmpty()) {
            personAdapter.updateList(ArrayList(masterUserList)) // show all
            return
        }

        val filtered = masterUserList.filter {
            it.name.toLowerCase().contains(query, ignoreCase = true) ||
                    it.detail.toLowerCase().contains(query, ignoreCase = true) ||
                    it.detail2.toLowerCase().contains(query, ignoreCase = true)
        }
        personAdapter.updateList(ArrayList(filtered))
    }

    private fun showHistoryPopup(anchor: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_history, null)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val popupWidth = (screenWidth * 0.5).toInt() // 75% of screen width

        val popupWindow = PopupWindow(
            popupView,
            popupWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setBackgroundDrawable(ColorDrawable()) // Enables outside touch dismissal
        popupWindow.elevation = 10f

        popupWindow.showAsDropDown(anchor)

        val itemBatchReady = popupView.findViewById<TextView>(R.id.itemBatchReady)
        val itemNeedLoad = popupView.findViewById<TextView>(R.id.itemNeedLoad)
        val itemGoingForLoad = popupView.findViewById<TextView>(R.id.itemGoingForLoad)

//        itemBatchReady.setOnClickListener {
//            val query = "batchready"
//            searchView.setQuery(query, false)
//            personAdapter.filter(query)
//            popupWindow.dismiss()
//        }

        itemNeedLoad.setOnClickListener {
            val query = "needload"
            searchView.setQuery(query, false)
            personAdapter.filter(query)
            popupWindow.dismiss()
        }

        itemBatchReady.visibility = View.GONE
        itemGoingForLoad.visibility = View.GONE

//        itemGoingForLoad.setOnClickListener {
//            val query = "goingforload"
//            searchView.setQuery(query, false)
//            personAdapter.filter(query)
//            popupWindow.dismiss()
//        }
    }

    private fun getUserList(userId: String?, reset: Boolean = true, query: String = currentQuery) {
        if (reset) {
            currentPage = 1
            isLastPage = false
            userList_1.clear()
            personAdapter.updateList(arrayListOf()) // clear adapter
            masterUserList.clear()

        }

        loader.show()
        isLoading = true

        val request = buildUserListRequest(userId, currentPage, pageSizeInitial)
        val call = ApiClient.retrofit.create(ApiService::class.java).getUserList(request)

        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                loader.hide()
                isLoading = false

                if (response.isSuccess) {
                    val mappedUsers = response.item.items.map { user -> mapUser(user) }
                    if (reset) {
                        initialUserList.clear()
                        initialUserList.addAll(mappedUsers)
                    }

                    // ✅ Always append to master list
                    masterUserList.addAll(mappedUsers)

                    if (mappedUsers.isEmpty()) {
                        CustomAlertDialog(requireContext())
                            .setTitle("No data found!")
                            .setDescription("Shopkeepers are not available!!")
                            .showOkButton(true, "OK") {
                                println("User acknowledged the error.")
                            }
                            .showCancelButton(false)
                            .show()
                        return@post
                    } else {
                        if (reset) {
                            personAdapter.updateList(mappedUsers)   // fresh set
                        } else {
                            personAdapter.appendList(mappedUsers)   // append for pagination
                        }
                    }

                    isLastPage = mappedUsers.size < pageSizeInitial
                }
            },
            onFailure = { error ->
                loader.hide()
                isLoading = false
                Log.e("FarmerFragment", "API Failure: $error")
            }
        )
    }


    private fun loadMoreItems() {
        isLoading = true
        currentPage++

        val userId = SharedPreferencesManager.getUserId(requireContext())
        val request = buildUserListRequest(userId, currentPage, pageSizeLoadMore)

        val call = ApiClient.retrofit.create(ApiService::class.java).getUserList(request)

        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                isLoading = false
                personAdapter.showLoadingFooter(false)

                if (response.isSuccess) {
                    val fetchedUsers = response.item.items
                    if (fetchedUsers.isNotEmpty()) {
                        val mappedUsers = fetchedUsers.map { user -> mapUser(user) }
                        // ✅ Keep all data in master list
                        masterUserList.addAll(mappedUsers)

                        userList_1.addAll(mappedUsers)
                        personAdapter.appendList(mappedUsers)

                        isLastPage = fetchedUsers.size < pageSizeLoadMore
                    } else {
                        isLastPage = true
                    }
                }
            },
            onFailure = { error ->
                isLoading = false
                personAdapter.showLoadingFooter(false)
                Log.e("Pagination", "Error: $error")
            }
        )
    }

    private fun mapUser(user: UserItematList): UserModel {


        val roleId = user.roleID
        val isFarmer = roleId == UserRoles.ID_FARMER
        val isShopkeeper = roleId == UserRoles.ID_SHOPKEEPER

        val (status, color) = when {

            user.needLoad -> "Status: Need Load" to "green"

            else -> "Status: Load not needed" to "orange"
        }
        return UserModel(
            role = getRoleName(user.roleID) ?: "Unknown",
            name = user.name ?: "No Name",
            detail = "Mobile: ${user.mobileNumber}",
            detail2 = "Shop Name: ${user.propertyList.firstOrNull()?.propertyName ?: "N/A"}",
            status = status,
            colorTemp = color,
            batchReady = user.batchReady,
            needLoad = user.needLoad,
            goingForLoad = user.goingForLoad,
            batchReadyUpdatedDateTime = user.batchReadyUpdatedDateTime,
            needLoadUpdatedDateTime = user.needLoadUpdatedDateTime,
            goingForLoadUpdatedDateTime = user.goingForLoadUpdatedDateTime,
            stateID = user.stateID,
            districtID = user.districtID,
            henCount = user.henCount,
            henWeight = user.henWeight,
            longitude = user.longitude,
            latitude = user.latitude,
            propertyList = user.propertyList ?: emptyList()   // 👈 pass it properly


        )
    }

    private fun buildUserListRequest(
        userId: String?,
        pageNumber: Int,
        pageSize: Int
    ): GetUserList {
        val query = searchView.query.toString().trim().lowercase()

        var batchReady = false
        var needLoad = false
        var goingForLoad = false
        var search = ""

        when (query) {
            "batchready" -> batchReady = true
            "needload" -> needLoad = true
            "goingforload" -> goingForLoad = true
            else -> {
                if (query.length >= searchCharLimit || fromFilterClick) {
                    search = query
                }
            }
        }

        return GetUserList(
            userId = userId ?: "",
            roleId = UserRoles.ID_SHOPKEEPER,
            pageNumber = pageNumber,
            pageSize = pageSize,
            search = search,
            sortColumn = "",
            sortDirection = "",
            stateId = selectedStatePosition,
            districtId = selectedDistrictPosition,
            batchReady = batchReady,
            needLoad = needLoad,
            goingForLoad = goingForLoad
        )
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}