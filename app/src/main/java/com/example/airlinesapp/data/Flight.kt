package com.example.airlinesapp.data

import java.time.DayOfWeek
import java.util.*
import kotlin.collections.ArrayList

data class Flight(
    val id : UUID = UUID.randomUUID(),
    var departureCity: String="",
    var arrivalCity: String="",
    var nameOfPlane: String="",
    var hour: Int,
    var minute: Int,
    var dayOfWeek: String="",
    var flightTime: Int,
    var planes: ArrayList<Plane>
)