package com.gsggroups.poultrymarket


import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class UserInfo(context: Context) {

    // Inflate the layout
    private val layoutInflater = LayoutInflater.from(context)
    val view: View = layoutInflater.inflate(R.layout.nav_header, null)

    // Get references to the views
    private val roundImageView: ImageView = view.findViewById(R.id.roundImageView)
    private val userNameTextView: TextView = view.findViewById(R.id.user_name)
    private val userEmailTextView: TextView = view.findViewById(R.id.user_mobile)

    /**
     * Update the user info layout with dynamic data.
     *
     * @param userName The name of the user.
     * @param userEmail The email of the user.
     * @param userImage A Drawable for the user's profile image. If null, a placeholder will be used.
     * @param showSeparator Whether to show the separator line.
     */
    fun updateUserInfo(
        userName: String,
        userEmail: String,
        userImage: Drawable? = null,
    ) {
        // Set user name
        userNameTextView.text = userName

        // Set user email
        userEmailTextView.text = userEmail

        // Set user image or placeholder
        roundImageView.setImageDrawable(userImage ?: ContextCompat.getDrawable(view.context, R.drawable.hen3))
    }
}