package com.example.airlinesapp.ui

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.airlinesapp.databinding.FragmentAirlineListBinding
import com.example.airlinesapp.models.AirlineListViewModel
import com.example.airlinesapp.R
import com.example.airlinesapp.data.Airline
import java.util.*

const val AIRLINE_LIST_TAG = "AirlineListFragment"
const val AIRLINE_LIST_TITLE = "Авиакомпании"

class AirlineListFragment : Fragment() {
    private lateinit var viewModel: AirlineListViewModel
    private var _binding: FragmentAirlineListBinding? = null
    private val binding
        get() = _binding!!

    private var adapter: AirlineListAdapter = AirlineListAdapter(emptyList())

    companion object {
        fun newInstance() = AirlineListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAirlineListBinding.inflate(inflater, container, false)
        //отображение по вертикали
        binding.rvAirline.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AirlineListViewModel::class.java]
        viewModel.airlines.observe(viewLifecycleOwner) {
            adapter = AirlineListAdapter(it)
            binding.rvAirline.adapter = adapter
        }
        callbacks?.setTitle(AIRLINE_LIST_TITLE)

        binding.airlineAddBtn.setOnClickListener {
            editCreateDialog(null)
            Log.d("AirlineListFragment", "message")
        }
    }

    private inner class AirlineHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        lateinit var airline: Airline

        fun bind(airline: Airline) {
            this.airline = airline
            itemView.findViewById<TextView>(R.id.tvElement).text = airline.name
            itemView.findViewById<ConstraintLayout>(R.id.crudButtons).visibility = View.GONE
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val cl = itemView.findViewById<ConstraintLayout>(R.id.crudButtons)
            cl.visibility = View.VISIBLE
            lastItemView?.findViewById<ConstraintLayout>(R.id.crudButtons)?.visibility = View.GONE
            lastItemView = if (lastItemView == itemView) null else itemView
            if (cl.visibility == View.VISIBLE) {
                itemView.findViewById<ImageButton>(R.id.openBtn).setOnClickListener {
                    callbacks?.showAirline(airline.id)
                }
                itemView.findViewById<ImageButton>(R.id.delBtn).setOnClickListener {
                    commitDeleteDialog(airline)
                }
                itemView.findViewById<ImageButton>(R.id.editBtn).setOnClickListener {
                    editCreateDialog(airline)
                }
            }
        }
    }

    private var lastItemView: View? = null

    private fun editCreateDialog(airline: Airline?){
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.airline_dialog, null)
        builder.setView(dialogView)

        val etAirlineName = dialogView.findViewById(R.id.etAirlineName) as EditText
        val etAirlineYear = dialogView.findViewById(R.id.etAirlineYear) as EditText

        if(airline != null){
            builder.setTitle("Редактирование авиакомпании")
            etAirlineName.setText(airline.name)
            etAirlineYear.setText(airline.year.toString())
        }
        else
            builder.setTitle("Добавление авиакомпании")
        builder.setPositiveButton(getString(R.string.commit)) { _, _, ->
            var p = true
            etAirlineName.text.toString().trim().ifBlank {
                p = false
                etAirlineName.error = "Укажите значение"
            }
            etAirlineYear.text.toString().trim().ifBlank {
                p = false
                etAirlineYear.error = "Укажите значение"
            }
            if (p) {
                if(airline != null) {
                    viewModel.editAirline(
                        airline.id,
                        etAirlineName.text.toString().trim(),
                        etAirlineYear.text.toString().trim().toInt()
                    )
                    Toast.makeText(requireContext(), "Авиакомпания успешно обновлена.", Toast.LENGTH_SHORT).show()
                }
                else {
                    viewModel.newAirline(
                        etAirlineName.text.toString().trim(),
                        etAirlineYear.text.toString().trim().toInt()
                    )
                    Toast.makeText(requireContext(), "Авиакомпания успешно добавлена.", Toast.LENGTH_SHORT).show()
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

    private fun commitDeleteDialog(airline: Airline) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить авиакомпанию ${airline.name} из списка?")
        builder.setTitle("Подтверждение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            viewModel.deleteAirline(airline)
            Toast.makeText(requireContext(), "Авиакомпания успешно удалена.", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    private inner class AirlineListAdapter(private val items: List<Airline>) :
        RecyclerView.Adapter<AirlineHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AirlineHolder {
            val view = layoutInflater.inflate(R.layout.element_list, parent, false)
            return AirlineHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: AirlineHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    interface Callbacks {
        fun setTitle(_title: String)
        fun showAirline(id: UUID)
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