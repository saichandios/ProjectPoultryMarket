package com.gsggroups.poultrymarket.Common

import android.content.Context

object UserRoles {

    // Define all available roles
    const val ROLE_CHICKS_SUPPLIER = "ChicksSupplier"
    const val ROLE_FARMER = "Farmer"
    const val ROLE_TRADER = "Trader"
    const val ROLE_SHOPKEEPER = "Shopkeeper"
    const val ROLE_EMPLOYERORG = "EmployerOrg"
    const val ROLE_EMPLOYEE = "Employee"

    // Store the current user's role
    private var currentUserRole: String? = null

    // Function to set the current role when a user logs in
    fun setCurrentRole(role: String) {
        currentUserRole = role
    }

    // Function to retrieve the current role
    fun getCurrentRole(context: Context): String? {
        return SharedPreferencesManager.getUserRole(context)
    }

    // Permissions based on role
    fun hasPermission(context: Context, permission: String): Boolean {
        val role = getCurrentRole(context)
        return when (currentUserRole) {
            ROLE_CHICKS_SUPPLIER -> checkChicksSupplierPermissions(permission)
            ROLE_FARMER -> checkFarmerPermissions(permission)
            ROLE_TRADER -> checkTraderPermissions(permission)
            ROLE_SHOPKEEPER -> checkShopkeeperPermissions(permission)
            ROLE_EMPLOYERORG -> checkEmployerOrgPermissions(permission)
            ROLE_EMPLOYEE -> checkEmploymentPermissions(permission)
            else -> false
        }
    }

    // Define permissions for each role
    private fun checkChicksSupplierPermissions(permission: String): Boolean {
        // List specific permissions for the Chicks Supplier role
        val allowedPermissions = listOf("READ_CHICKS_DATA", "WRITE_CHICKS_DATA")
        return allowedPermissions.contains(permission)
    }

    private fun checkFarmerPermissions(permission: String): Boolean {
        val allowedPermissions = listOf("READ_FARMER_DATA", "WRITE_FARMER_DATA")
        return allowedPermissions.contains(permission)
    }

    private fun checkTraderPermissions(permission: String): Boolean {
        val allowedPermissions = listOf("READ_TRADER_DATA", "WRITE_TRADER_DATA")
        return allowedPermissions.contains(permission)
    }

    private fun checkShopkeeperPermissions(permission: String): Boolean {
        val allowedPermissions = listOf("READ_SHOPKEEPER_DATA", "WRITE_SHOPKEEPER_DATA")
        return allowedPermissions.contains(permission)
    }

    private fun checkEmployerOrgPermissions(permission: String): Boolean {
        val allowedPermissions = listOf("READ_EMPLOYERORG_DATA", "WRITE_EMPLOYERORG_DATA")
        return allowedPermissions.contains(permission)
    }

    private fun checkEmploymentPermissions(permission: String): Boolean {
        val allowedPermissions = listOf("READ_EMPLOYEE_DATA", "WRITE_EMPLOYEE_DATA")
        return allowedPermissions.contains(permission)
    }
}


/*
        // Assuming you have login logic and role determination
        val userRole = "Farmer" // This value would be dynamically determined

        // Set the current role
        UserRoles.setCurrentRole(userRole)


        // Check for permission before performing an action
        if (UserRoles.hasPermission("READ_FARMER_DATA")) {
            // Do something related to Farmer data
        } else {
            // Show a message or disable access
            Toast.makeText(this, "You don't have permission", Toast.LENGTH_SHORT).show()
        }


        fun setupDashboardUI() {
             when (UserRoles.getCurrentRole()) {
                 UserRoles.ROLE_FARMER -> {
                            // Show farmer-specific UI
                    }
                    UserRoles.ROLE_TRADER -> {
                        // Show trader-specific UI
                     }
                        // Add more cases as needed
                }
        }

*/