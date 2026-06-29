package com.example.mad

import com.google.firebase.database.PropertyName

data class Medicine(
    val id: String = "",
    val name: String = "",
    val quantity: String = "",
    val type: String = "",
    val unit: String = "",
    val frequency: String = "",
    val time: String = "",
    val startDate: String = "",
    val endDate: String = "",
    
    @get:PropertyName("isTaken")
    @set:PropertyName("isTaken")
    var isTaken: Boolean = false,
    
    @get:PropertyName("isSkipped")
    @set:PropertyName("isSkipped")
    var isSkipped: Boolean = false,
    
    val typeIconName: String = "Tablet"
)

