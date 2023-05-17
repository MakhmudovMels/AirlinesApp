package com.example.airlinesapp.data

data class Seat(
    val name: String="",
    var isFree: Boolean=true
){
    override fun toString(): String {
        return name
    }
}
