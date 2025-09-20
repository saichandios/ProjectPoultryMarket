package com.gsggroups.poultrymarket.Common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gsggroups.poultrymarket.Model.UserItem
import com.gsggroups.poultrymarket.Utils.Property

object SharedPreferencesManager {


    private const val PREFS_NAME = "UserPreferences"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_FARM_NAME = "user_farm_name"
    private const val KEY_USER_FARM_ADDR1 = "user_farm_addr1"
    private const val KEY_USER_FARM_ADDR2 = "user_farm_addr2"
    private const val KEY_STATE_ID = "state_id"
    private const val KEY_DISTRICT_ID = "district_id"
    private const val KEY_ROLE_ID = "role_id"
    private const val KEY_PROPERTY_ID = "property_id"
    private const val KEY_PROPERTY_LIST = "KEY_PROPERTY_LIST"
    private const val KEY_SIGN_IN = "KEY_SIGN_IN"
    private const val KEY_RATES = "KEY_RATES"
    private const val KEY_TIME = "KEY_TIME"
    private const val KEY_MOBILINUMBER_LOGIN = "KEY_MOBILENUMBER"
    private const val KEY_PIN_LOGIN = "KEY_PIN"
    private const val LAST_SUBMIT_TIMESTAMP_BATCH = "last_submit_timestamp_BatchReady"
    private const val KEY_HEN_COUNT = "hencount_BatchReady"
    private const val KEY_HEN_COUNT_NEED = "hencount_NeedLoad"
    private const val KEY_HEN_COUNT_GOING = "hencount_GoingForLoad"
    private const val KEY_HEN_SIZE_BATCH = "hensize_BatchReady"
    private const val KEY_HEN_SIZE_NEED = "hensize_NeedLoad"
    private const val KEY_HEN_SIZE_GOING = "hensize_GoingForLoad"
    private const val LAST_SUBMIT_TIMESTAMP_NEEDLOAD = "last_submit_timestamp_NeedLoad"
    private const val LAST_SUBMIT_TIMESTAMP_GOINGLOAD = "last_submit_timestamp_GoingForLoad"
    private const val KEY_SUBMIT_PENDING = "KEY_SUBMIT_PENDING"
    private const val KEY_LOGIN_BATCH = "KEY_LOGIN_BATCH"
    private const val KEY_LOGIN_NEED = "KEY_LOGIN_NEED"
    private const val KEY_LOGIN_GOING = "KEY_LOGIN_GOING"


    fun getStateId(context: Context): Int =
        getPreferences(context).getInt(KEY_STATE_ID, 0)

    fun getDistrictId(context: Context): Int =
        getPreferences(context).getInt(KEY_DISTRICT_ID, 0)

    fun saveStateId(context: Context, stateId: Int) {
        val prefs = getPreferences(context)
        prefs.edit().putInt(KEY_STATE_ID, stateId).apply()
    }
    fun saveDistrictId(context: Context, distrctId: Int) {
        val prefs = getPreferences(context)
        prefs.edit().putInt(KEY_DISTRICT_ID, distrctId).apply()
    }

    fun saveLastSubmitTimeBatch(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(LAST_SUBMIT_TIMESTAMP_BATCH, timestamp).apply()
    }

    fun getLastSubmitTimeBatch(context: Context): String? {
        return getPreferences(context).getString(LAST_SUBMIT_TIMESTAMP_BATCH, null)
    }

    fun saveLoginMobileNUmber(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_MOBILINUMBER_LOGIN, timestamp).apply()
    }

    fun getLoginMobileNumber(context: Context): String? {
        return getPreferences(context).getString(KEY_MOBILINUMBER_LOGIN, null)
    }

    fun saveLoginPIN(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_PIN_LOGIN, timestamp).apply()
    }

    fun getLoginPIN(context: Context): String? {
        return getPreferences(context).getString(KEY_PIN_LOGIN, null)
    }

    fun saveHenCountSubmit(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_HEN_COUNT, timestamp).apply()
    }

    fun getHenCountSubmit(context: Context): String? {
        return getPreferences(context).getString(KEY_HEN_COUNT, null)
    }

    fun saveHenCountSubmitNeed(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_HEN_COUNT_NEED, timestamp).apply()
    }

    fun getHenCountSubmitNeed(context: Context): String? {
        return getPreferences(context).getString(KEY_HEN_COUNT_NEED, null)
    }

    fun saveHenCountSubmitGoing(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_HEN_COUNT_GOING, timestamp).apply()
    }

    fun getHenCountSubmitGoing(context: Context): String? {
        return getPreferences(context).getString(KEY_HEN_COUNT_GOING, null)
    }

    fun saveHenSizeSubmitNeed(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_HEN_SIZE_NEED, timestamp).apply()
    }

    fun clearLastSubmitHensize(context: Context) {
        getPreferences(context).edit().remove(KEY_HEN_SIZE_BATCH).apply()
    }

    fun clearLastSubmitHencount(context: Context) {
        getPreferences(context).edit().remove(KEY_HEN_COUNT_NEED).apply()
    }

    fun getHenSizeSubmitBatch(context: Context): String? {
        return getPreferences(context).getString(KEY_HEN_SIZE_BATCH, null)
    }

    fun saveHenSizeSubmitBatch(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_HEN_SIZE_BATCH, timestamp).apply()
    }

    fun getHenSizeSubmitNeed(context: Context): String? {
        return getPreferences(context).getString(KEY_HEN_SIZE_NEED, null)
    }

    fun saveHenSizeSubmitGoing(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_HEN_SIZE_GOING, timestamp).apply()
    }

    fun getHenSizeSubmitGoing(context: Context): String? {
        return getPreferences(context).getString(KEY_HEN_SIZE_GOING, null)
    }

    fun saveBatchBoolean(context: Context, pending: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_LOGIN_BATCH, pending).apply()
    }

    // Read state
    fun getBatchBoolean(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_LOGIN_BATCH, false)
    }

    fun saveNeedBoolean(context: Context, pending: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_LOGIN_NEED, pending).apply()
    }

    // Read state
    fun getNeedBoolean(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_LOGIN_NEED, false)
    }

    fun saveGoingBoolean(context: Context, pending: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_LOGIN_GOING, pending).apply()
    }

    // Read state
    fun getGoingBoolean(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_LOGIN_GOING, false)
    }


    fun clearLastSubmitTimeBatch(context: Context) {
        getPreferences(context).edit().remove(LAST_SUBMIT_TIMESTAMP_BATCH).apply()
    }

    fun clearhenCount(context: Context) {
        getPreferences(context).edit().remove(KEY_HEN_COUNT).clear()
    }

    fun clearHenSize(context: Context) {
        getPreferences(context).edit().remove(KEY_HEN_SIZE_BATCH).clear()
    }

    fun clearBatchBoolean(context: Context) {
        getPreferences(context).edit().remove(LAST_SUBMIT_TIMESTAMP_BATCH).apply()
    }

    fun saveLastSubmitTimeNeed(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(LAST_SUBMIT_TIMESTAMP_NEEDLOAD, timestamp).apply()
    }

    fun getLastSubmitTimeNeed(context: Context): String? {
        return getPreferences(context).getString(LAST_SUBMIT_TIMESTAMP_NEEDLOAD, null)
    }

    fun clearLastSubmitTimeNeed(context: Context) {
        getPreferences(context).edit().remove(LAST_SUBMIT_TIMESTAMP_NEEDLOAD).apply()
    }

    fun saveLastSubmitTimeGoing(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(LAST_SUBMIT_TIMESTAMP_GOINGLOAD, timestamp).apply()
    }

    fun getLastSubmitTimeGoing(context: Context): String? {
        return getPreferences(context).getString(LAST_SUBMIT_TIMESTAMP_GOINGLOAD, null)
    }

    fun clearLastSubmitTimeGoing(context: Context) {
        getPreferences(context).edit().remove(LAST_SUBMIT_TIMESTAMP_GOINGLOAD).apply()
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    //signed In
    fun saveSignedIn(context: Context, signIn: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_SIGN_IN, signIn)
        editor.apply()
    }

    // Retrieve sign-in status
    fun getSignedIn(context: Context): Boolean {
        val sharedPreferences = getPreferences(context)
        return sharedPreferences.getBoolean(KEY_SIGN_IN, false) // Default to false if not found
    }

    // Save user role in SharedPreferences
    fun saveUserRole(context: Context, role: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_ROLE, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getUserRole(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ROLE, null)
    }

    // Save user_id in SharedPreferences
    fun saveUserID(context: Context, role: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_ID, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getUserId(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ID, null)
    }

    fun saveUserName(context: Context, role: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_NAME, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getUserName(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_NAME, null)
    }

    fun saveUserForm(context: Context, role: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_FARM_NAME, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getUserForm(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_FARM_NAME, null)
    }

    fun saveUserFormAddress1(context: Context, role: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_FARM_ADDR1, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getUserFormAdress1(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_FARM_ADDR1, null)
    }
    fun saveUserFormAddress2(context: Context, role: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_FARM_ADDR2, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getUserFormAdress2(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_FARM_ADDR2, null)
    }

    // Save roll_id in SharedPreferences
    fun saveRoleID(context: Context, role: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(KEY_ROLE_ID, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getRoleId(context: Context): Int? {
        return getPreferences(context).getInt(KEY_ROLE_ID, 0)
    }

    // Save roll_id in SharedPreferences
    fun savePropertyID(context: Context, role: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_PROPERTY_ID, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getPropertyId(context: Context): String? {
        return getPreferences(context).getString(KEY_PROPERTY_ID, "")
    }

    // Save property list
    fun savePropertyList(context: Context, properties: List<Property>) {
        val gson = Gson()
        val json = gson.toJson(properties)
        val editor = getPreferences(context).edit()
        editor.putString("KEY_PROPERTY_LIST", json)
        editor.apply()
    }

    // Retrieve property list
    fun getPropertyList(context: Context): List<Property> {
        val json = getPreferences(context).getString(KEY_PROPERTY_LIST, null) ?: return emptyList()
        val type = object : TypeToken<List<Property>>() {}.type
        return Gson().fromJson(json, type)
    }


    // Save user role in SharedPreferences
    fun saveRatesCard(context: Context, role: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_RATES, role)
        editor.apply()
    }

    // Retrieve the saved user role
    fun getRatesCard(context: Context): String? {
        return getPreferences(context).getString(KEY_RATES, null)
    }

    // Clear user role (useful for logout or role change)
    fun clearSignedIn(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_SIGN_IN)
        editor.apply()
    }

    // Clear user role (useful for logout or role change)
    fun clearUserRole(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_USER_ROLE)
        editor.apply()
    }

    // Clear user role (useful for logout or role change)
    fun clearRoleId(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_ROLE_ID)
        editor.apply()
    }

    // Clear user role (useful for logout or role change)
    fun clearRates(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_RATES)
        editor.apply()
    }

    // Save user data as a JSON string
    fun saveUserData(context: Context, response: Any) {
        val editor = getPreferences(context).edit()
        editor.putString("userData", Gson().toJson(response)) // Convert response to JSON string
        editor.apply()
    }

    // Retrieve user data
    fun getUserData(context: Context): UserItem? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userDataJson = sharedPreferences.getString("userData", null)
        return if (userDataJson != null) {
            Gson().fromJson(userDataJson, UserItem::class.java)
        } else {
            null
        }
    }

    fun saveLastSubmissionTime(context: Context, batchReadyUpdatedDateTime: String?) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_TIME, batchReadyUpdatedDateTime).apply()
    }

    fun getLastSubmissionTime(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TIME, null)
    }

    fun clearAll(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

}