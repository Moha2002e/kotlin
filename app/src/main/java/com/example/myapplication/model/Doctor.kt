package com.example.myapplication.model

/**
 * Modèle représentant un médecin
 */
data class Doctor(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val specialtyId: Int? = null,
    val password: String? = null
) {
    val fullName: String
        get() = "$firstName $lastName"
}

