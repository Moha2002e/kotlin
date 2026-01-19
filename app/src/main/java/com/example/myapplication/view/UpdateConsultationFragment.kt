package com.example.myapplication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.controller.NetworkManager
import com.example.myapplication.databinding.FragmentUpdateConsultationBinding
import com.example.myapplication.model.CAPRequest
import hepl.fead.model.entity.Consultation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class UpdateConsultationFragment : Fragment() {
    private var _binding: FragmentUpdateConsultationBinding? = null
    private val binding get() = _binding!!
    private lateinit var networkManager: NetworkManager
    private var consultationId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateConsultationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as? MainActivity
        if (mainActivity == null) {
            return
        }
        networkManager = mainActivity.getNetworkManager()

        // Retrieve consultation from arguments
        val consultation = arguments?.getSerializable("consultation") as? Consultation
        if (consultation != null) {
            consultationId = consultation.getId()
            binding.dateEditText.setText(consultation.getDate() ?: "")
            binding.timeEditText.setText(consultation.getHour() ?: "")
            binding.patientIdEditText.setText(consultation.getPatient_id()?.toString() ?: "")
            binding.reasonEditText.setText(consultation.getReason() ?: "")
        }

        binding.dateEditText.setOnClickListener { showDatePicker() }
        binding.timeEditText.setOnClickListener { showTimePicker() }

        binding.updateButton.setOnClickListener {
            updateConsultation()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        // Try parsing current text to set calendar
        // Simple implementation: just use current date defaults
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = android.app.DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.dateEditText.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = android.app.TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.timeEditText.setText(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun updateConsultation() {
        val id = consultationId
        if (id == null) {
            Toast.makeText(context, "Erreur: ID de consultation manquant", Toast.LENGTH_SHORT).show()
            return
        }

        val date = binding.dateEditText.text.toString().trim()
        val time = binding.timeEditText.text.toString().trim()
        val patientIdText = binding.patientIdEditText.text.toString().trim()
        val reason = binding.reasonEditText.text.toString().trim()

        var patientId: Int? = null
        if (patientIdText.isNotEmpty()) {
            patientId = patientIdText.toIntOrNull()
            if (patientId == null) {
                Toast.makeText(context, getString(R.string.error_invalid_patient_id), Toast.LENGTH_SHORT).show()
                return
            }
        }

        // According to user, we can update specific fields.
        // We send all filled fields.
        
        binding.updateButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val request = CAPRequest.UpdateConsultationRequest(
                    consultationId = id,
                    date = if (date.isEmpty()) null else date,
                    hour = if (time.isEmpty()) null else time,
                    patientId = patientId,
                    reason = if (reason.isEmpty()) null else reason
                )

                val responseResult = networkManager.sendRequest(request)

                responseResult.onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        binding.updateButton.isEnabled = true
                        if (response.success) {
                            Toast.makeText(context, getString(R.string.success_consultation_updated), Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack() // Go back to list
                        } else {
                            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }.onFailure { error ->
                    withContext(Dispatchers.Main) {
                        binding.updateButton.isEnabled = true
                        var errorMsg = "Erreur"
                        if (error.message != null) {
                            errorMsg = error.message!!
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.updateButton.isEnabled = true
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
