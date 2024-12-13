package com.gsggroups.poultrymarket.Employement

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
import com.gsggroups.poultrymarket.DashboardView.RecyclerAdapter
import com.gsggroups.poultrymarket.Model.ListResponseModel
import com.gsggroups.poultrymarket.Model.ListRoleRequest
import com.gsggroups.poultrymarket.Model.UserModel
import com.gsggroups.poultrymarket.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllEmployersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllEmployersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var personAdapter: RecyclerAdapter
    private lateinit var personList: ArrayList<Person>
    private lateinit var employerList: ArrayList<UserModel>
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
        getEmployerList()
        return inflater.inflate(R.layout.fragment_all_employers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sample data for demonstration
        personList = arrayListOf(
            Person("Farmer","Saichand", "Need Cutter"),
            Person("Trader","Gopi", "Need Driver"),
            Person("ShopKeeper","Ganapti", "Need Cutter"),
            Person(role = "Farmer", name = "Farmer 1", detail = "Need Supervisor"),
            Person(role = "Trader", name = "Trader 2", detail = "Need Driver"),
            Person(role = "ShopKeeper", name = "ShopKeeper 3", detail = "Need Cutter")
        )

        recyclerView = view.findViewById(R.id.dashboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

//         Set up the adapter and handle the row click
        personAdapter = RecyclerAdapter(employerList) { person ->
            val intent = Intent(requireActivity(), EmployeDetails::class.java).apply {
                putExtra("name", person.name)
                putExtra("role", person.roleID)
                putExtra("detail", person.mobileNumber)
            }
            startActivity(intent)
        }

        recyclerView.adapter = personAdapter

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllEmployersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllEmployersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun getEmployerList() {
        val farmerRequest = ListRoleRequest(
            roleId = 0,
            pageNumber = 0,
            pageSize = 0,
            search = "string",
            sortColumn = "string",
            sortDirection = "string",
            stateId = 0,
            districtId = 0,
            batchReady = true,
            needLoad = true,
            goingForLoad = true
        )

        ApiHelper.post(
            url = "getUserList",
            body = farmerRequest,
            responseType = ListResponseModel::class.java,
            onSuccess = { response ->
                if (response.isSuccess) {
                    val userItem = response.item.items.firstOrNull()
                    userItem?.let {
                        println("User Name: ${it.name}")
                    }
                    // Proceed to next activity or logic

                } else {
                    CustomAlertDialog(requireContext())
                        .setTitle("Error")
                        .setDescription(response.message)
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