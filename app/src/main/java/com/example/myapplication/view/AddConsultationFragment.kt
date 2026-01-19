package com.example.myapplication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.controller.NetworkManager
import com.example.myapplication.databinding.FragmentAddConsultationBinding
import com.example.myapplication.model.CAPRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddConsultationFragment : Fragment() {
    private var _binding: FragmentAddConsultationBinding? = null
    private val binding get() = _binding!!
    private lateinit var networkManager: NetworkManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddConsultationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mainActivity = activity as? MainActivity
        if (mainActivity == null) {
            return
        }
        
        networkManager = mainActivity.getNetworkManager()

        binding.dateEditText.setOnClickListener {
            showDatePicker()
        }

        binding.timeEditText.setOnClickListener {
            showTimePicker()
        }
        
        binding.addButton.setOnClickListener {
            addConsultation()
        }
    }

    private fun showDatePicker() {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        val datePickerDialog = android.app.DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.dateEditText.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)

        val timePickerDialog = android.app.TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.timeEditText.setText(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }
    
    private fun addConsultation() {
        val date = binding.dateEditText.text.toString().trim()
        val time = binding.timeEditText.text.toString().trim()
        val countText = binding.countEditText.text.toString().trim()
        
        if (date.isEmpty() || time.isEmpty() || countText.isEmpty()) {
            Toast.makeText(context, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }
        
        val count = countText.toIntOrNull()
        if (count == null || count <= 0) {
            Toast.makeText(context, "Le nombre de consultations doit être supérieur à 0", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.addButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val request = CAPRequest.AddConsultationRequest(
                    date = date,
                    hour = time,
                    duration = 30,
                    consecutiveCount = count
                )
                
                val responseResult = networkManager.sendRequest(request)
                
                responseResult.onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        binding.addButton.isEnabled = true
                        
                        if (response.success) {
                            Toast.makeText(context, getString(R.string.success_consultation_added), Toast.LENGTH_SHORT).show()
                            binding.dateEditText.text?.clear()
                            binding.timeEditText.text?.clear()
                            binding.countEditText.text?.clear()
                        } else {
                            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }.onFailure { error ->
                    withContext(Dispatchers.Main) {
                        binding.addButton.isEnabled = true
                        var errorMsg = "Erreur"
                        if (error.message != null) {
                            errorMsg = error.message!!
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.addButton.isEnabled = true
                    var errorMsg = "Erreur"
                    if (e.message != null) {
                        errorMsg = e.message!!
                    }
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
