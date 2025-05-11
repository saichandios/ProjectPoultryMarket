package com.gsggroups.poultrymarket.Common

import android.content.Context

object UserRoles {

    // Role Names
    const val ROLE_ADMIN = "Admin"
    const val ROLE_FARMER = "Farmer"
    const val ROLE_TRADER = "Trader"
    const val ROLE_SHOPKEEPER = "Shopkeeper"
    const val ROLE_CHICKS_SUPPLIER = "Chicks"
    const val ROLE_FEED = "Feed"
    const val ROLE_EMPLOYERORG = "EmployerOrg"
    const val ROLE_EMPLOYEE = "Employee"
    const val ROLE_CUTTER = "Cutter"
    const val ROLE_DRIVER = "Driver"
    const val ROLE_SUPERVISOR = "Supervisor"
    const val ROLE_OTHER = "Other"

    // Role IDs
    const val ID_ADMIN = 333
    const val ID_FARMER = 336194569
    const val ID_TRADER = 632914599
    const val ID_SHOPKEEPER = 963186725
    const val ID_CHICKS = 338932196
    const val ID_FEED = 696554133
    const val ID_EMPLOYERORG = 999000111 // Example ID
    const val ID_EMPLOYEE = 999000222 // Example ID
    const val ID_CUTTER = 363322599
    const val ID_DRIVER = 649945939
    const val ID_SUPERVISOR = 137594963
    const val ID_OTHER = 936

    // ID to Role Name map
    private val roleNameToIdMap = mapOf(
        ROLE_ADMIN to ID_ADMIN,
        ROLE_FARMER to ID_FARMER,
        ROLE_TRADER to ID_TRADER,
        ROLE_SHOPKEEPER to ID_SHOPKEEPER,
        ROLE_CHICKS_SUPPLIER to ID_CHICKS,
        ROLE_FEED to ID_FEED,
        ROLE_EMPLOYERORG to ID_EMPLOYERORG,
        ROLE_EMPLOYEE to ID_EMPLOYEE,
        ROLE_CUTTER to ID_CUTTER,
        ROLE_DRIVER to ID_DRIVER,
        ROLE_SUPERVISOR to ID_SUPERVISOR,
        ROLE_OTHER to ID_OTHER
    )
    private val roleIdToNameMap = roleNameToIdMap.entries.associate { (k, v) -> v to k }

    fun getRoleIdByName(roleName: String): Int {
        return roleNameToIdMap[roleName] ?: -1 // -1 for unknown/invalid role
    }

    fun getRoleNameById(roleId: Int): String? {
        return roleIdToNameMap[roleId]
    }

    private var currentUserRole: String? = null

    fun getCurrentRole(context: Context): String? {
        return SharedPreferencesManager.getUserRole(context)
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        val role = getCurrentRole(context)
        return when (role) {
            ROLE_ADMIN -> checkAdminPermissions(permission)
            ROLE_FARMER -> checkFarmerPermissions(permission)
            ROLE_TRADER -> checkTraderPermissions(permission)
            ROLE_SHOPKEEPER -> checkShopkeeperPermissions(permission)
            ROLE_CHICKS_SUPPLIER -> checkChicksPermissions(permission)
            ROLE_FEED -> checkFeedPermissions(permission)
            ROLE_EMPLOYERORG -> checkEmployerOrgPermissions(permission)
            ROLE_EMPLOYEE -> checkEmployeePermissions(permission)
            ROLE_CUTTER -> checkCutterPermissions(permission)
            ROLE_DRIVER -> checkDriverPermissions(permission)
            ROLE_SUPERVISOR -> checkSupervisorPermissions(permission)
            ROLE_OTHER -> checkOtherPermissions(permission)
            else -> false
        }
    }

    // Permission sets
    private fun checkAdminPermissions(permission: String) = listOf(
        "ALL_ACCESS", "MANAGE_USERS", "VIEW_REPORTS"
    ).contains(permission)

    private fun checkFarmerPermissions(permission: String) = listOf(
        "READ_FARMER_DATA", "WRITE_FARMER_DATA", "VIEW_MARKET_RATES"
    ).contains(permission)

    private fun checkTraderPermissions(permission: String) = listOf(
        "READ_TRADER_DATA", "WRITE_TRADER_DATA", "VIEW_TRADES"
    ).contains(permission)

    private fun checkShopkeeperPermissions(permission: String) = listOf(
        "READ_SHOPKEEPER_DATA", "WRITE_SHOPKEEPER_DATA", "MANAGE_PRODUCTS"
    ).contains(permission)

    private fun checkChicksPermissions(permission: String) = listOf(
        "READ_CHICKS_DATA", "WRITE_CHICKS_DATA", "MANAGE_CHICKS_ORDERS"
    ).contains(permission)

    private fun checkFeedPermissions(permission: String) = listOf(
        "READ_FEED_DATA", "WRITE_FEED_DATA", "MANAGE_FEED_ORDERS"
    ).contains(permission)

    private fun checkEmployerOrgPermissions(permission: String) = listOf(
        "READ_EMPLOYERORG_DATA", "WRITE_EMPLOYERORG_DATA", "MANAGE_EMPLOYEES"
    ).contains(permission)

    private fun checkEmployeePermissions(permission: String) = listOf(
        "READ_EMPLOYEE_DATA", "WRITE_EMPLOYEE_DATA", "VIEW_TASKS"
    ).contains(permission)

    private fun checkCutterPermissions(permission: String) = listOf(
        "READ_CUTTER_DATA", "MARK_CUTTING_DONE"
    ).contains(permission)

    private fun checkDriverPermissions(permission: String) = listOf(
        "READ_DRIVER_DATA", "MARK_DELIVERY_DONE"
    ).contains(permission)

    private fun checkSupervisorPermissions(permission: String) = listOf(
        "READ_SUPERVISOR_DATA", "ASSIGN_TASKS"
    ).contains(permission)

    private fun checkOtherPermissions(permission: String) = listOf(
        "READ_OTHER_DATA"
    ).contains(permission)
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


// Set role
UserRoles.setCurrentRole(UserRoles.ROLE_ADMIN)

// Check permission
if (UserRoles.hasPermission(context, "MANAGE_USERS")) {
    // Allow access
}
*/