package com.gsggroups.poultrymarket.DashboardView

import Person
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
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
 * Use the [TraderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TraderFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var personAdapter: RecyclerAdapter
    private lateinit var personList: ArrayList<Person>
    private lateinit var userList: ArrayList<UserModel>
    private lateinit var filterButton: Button
    private lateinit var searchView: SearchView
    private lateinit var stateSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var userList_1: ArrayList<UserModel>
    var userRoleId: Int = 0
    var userRoleName: String = ""
    var selectedStatePosition = 0
    var selectedDistrictPosition = 0
    private lateinit var loader: LoaderUtils

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
        return inflater.inflate(R.layout.fragment_trader, container, false)
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TraderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TraderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateSpinner = view.findViewById(R.id.trader_state_dropdown)
        districtSpinner = view.findViewById(R.id.trader_district_dropdown)
        recyclerView = view.findViewById(R.id.dashboardRecyclerView)
        filterButton = view.findViewById(R.id.trader_filter_button)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        // Inflate the layout for this fragment
        userRoleId = SharedPreferencesManager.getRoleId(requireContext())?.toInt() ?: 0
        userRoleName = UserRoles.getRoleNameById(userRoleId).toString()

        setupDropDown()

        val userId = SharedPreferencesManager.getUserId(requireContext())
        getUserList(userId)

        // Set up the adapter and handle the row click
        personAdapter = RecyclerAdapter(userList_1) { person ->
            val intent = Intent(context, EmployeDetails::class.java).apply {
                putExtra("name", person.name)
                putExtra("role", person.role)
                putExtra("detail", person.detail)
            }
            startActivity(intent)
        }

        searchView = view.findViewById(R.id.searchView)
        setupSearchView()
    }

    private fun setupSearchView() {
        // Ensure the SearchView is expanded by default and shows typing interface
        searchView.setIconifiedByDefault(false)

        // Set up listener for text changes in SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                personAdapter.filter(newText ?: "")
                return true
            }
        })
    }

    private fun getUserList( userId: String?) {
        //  loader.show()
        userList_1 = ArrayList()

        val request = GetUserList(
            userId = userId ?: "",
            roleId = UserRoles.ID_TRADER,
            pageNumber = 1,
            pageSize = 10,
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
                if (response.isSuccess) {
                    val fetchedUsers = response.item.items
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
                            detail2 = "Property: ${user.propertyList.firstOrNull()?.propertyName ?: "N/A"}",
                            status = status,
                            colorTemp = color
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
                } else {
                    Log.e("FarmerFrag", "Error: ${response.message}")
                }
            },
            onFailure = { error ->
                Log.e("Dashboard", "Error: $error")
            }
        )
    }
}