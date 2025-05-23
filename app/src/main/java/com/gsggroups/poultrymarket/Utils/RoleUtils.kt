package com.gsggroups.poultrymarket.Utils

fun getRoleName(roleId: Int): String {
    return when (roleId) {
        333 -> "Admin"
        336194569 -> "Farmer"
        963186725 -> "Shopkeeper"
        632914599 -> "Trader"
        else -> "Unknown"
    }
}