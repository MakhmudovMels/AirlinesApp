package com.example.airlinesapp

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.airlinesapp.repository.AppRepository
import com.example.airlinesapp.ui.*
import java.util.*


class MainActivity : AppCompatActivity(), AirlineListFragment.Callbacks,
    CityListFragment.Callbacks, FlightListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppRepository.newInstance()
        AppRepository.get().loadData(this)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, AirlineListFragment.newInstance(), AIRLINE_LIST_TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else
                    finish()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        AppRepository.get().saveData(this)
    }

    override fun setTitle(_title: String) {
        title = _title
    }

    override fun showAirline(id: UUID) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, CityListFragment.newInstance(id), CITY_LIST_TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }

}