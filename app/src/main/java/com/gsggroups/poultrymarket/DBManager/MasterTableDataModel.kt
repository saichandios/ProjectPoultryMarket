package com.gsggroups.poultrymarket.DBManager

data class State(
    val stateId: String = "",
    val stateName: String = ""
)

data class District(
    val id: String = "",
    val stateId: String = "",
    val districtName: String = ""
)

data class City(
    val id: String = "",
    val districtId: String = "",
    val stateId: String = "",
    val cityName: String = ""
)
