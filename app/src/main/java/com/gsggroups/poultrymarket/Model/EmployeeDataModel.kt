package com.gsggroups.poultrymarket.Model

data class Employee(
    val empId: String,
    val name: String,
    val mobile: String,
    val pin: String,
    val address1: String,
    val address2: String,
    val stateID: String,
    val districtID: String,
    val cityID: String,
    val latitude: Double,
    val longitude: Double,
    val userType: Int
)

data class Notification(
    val notificationId: String,
    val henCount: Int, // Number of hens
    val henWeight: Float, // Weight of hens (1.0 to 5.3)
    val districtId: String, // District ID as a JSON object or string representation
    val receivedNote: String // Notification message or content
)

data class Rate(
    val rateId: String,
    val districtID: String, // Unique identifier for the district
    val districtName: String, // Name of the district
    val rateLifting: Float, // Rate for lifting
    val retailRate: Float, // Retail rate
    val rateSkin: Float, // Rate for skin
    val rateSkinless: Float // Rate for skinless
)

data class SubmitLoadRequest(
    val userId: String,
    val propertyId: String,
    val distrcitIds: List<Int>,
    val message: String,
    val roleId: Int,
    val henCount: Int,
    val henWeight: Float,
    val goingForLoad: Boolean = false,
    val needLoad: Boolean = false,
    val loadAvailable: Boolean = false
)

data class SubmitLoadResponse(
    val isSuccess: Boolean,
    val message: String,
    val item: Boolean
)
