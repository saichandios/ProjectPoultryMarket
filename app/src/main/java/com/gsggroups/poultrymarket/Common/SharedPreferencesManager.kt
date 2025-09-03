package com.gsggroups.poultrymarket.Common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.gsggroups.poultrymarket.Model.UserItem

object SharedPreferencesManager {

    private const val PREFS_NAME = "UserPreferences"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_ROLE_ID = "role_id"
    private const val KEY_PROPERTY_ID = "property_id"
    private const val KEY_SIGN_IN = "KEY_SIGN_IN"
    private const val KEY_RATES = "KEY_RATES"
    private const val KEY_TIME = "KEY_TIME"
    private const val KEY_MOBILINUMBER_LOGIN = "KEY_MOBILENUMBER"
    private const val KEY_PIN_LOGIN = "KEY_PIN"
    private const val LAST_SUBMIT_TIMESTAMP_BATCH = "last_submit_timestamp_BatchReady"
    private const val KEY_HEN_COUNT = "hencount_BatchReady"
    private const val KEY_HEN_SIZE = "hensize_BatchReady"
    private const val LAST_SUBMIT_TIMESTAMP_NEEDLOAD = "last_submit_timestamp_NeedLoad"
    private const val LAST_SUBMIT_TIMESTAMP_GOINGLOAD = "last_submit_timestamp_GoingForLoad"

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

    fun saveHenSizeSubmit(context: Context, timestamp: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_HEN_SIZE, timestamp).apply()
    }

    fun getHenSizeSubmit(context: Context): String? {
        return getPreferences(context).getString(KEY_HEN_SIZE, null)
    }

    fun clearLastSubmitTimeBatch(context: Context) {
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
    fun getLastSubmissionTime(context: Context): String ?{
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TIME, null)
    }
}