package com.krotolamo.mobileappk

import java.util.HashMap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class SessionManager// Constructor
    (internal var _context: Context) {
    // Shared Preferences
    internal var pref: SharedPreferences

    // Editor for Shared preferences
    internal var editor: Editor

    // Shared pref mode
    internal var PRIVATE_MODE = 0

    companion object {

        // Sharedpref file name
        private val PREF_NAME = "AndroidHivePref"

        // All Shared Preferences Keys
        private val IS_LOGIN = "IsLoggedIn"

        val KEY_TOKEN = "user"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        editor.commit()
    }

    /**
     * Create login session
     */
    fun createLoginSession(token: String) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_TOKEN, token)

        // commit changes
        editor.commit()
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    fun checkLogin() {
        // Check login status
        if (!this.isLoggedIn) {
            // user is not logged in redirect him to Login Activity
            val i = Intent(_context, MainActivity::class.java)
            // Staring Login Activity
            _context.startActivity(i)
        }

    }


    /**
     * Get stored session data
     */
    val userDetails: String
        get() {
            return pref.getString(KEY_TOKEN, null)
        }

    /**
     * Quick check for login
     */
    // Get Login State
    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    /**
     * Clear session details
     */
    fun logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()

        // After logout redirect user to Loing Activity
        val i = Intent(_context, MainActivity::class.java)
        // Staring Login Activity
        _context.startActivity(i)
    }
}