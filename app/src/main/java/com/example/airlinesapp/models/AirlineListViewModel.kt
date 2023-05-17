package com.example.airlinesapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.airlinesapp.data.Airline
import com.example.airlinesapp.repository.AppRepository
import java.util.*

class AirlineListViewModel : ViewModel() {
    var airlines: MutableLiveData<List<Airline>> = MutableLiveData()

    init {
        AppRepository.get().airlines.observeForever {
            airlines.postValue(it)
        }
    }

    fun deleteAirline(airline: Airline) = AppRepository.get().deleteAirline(airline)

    fun editAirline(id: UUID, name: String, year: Int) = AppRepository.get().editAirline(id, name, year)

    fun newAirline(name: String, year: Int) = AppRepository.get().newAirline(name, year)
}