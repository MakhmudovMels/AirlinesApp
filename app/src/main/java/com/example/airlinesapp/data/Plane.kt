package com.example.airlinesapp.data

import java.util.*
import kotlin.collections.ArrayList

data class Plane(
    val id : UUID = UUID.randomUUID(),
    val date: String="",
    val numberOfSeats: Int,
    val numberOfRows: Int,
    var seats: ArrayList<Seat>
)
