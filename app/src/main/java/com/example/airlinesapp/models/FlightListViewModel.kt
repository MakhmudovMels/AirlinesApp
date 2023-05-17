package com.example.airlinesapp.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.airlinesapp.data.City
import com.example.airlinesapp.data.Flight
import com.example.airlinesapp.repository.AppRepository
import java.util.*

class FlightListViewModel : ViewModel() {
    val city: MutableLiveData<City?> = MutableLiveData()

    private var airlineID: UUID? = null
    private var cityID: UUID? = null

    init {
        AppRepository.get().airlines.observeForever {
            city.postValue(it.find { airline -> airline.id == airlineID }
                ?.cities?.find { city -> city.id == cityID })
        }
    }

    fun setAirlineAndCityID(airlineID: UUID, cityID: UUID) {
        this.airlineID = airlineID
        this.cityID = cityID
        city.postValue(AppRepository.get().airlines.value?.find { airline -> airline.id == airlineID }
            ?.cities?.find { city -> city.id == cityID })
    }

    fun getNameOfCity(): String = city.value?.name ?: ""

    fun newFlight(flight: Flight) = AppRepository.get().newFlight(airlineID, cityID, flight)

    fun deleteFlight(flight: Flight) = AppRepository.get().deleteFlight(airlineID, cityID, flight)

    fun editFlight(flightID: UUID, newFlight: Flight) =
        AppRepository.get().editFlight(airlineID, cityID, flightID, newFlight)

    fun pay_ticket(flightID: UUID, planeDate: String, seatName: String) =
        AppRepository.get().pay_ticket(airlineID, cityID, flightID, planeDate, seatName)
}