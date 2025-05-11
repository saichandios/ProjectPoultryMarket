package com.gsggroups.poultrymarket

import Person
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.RetrofitClient
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.DashboardView.RecyclerAdapter
import com.gsggroups.poultrymarket.Employement.EmployeDetails
import com.gsggroups.poultrymarket.Model.GetUserList
import com.gsggroups.poultrymarket.Model.ListResponseModel
import com.gsggroups.poultrymarket.Model.ListRoleRequest
import com.gsggroups.poultrymarket.Model.UserModel
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.base.ApiClient


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CutterListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CutterListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var personAdapter: RecyclerAdapter
    private lateinit var personList: ArrayList<Person>
    private lateinit var userList: ArrayList<UserModel>
    private lateinit var searchView: SearchView

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
        val userId = SharedPreferencesManager.getUserId(requireContext())

        getUserList(userId)
        return inflater.inflate(R.layout.fragment_cutter_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sample data for demonstration
        userList = arrayListOf(
            UserModel(role = "Cutter", name = "Cutter 123", detail = "State: Andhra Pradesh",
                detail2 = "District: Guntur", status = ""),
            UserModel(role = "Cutter", name = "SS1232", detail = "State: Telangana",
                detail2 = "District: RangaReddy", status = ""),
            UserModel(role = "Cutter", name = "new Cutter", detail = "State: Telangana",
                detail2 = "District: RangaReddy", status = "Need Job")
        )

        recyclerView = view.findViewById(R.id.dashboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        // Set up the adapter and handle the row click
        personAdapter = RecyclerAdapter(userList) { person ->
            val intent = Intent(context, EmployeDetails::class.java).apply {
                putExtra("name", person.name)
                putExtra("role", person.roleID)
                putExtra("detail", person.mobileNumber)
            }
            startActivity(intent)
        }

        userList.forEach { user ->
            // Normalize and check the status
            val trimmedStatus = user.status.trim().replace("\\s+".toRegex(), " ")
            if (trimmedStatus.contains("Need Job", ignoreCase = true)) {
                user.colorTemp = "green" // Assign the color
            } else {
                user.colorTemp = "orange"
            }
        }

        recyclerView.adapter = personAdapter

        searchView = view.findViewById(R.id.cutter_searchView)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CutterListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CutterListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getUserList( userId: String?) {
        val farmerRequest = GetUserList(
            userId = userId ?: "",
            roleId = 0,
            pageNumber = 1,
            pageSize = 10,
            search = "",
            sortColumn = "",
            sortDirection = "",
            stateId = 0,
            districtId = 0,
            batchReady = false,
            needLoad = false,
            goingForLoad = false
        )

        val call = RetrofitClient.retrofit.create(ApiService::class.java).getUserList(farmerRequest)

        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                val isSuccess = response.isSuccess
                if (isSuccess) {
                    val item = response.item as? Map<*, *>
                    val items = item?.get("items") as? List<Map<String, Any>>
                    val userItem = items?.firstOrNull()
                    userItem?.let {
                        val name = it["name"]?.toString() ?: "No Name"
                        println("User Name: $name")
                    }
                } else {
                    val message = response.message ?: "Unknown error"
                    CustomAlertDialog(requireContext())
                        .setTitle("Error")
                        .setDescription(message)
                        .showOkButton(true, "OK") {
                            println("User acknowledged the error.")
                        }
                        .showCancelButton(false)
                        .show()
                }
            },
            onFailure = { error ->
                CustomAlertDialog(requireContext())
                    .setTitle("API Failure")
                    .setDescription(error)
                    .showOkButton(true, "Retry") {
                        println("Retrying API request...")
                    }
                    .showCancelButton(false)
                    .show()
            }
        )
    }
}