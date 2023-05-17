package com.example.airlinesapp.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.airlinesapp.data.Airline
import com.example.airlinesapp.data.City
import com.example.airlinesapp.repository.AppRepository
import java.util.*

class CityListViewModel : ViewModel() {
    val airline: MutableLiveData<Airline?> = MutableLiveData()

    private var airlineID: UUID? = null

    init {
        AppRepository.get().airlines.observeForever {
            airline.postValue(it.find { airline -> airline.id == airlineID })
        }
    }

    fun setAirlineId(airlineID: UUID) {
        this.airlineID = airlineID
        airline.postValue(AppRepository.get().airlines.value?.find { airline -> airline.id == airlineID })
    }

    fun newCity(airlineId: UUID, name: String) = AppRepository.get().newCity(airlineId, name)

    fun editCity(airlineId: UUID, cityId: UUID, name: String) =
        AppRepository.get().editCity(airlineId, cityId, name)

    fun deleteCity(airlineId: UUID, city: City) = AppRepository.get().deleteCity(airlineId, city)
}