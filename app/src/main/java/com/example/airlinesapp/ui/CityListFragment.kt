package com.example.airlinesapp.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.airlinesapp.R
import com.example.airlinesapp.data.Airline
import com.example.airlinesapp.data.City
import com.example.airlinesapp.databinding.FragmentCityListBinding
import com.example.airlinesapp.models.CityListViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

const val CITY_LIST_TAG = "CityListFragment"

class CityListFragment private constructor(): Fragment() {

    private var _binding: FragmentCityListBinding? = null

    private val binding
        get() = _binding!!

    companion object {
        private lateinit var id: UUID
        private var _city: City? = null
        fun newInstance(id: UUID): CityListFragment {
            this.id = id
            return CityListFragment()
        }

        val getAirlineId
            get() = id
    }

    private lateinit var viewModel: CityListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CityListViewModel::class.java]
        viewModel.setAirlineId(getAirlineId)
        viewModel.airline.observe(viewLifecycleOwner) {
            updateUI(it)
            callbacks?.setTitle(it?.name ?: "")
        }
        binding.cityAddBtn.setOnClickListener {
            editCreateDialog(null)
        }
        binding.cityEditBtn.setOnClickListener {
            if(binding.tabCity.tabCount > 0)
                editCreateDialog(_city)
            else
                Toast.makeText(requireContext(), "Список городов пуст.", Toast.LENGTH_SHORT).show()
        }
        binding.cityDelBtn.setOnClickListener {
            if(binding.tabCity.tabCount > 0)
                commitDeleteDialog(_city!!)
            else
                Toast.makeText(requireContext(), "Список городов пуст.", Toast.LENGTH_SHORT).show()
        }
    }

    private var tabPosition: Int = 0

    private fun updateUI(airline: Airline?) {
        binding.tabCity.clearOnTabSelectedListeners()
        binding.tabCity.removeAllTabs()
        for (i in 0 until (airline?.cities?.size ?: 0)) {
            binding.tabCity.addTab(binding.tabCity.newTab().apply {
                text = i.toString()
            })
        }

        val adapter = GroupPageAdapter(requireActivity(), airline!!)
        binding.vpCity.adapter = adapter
        TabLayoutMediator(binding.tabCity, binding.vpCity, true, true) { tab, pos ->
            tab.text = airline.cities.get(pos).name
        }.attach()
        if (tabPosition < binding.tabCity.tabCount)
            binding.tabCity.selectTab(binding.tabCity.getTabAt(tabPosition))
        else
            binding.tabCity.selectTab(binding.tabCity.getTabAt(tabPosition - 1))
        if ((airline.cities.size) > 0){
            _city = airline.cities[tabPosition]
        }
        binding.tabCity.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position!!
                _city = airline.cities[tabPosition]
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private inner class GroupPageAdapter(fa: FragmentActivity, private val airline: Airline) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return airline.cities.size
        }

        override fun createFragment(position: Int): Fragment {
            return FlightListFragment.newInstance(airline.id, airline.cities[position].id)
        }
    }

    private fun editCreateDialog(city: City?){
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.city_dialog, null)
        builder.setView(dialogView)
        val etCityName = dialogView.findViewById(R.id.etCityName) as EditText
        if(city != null){
            builder.setTitle("Редактирование города")
            etCityName.setText(city.name)
        }
        else
            builder.setTitle("Добавление города")
        builder.setPositiveButton(getString(R.string.commit)) { _, _, ->
            var p = true
            etCityName.text.toString().trim().ifBlank {
                p = false
                etCityName.error = "Укажите значение"
            }
            if (p) {
                if(city != null) {
                    for (flight in city.flights)
                        flight.departureCity = etCityName.text.toString().trim()
                    viewModel.editCity(getAirlineId, city.id, etCityName.text.toString().trim())
                    Toast.makeText(requireContext(), "Город успешно обновлён.", Toast.LENGTH_SHORT).show()
                }
                else {
                    viewModel.newCity(getAirlineId, etCityName.text.toString().trim())
                    Toast.makeText(requireContext(), "Город успешно добавлен.", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton(R.string.cancel, null)
        val alert = builder.create()
        alert.show()
    }

    private fun commitDeleteDialog(city: City) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить город ${city.name} из списка?")
        builder.setTitle("Подтверждение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            viewModel.deleteCity(getAirlineId, city)
            Toast.makeText(requireContext(), "Город успешно удалён.", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    interface Callbacks {
        fun setTitle(_title: String)
    }

    var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }
}