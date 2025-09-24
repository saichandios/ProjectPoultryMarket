
data class Person(
    val role: String,
    val name: String,
    val detail: String
)

data class LoginResponse(
    val isSuccess: Boolean,
    val message: String,
    val item: UserDetails
)

data class UserDetails(
    val userID: String,
    val roleID: Int,
    val subscriptionID: Int,
    val name: String,
    val stateID: Int,
    val districtID: Int,
    val cityID: Int,
    val userType: Int,
    val batchReady: Boolean,
    val batchReadyUpdatedDateTime: String? = "",
    val needLoadUpdatedDateTime: String? = "",
    val goingForLoadUpdatedDateTime: String? = "",
    val needLoad: Boolean,
    val goingForLoad: Boolean,
    val henCount: Int,
    val henWeight: Float,
    val deviceToken: String,
    val isDeleted: Boolean,
    val createdDateTime: String,
    val updatedDateTime: String,
    val mobileNumber: String,
    val password: String,
    val pin: String,
    val properties: List<Property>
)

data class Property(
    val propertyID: String,
    val userID: String,
    val address1: String,
    val address2: String,
    val propertyName: String,
    val propertyLat: Int,
    val propertyLong: Int,
    val isDeleted: Boolean,
    val createdDateTime: String,
    val updatedDateTime: String
)

