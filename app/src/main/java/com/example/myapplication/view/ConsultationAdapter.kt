package com.example.myapplication.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemConsultationBinding
import hepl.fead.model.entity.Consultation

class ConsultationAdapter(
    var onDeleteClick: (Consultation) -> Unit,
    private val showDeleteButton: Boolean = true
) : ListAdapter<Consultation, ConsultationAdapter.ViewHolder>(ConsultationDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConsultationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemConsultationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(consultation: Consultation) {
            var dateText = ""
            if (consultation.getDate() != null) {
                dateText = consultation.getDate()!!
            }
            binding.consultationDateTextView.text = dateText
            
            var timeText = ""
            if (consultation.getHour() != null) {
                timeText = consultation.getHour()!!
            }
            binding.consultationTimeTextView.text = timeText
            
            val patientId = consultation.getPatient_id()
            if (patientId != null) {
                val firstName = consultation.getPatient_first_name()
                val lastName = consultation.getPatient_last_name()
                var patientName = ""
                if (firstName != null && firstName.isNotEmpty()) {
                    patientName = patientName + firstName
                }
                if (lastName != null && lastName.isNotEmpty()) {
                    if (patientName.isNotEmpty()) {
                        patientName = patientName + " "
                    }
                    patientName = patientName + lastName
                }
                if (patientName.isEmpty()) {
                    patientName = "Patient ID: " + patientId
                }
                binding.consultationPatientTextView.text = binding.root.context.getString(
                    R.string.consultation_patient,
                    patientName
                )
            } else {
                binding.consultationPatientTextView.text = binding.root.context.getString(
                    R.string.consultation_patient,
                    binding.root.context.getString(R.string.consultation_free)
                )
            }
            
            val reason = consultation.getReason()
            if (reason != null && reason.isNotEmpty()) {
                binding.consultationReasonTextView.text = binding.root.context.getString(
                    R.string.consultation_reason,
                    reason
                )
                binding.consultationReasonTextView.visibility = android.view.View.VISIBLE
            } else {
                binding.consultationReasonTextView.visibility = android.view.View.GONE
            }
            
            if (showDeleteButton) {
                binding.deleteButton.visibility = android.view.View.VISIBLE
                binding.deleteButton.setOnClickListener {
                    onDeleteClick.invoke(consultation)
                }
            } else {
                binding.deleteButton.visibility = android.view.View.GONE
            }
        }
    }
    
    class ConsultationDiffCallback : DiffUtil.ItemCallback<Consultation>() {
        override fun areItemsTheSame(oldItem: Consultation, newItem: Consultation): Boolean {
            return oldItem.getId() == newItem.getId()
        }
        
        override fun areContentsTheSame(oldItem: Consultation, newItem: Consultation): Boolean {
            if (oldItem.getDate() != newItem.getDate()) {
                return false
            }
            if (oldItem.getHour() != newItem.getHour()) {
                return false
            }
            if (oldItem.getPatient_id() != newItem.getPatient_id()) {
                return false
            }
            if (oldItem.getReason() != newItem.getReason()) {
                return false
            }
            return true
        }
    }
}
