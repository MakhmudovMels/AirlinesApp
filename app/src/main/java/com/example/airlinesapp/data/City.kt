package com.example.airlinesapp.data

import java.util.*

data class City(
    val id : UUID = UUID.randomUUID(),
    var name : String="") {
    var flights: List<Flight> = emptyList()
}