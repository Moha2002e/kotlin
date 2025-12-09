package com.example.myapplication.model

/**
 * Requêtes du protocole CAP (Consultation Administration Protocol)
 */
sealed class CAPRequest { // c'est une classe scellée pour représenter les différentes requêtes du protocole CAP
    abstract val command: String
    
    data class LoginRequest(
        val login: String,
        val password: String
    ) : CAPRequest() {
        override val command = "LOGIN"
    }
    
    data class AddConsultationRequest(
        val date: String,
        val hour: String,
        val duration: Int,
        val consecutiveCount: Int
    ) : CAPRequest() { // le caprequest sert à ajouter une consultation
        override val command = "ADD_CONSULTATION"
    }
    
    data class AddPatientRequest(
        val firstName: String,
        val lastName: String
    ) : CAPRequest() {
        override val command = "ADD_PATIENT"
    }
    
    data class UpdateConsultationRequest(
        val consultationId: Int,
        val date: String? = null,
        val hour: String? = null,
        val patientId: Int? = null,
        val reason: String? = null
    ) : CAPRequest() {
        override val command = "UPDATE_CONSULTATION"
    }
    
    data class SearchConsultationsRequest(
        val patientId: Int? = null,
        val date: String? = null
    ) : CAPRequest() {
        override val command = "SEARCH_CONSULTATIONS"
    }
    
    data class DeleteConsultationRequest(
        val consultationId: Int
    ) : CAPRequest() {
        override val command = "DELETE_CONSULTATION"
    }
    
    object LogoutRequest : CAPRequest() {
        override val command = "LOGOUT"
    }
}

/**
 * Réponses du protocole CAP
 */
data class CAPResponse(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)

