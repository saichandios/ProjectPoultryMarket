package com.gsggroups.poultrymarket.DashboardView

import Person
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import android.widget.ListPopupWindow
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Common.UserRoles
import com.gsggroups.poultrymarket.Employement.EmployeDetails
import com.gsggroups.poultrymarket.Model.GetUserList
import com.gsggroups.poultrymarket.Model.ListResponseModel
import com.gsggroups.poultrymarket.Model.ListRoleRequest
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
 * Use the [FarmerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FarmerFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var personAdapter: RecyclerAdapter
    private lateinit var personList: ArrayList<Person>
    private lateinit var userList: ArrayList<UserModel>
    private lateinit var searchView: SearchView
    private lateinit var filterButton: Button
    private lateinit var stateSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var userList_1: ArrayList<UserModel>
    var userRoleId: Int = 0
    var userRoleName: String = ""
    var selectedStatePosition = 0
    var selectedDistrictPosition = 0
    private lateinit var loader: LoaderUtils


    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private val pageSizeInitial = 20
    private val pageSizeLoadMore = 10

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
        return inflater.inflate(R.layout.fragment_farmer, container, false)
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

                // Set up district adapter
                val districtAdapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_item, districts
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                districtSpinner.adapter = districtAdapter

                // Reset district position
                selectedDistrictPosition = -1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

// District selection listener
        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                // Store district position
                selectedDistrictPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FarmerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = view.findViewById(R.id.searchViewFarmer)
        stateSpinner = view.findViewById(R.id.farmer_state_dropdown)
        districtSpinner = view.findViewById(R.id.farmer_district_dropdown)
        recyclerView = view.findViewById(R.id.dashboardRecyclerView)
        filterButton = view.findViewById(R.id.farmer_filter_button)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        // Inflate the layout for this fragment
        userRoleId = SharedPreferencesManager.getRoleId(requireContext())?.toInt() ?: 0
        userRoleName = UserRoles.getRoleNameById(userRoleId).toString()

        setupDropDown()

        val userId = SharedPreferencesManager.getUserId(requireContext())
        getUserList(userId)

//        userList_1.forEach { user ->
//            // Normalize and check the status
//            val trimmedStatus = user.status.trim().replace("\\s+".toRegex(), " ")
//            if (trimmedStatus.contains("Batch Ready", ignoreCase = true)) {
//                user.colorTemp = "green" // Assign the color
//            } else {
//                user.colorTemp = "orange"
//            }
//        }

//        recyclerView.adapter = personAdapter

        setupSearchView()



        filterButton.setOnClickListener {
            val userId = SharedPreferencesManager.getUserId(requireContext())
            getUserList(userId)
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
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= pageSizeLoadMore
                    ) {
                        loadMoreItems()
                    }
                }
            }
        })
    }

    private fun getUserList( userId: String?) {
        loader.show()
        userList_1 = ArrayList()

        val request = GetUserList(
            userId = userId ?: "",
            roleId = UserRoles.ID_FARMER,
            pageNumber = 1,
            pageSize = 20,
            search = searchView.query.toString(),
            sortColumn = "",
            sortDirection = "",
            stateId = selectedStatePosition,
            districtId = selectedDistrictPosition,
            batchReady = false,
            needLoad = false,
            goingForLoad = false
        )
        val call = ApiClient.retrofit
            .create(ApiService::class.java)
            .getUserList(request)
        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                if (response.isSuccess) {
                    userList_1.clear()

                    val fetchedUsers = response.item.items
                    if (fetchedUsers.isEmpty()) {
                        loader.hide()
                        personAdapter = RecyclerAdapter(userList_1) { /* item click logic */ }
                        recyclerView.adapter = personAdapter
                        CustomAlertDialog(requireContext())
                            .setTitle("No data found!")
                            .setDescription("Farmers are not available")
                            .showOkButton(true, "OK") {
                                println("User acknowledged the error.")
                            }
                            .showCancelButton(false)
                            .show()
                        return@post
                    }

                    for (user in fetchedUsers) {
                        val status = when {
                            user.batchReady -> "Status: Batch Ready"
                            else -> "Status: Batch not available"
                        }

                        val color = if (user.batchReady) "green" else "orange"

                        val userModel = UserModel(
                            role = getRoleName(user.roleID) ?: "Unknown",
                            name = user.name ?: "No Name",
                            detail = "Mobile: ${user.mobileNumber}",
                            detail2 = "Farm Name: ${user.propertyList.firstOrNull()?.propertyName ?: "N/A"}",
                            status = status,
                            colorTemp = color,
                            batchReady = user.batchReady,
                            needLoad = user.needLoad,
                            goingForLoad = user.goingForLoad,
                            batchReadyUpdatedDateTime = user.batchReadyUpdatedDateTime,
                            needLoadUpdatedDateTime = user.needLoadUpdatedDateTime,
                            goingForLoadUpdatedDateTime = user.goingForLoadUpdatedDateTime,
                            stateID = user.stateID,
                            districtID = user.districtID
                        )
                        userList_1.add(userModel)
                    }

                    personAdapter = RecyclerAdapter(userList_1) { person ->
                        val intent = Intent(context, EmployeDetails::class.java).apply {
                            putExtra("name", person.name)
                            putExtra("role", person.role)
                            putExtra("mobile", person.detail)
                        }
                        startActivity(intent)
                    }

                    recyclerView.adapter = personAdapter
                    loader.hide()
                } else {
                    Log.e("Dashboard", "Error: ${response.message}")
                }
            },
            onFailure = { error ->
                Log.e("Dashboard", "Error: $error")
            }
        )


//       private fun setupSearchView() {
//        searchView.setIconifiedByDefault(false)
//            // Set up listener for text changes in SearchView
//            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                personAdapter.filter(newText ?: "")
//                return true
//            }
//             })
//        }
    }



    private fun setupSearchView() {
        searchView.setIconifiedByDefault(false)

        // Listen for query text changes
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                personAdapter.filter(newText ?: "")
                return true
            }
        })

        // Add touch listener to detect drawable (e.g., right icon) click
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setOnTouchListener { v, event ->
            val drawableEnd = searchEditText.compoundDrawables[2] // Right drawable
            if (drawableEnd != null && event.action == MotionEvent.ACTION_UP) {
                val drawableWidth = drawableEnd.bounds.width()
                if (event.rawX >= (searchEditText.right - drawableWidth)) {
                    showHistoryPopup(searchEditText)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Show popup when SearchView is clicked
        searchView.setOnClickListener {
            showHistoryPopup(searchView)
        }

        // Also optional: Show popup when SearchView gains focus (in case user taps into the field)
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistoryPopup(searchView)
            }
        }
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

        itemBatchReady.setOnClickListener {
            val query = "batchready"
            searchView.setQuery(query, false)
            personAdapter.filter(query)
            popupWindow.dismiss()
        }

        itemNeedLoad.visibility = View.GONE
        itemGoingForLoad.visibility = View.GONE

//        itemNeedLoad.setOnClickListener {
//            val query = "needload"
//            searchView.setQuery(query, false)
//            personAdapter.filter(query)
//            popupWindow.dismiss()
//        }
//
//        itemGoingForLoad.setOnClickListener {
//            val query = "goingforload"
//            searchView.setQuery(query, false)
//            personAdapter.filter(query)
//            popupWindow.dismiss()
//        }
    }


    private fun loadMoreItems() {
        isLoading = true
        currentPage++

        val userId = SharedPreferencesManager.getUserId(requireContext())
        val request = GetUserList(
            userId = userId ?: "",
            roleId = UserRoles.ID_SHOPKEEPER,
            pageNumber = currentPage,
            pageSize = pageSizeLoadMore,
            search = "",
            sortColumn = "",
            sortDirection = "",
            stateId = selectedStatePosition,
            districtId = selectedDistrictPosition,
            batchReady = false,
            needLoad = false,
            goingForLoad = false
        )

        val call = ApiClient.retrofit
            .create(ApiService::class.java)
            .getUserList(request)

        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                isLoading = false
                if (response.isSuccess) {
                    val fetchedUsers = response.item.items
                    if (fetchedUsers.isEmpty()) {
                        isLastPage = true
                    } else {
                        for (user in fetchedUsers) {
                            val status = if (user.needLoad) "Status: Need Load" else "Status: No Need"
                            val color = if (user.batchReady) "green" else "orange"

                            val userModel = UserModel(
                                role = UserRoles.getRoleNameById(user.roleID) ?: "Unknown",
                                name = user.name ?: "No Name",
                                detail = "Mobile: ${user.mobileNumber}",
                                detail2 = "Property: ${user.propertyList.firstOrNull()?.propertyName ?: "N/A"}",
                                status = status,
                                colorTemp = color
                            )
                            userList_1.add(userModel)
                        }

                        personAdapter.notifyDataSetChanged()
                    }
                }
            },
            onFailure = { error ->
                isLoading = false
                Log.e("Pagination", "Error: $error")
            }
        )
    }
}