package com.gsggroups.poultrymarket.Utils
data class ApiResponse<T>(
    val isSuccess: Boolean,
    val message: String,
    val item: T,
    val statusCode: Int? = null,
)


data class UserItem(
    val userID: String,
    val roleID: Int,
    val name: String,
    val stateID: Int,
    val districtID: Int,
    val cityID: Int,
    val latitude: Double,
    val longitude: Double,
    val batchReady: Boolean,
    val batchReadyUpdatedDateTime: String,
    val needLoadUpdatedDateTime: String,
    val goingForLoadUpdatedDateTime: String,
    val needLoad: Boolean,
    val goingForLoad: Boolean,
    val henCount: Int,
    val henWeight: Int,
    val mobileNumber: String,
    val properties: List<Property>
)

data class Property(
    val propertyID: String,
    val userID: String,
    val address1: String,
    val address2: String,
    val propertyName: String,
    val propertyLat: Double,
    val propertyLong: Double,
    val isDeleted: Boolean,
    val createdDateTime: String,
    val updatedDateTime: String
)
