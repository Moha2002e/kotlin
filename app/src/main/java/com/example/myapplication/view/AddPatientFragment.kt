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
import com.example.myapplication.databinding.FragmentAddPatientBinding
import com.example.myapplication.model.CAPRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPatientFragment : Fragment() {
    private var _binding: FragmentAddPatientBinding? = null
    private val binding get() = _binding!!
    private lateinit var networkManager: NetworkManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPatientBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mainActivity = activity as? MainActivity
        if (mainActivity == null) {
            return
        }
        
        networkManager = mainActivity.getNetworkManager()
        
        binding.addButton.setOnClickListener {
            addPatient()
        }
    }
    
    private fun addPatient() {
        val firstName = binding.firstNameEditText.text.toString().trim()
        val lastName = binding.lastNameEditText.text.toString().trim()
        
        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(context, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.addButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val request = CAPRequest.AddPatientRequest(
                    firstName = firstName,
                    lastName = lastName
                )
                
                val responseResult = networkManager.sendRequest(request)
                
                responseResult.onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        binding.addButton.isEnabled = true
                        
                        if (response.success) {
                            val patientId = response.data as? Int
                            var message = ""
                            if (patientId != null) {
                                message = getString(R.string.success_patient_added, patientId)
                            } else {
                                message = getString(R.string.success_patient_added, 0)
                            }
                            
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            binding.firstNameEditText.text?.clear()
                            binding.lastNameEditText.text?.clear()
                        } else {
                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
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
