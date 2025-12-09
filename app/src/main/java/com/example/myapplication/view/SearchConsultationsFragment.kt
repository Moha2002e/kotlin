package com.example.myapplication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.controller.NetworkManager
import com.example.myapplication.databinding.FragmentSearchConsultationsBinding
import com.example.myapplication.model.CAPRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hepl.fead.model.entity.Consultation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchConsultationsFragment : Fragment() {
    private var _binding: FragmentSearchConsultationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ConsultationAdapter
    private lateinit var networkManager: NetworkManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchConsultationsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mainActivity = activity as? MainActivity
        if (mainActivity == null) {
            return
        }
        
        networkManager = mainActivity.getNetworkManager()
        
        adapter = ConsultationAdapter(
            onDeleteClick = { },
            showDeleteButton = false
        )
        
        binding.resultsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.resultsRecyclerView.adapter = adapter
        
        binding.searchButton.setOnClickListener {
            searchConsultations()
        }
    }
    
    private fun searchConsultations() {
        val date = binding.dateEditText.text.toString().trim()
        
        if (date.isEmpty()) {
            Toast.makeText(context, "Veuillez entrer une date", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.searchButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val request = CAPRequest.SearchConsultationsRequest(
                    patientId = null,
                    date = date
                )
                
                val responseResult = networkManager.sendRequest(request)
                
                responseResult.onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        binding.searchButton.isEnabled = true
                        
                        if (response.success) {
                            val consultations = parseConsultations(response.data)
                            adapter.submitList(consultations)
                            
                            if (consultations.isEmpty()) {
                                Toast.makeText(context, getString(R.string.no_consultations), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }.onFailure { error ->
                    withContext(Dispatchers.Main) {
                        binding.searchButton.isEnabled = true
                        var errorMsg = "Erreur"
                        if (error.message != null) {
                            errorMsg = error.message!!
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.searchButton.isEnabled = true
                    var errorMsg = "Erreur"
                    if (e.message != null) {
                        errorMsg = e.message!!
                    }
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun parseConsultations(data: Any?): List<Consultation> {
        if (data == null) {
            return emptyList()
        }
        
        try {
            val jsonString = Gson().toJson(data)
            val listType = object : TypeToken<List<Consultation>>() {}.type
            val result = Gson().fromJson<List<Consultation>>(jsonString, listType)
            if (result == null) {
                return emptyList()
            }
            return result
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
