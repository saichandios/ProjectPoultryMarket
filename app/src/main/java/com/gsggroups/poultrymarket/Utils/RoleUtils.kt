package com.gsggroups.poultrymarket.Utils

fun getRoleName(roleId: Int): String {
    return when (roleId) {
        0 -> "Admin"
        1 -> "Farmer"
        2 -> "Shopkeeper"
        3 -> "Trader"
        else -> "Unknown"
    }
}