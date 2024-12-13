package com.gsggroups.poultrymarket.Common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.gsggroups.poultrymarket.Model.UserItem

object SharedPreferencesManager {

    private const val PREFS_NAME = "UserPreferences"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_SIGN_IN = "KEY_SIGN_IN"
    private const val KEY_RATES = "KEY_RATES"

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
}