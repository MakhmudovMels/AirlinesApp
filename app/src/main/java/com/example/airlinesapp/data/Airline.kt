package com.example.airlinesapp.data

import java.util.*

data class Airline(
    val id : UUID = UUID.randomUUID(),
    var name : String="",
    var year : Int) {
    var cities: List<City> = emptyList()
}
