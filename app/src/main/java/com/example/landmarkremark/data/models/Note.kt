package com.example.landmarkremark.data.models

import com.example.landmarkremark.base.Util.Companion.timestampToDate

data class Note (
    var noteId: String = "",
    var userId: String = "",
    var userEmail: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var content: String = "",
    var timestamp: Long = 0
) {
    fun getFormattedTimestamp(): String {
        return timestampToDate(timestamp)
    }

    fun getUserAndTime(): String {
        return "$userEmail - ${getFormattedTimestamp()}"
    }
}