package com.example.landmarkremark.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Util {
    companion object {

        /**
         * Restart application
         * */
        fun restartApplication(context: Context) {
            // Get the package manager
            val packageManager = context.packageManager

            // Get the main activity of the application
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)

            // Create a new task for the main activity
            if (intent != null) {
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            or Intent.FLAG_ACTIVITY_NEW_TASK
                )
                context.startActivity(intent)
            }
        }

        /**
         * Check the permission to access the current location of user
         * */
        fun isLocationPermissionAccess(context: Context) : Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * convert timestamp to date
         */
        fun timestampToDate(timestamp: Long): String {
            val date = Date(timestamp)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

            return dateFormat.format(date)
        }

        /**
         * Toggle the click event of view
         */
        fun enableViewClickEvent(isEnable: Boolean, view: View) {
            view.isClickable = isEnable
            view.isFocusable = isEnable
            view.isEnabled = isEnable
        }

        /**
         * Hide keyoard
         */
        fun hideKeyboard(view: View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}