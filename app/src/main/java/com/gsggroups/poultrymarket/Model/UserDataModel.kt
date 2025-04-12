package com.gsggroups.poultrymarket.Model

data class UserRequest(
    val userID: String = "",
    val name: String = "",
    val mobileNumber: String = "",
    val pin: String = "",
    val stateID: String = "",
    val districtID: String = "",
    val cityID: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val batchReady: Boolean = false,
    val needLoad: Boolean = false,
    val goingForLoad: Boolean = false,
    val henCount: Int = 0,
    val henWeight: Float = 1.0f,
    val deviceToken: String = "",

    val roleID: Int = 0,
    val propertyList: List<PropertyRequest> = emptyList(),
    val subscriptionID: Int = 0,
    val password: String = "",
    val isDeleted: Boolean = false,
    val createdDateTime: String = "",
    val updatedDateTime: String = ""
)

data class PropertyRequest(
    val address1: String,
    val address2: String,
    val propertyName: String,
    val propertyLat: Double,
    val propertyLong: Double,
    val isDeleted: Boolean,
    val createdDateTime: String,
    val updatedDateTime: String
)


//set passcode screen
data class PasscodeRequest(
    val phone: String,
    val otp: String,
    val pin: String
)

data class State(
    val name: String,
    val districts: List<DistrictRates>
)

data class District(
    val name: String,
    val lifting: Int,
    val retail: Int,
    val skin: Int,
    val skinless: Int
)

data class DistrictRates(
    val stateName: String = "",
    val liftingRate: Double = 0.0,
    val retailRate: Double = 0.0,
    val skinRate: Double = 0.0,
    val skinLessRate: Double = 0.0,
    val eggRate: Double = 0.0
)
//===========================
 data class ApiResponse(
    val isSuccess: Boolean,
    val message: String,
    val item: UserItem
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
    val needLoad: Boolean,
    val goingForLoad: Boolean,
    val henCount: Int,
    val henWeight: Int,
    val mobileNumber: String,
    val propertyList: List<Property>,
    val subscriptionID: Int,
    val deviceToken: String,
    val password: String,
    val pin: String,
    val isDeleted: Boolean,
    val createdDateTime: String,
    val updatedDateTime: String
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

//--------------------------******** LIST **********----------------------------------------

data class ListRoleRequest(
    val roleId: Int = 0,
    val pageNumber: Int = 1,
    val pageSize: Int = 1,
    val search: String = "",
    val sortColumn: String = "",
    val sortDirection: String = "",
    val stateId: Int = 0,
    val districtId: Int = 0,

    val batchReady: Boolean = false,
    val needLoad: Boolean = false,
    val goingForLoad: Boolean = false
)


data class ListResponseModel(
    val isSuccess: Boolean = false,
    val message: String = "",
    val item: ItemModel = ItemModel()
)

data class ItemModel(
    val items: List<UserModel> = emptyList()
)

data class UserModel(
    val userID: String = "",
    val roleID: Int = 0,
    val name: String = "",
    val stateID: Int = 0,
    val districtID: Int = 0,
    val cityID: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val batchReady: Boolean = false,
    val needLoad: Boolean = false,
    val goingForLoad: Boolean = false,
    val henCount: Int = 0,
    val henWeight: Double = 0.0,
    val mobileNumber: String = "",
    val propertyList: List<PropertyModel> = emptyList(),

    //testing
    val role: String,
    val detail: String,
    val detail2: String,
    val status: String,
    var colorTemp: String = "bgColor"

)

data class PropertyModel(
    val propertyID: String = "",
    val userID: String = "",
    val address1: String = "",
    val address2: String = "",
    val propertyName: String = "",
    val propertyLat: Double = 0.0,
    val propertyLong: Double = 0.0,
    val createdDateTime: String = "",
    val updatedDateTime: String = "",
    val isDeleted: Boolean = false
)


//======================================================
//*********** sample Data
data class RatesResponse(
    val districtRates: List<DistrictRates>,
    val states: List<State>
)


data class LoginRequest(
    val mobileNumber: String,
    val pin: String
)

