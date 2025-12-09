package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.controller.NetworkManager
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.model.CAPRequest
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var networkManager: NetworkManager
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        networkManager = NetworkManager()
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        val savedLogin = sharedPreferences.getString("doctor_login", null)
        val savedPassword = sharedPreferences.getString("doctor_password", null)
        if (savedLogin != null && savedPassword != null) {
            binding.loginEditText.setText(savedLogin)
            binding.passwordEditText.setText(savedPassword)
        }
        
        binding.loginButton.setOnClickListener {
            performLogin()
        }
    }
    
    private fun performLogin() {
        val login = binding.loginEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        
        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.loginButton.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val connectResult = networkManager.connect()
                
                if (connectResult.isFailure) {
                    withContext(Dispatchers.Main) {
                        binding.loginButton.isEnabled = true
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@LoginActivity, getString(R.string.error_connection_failed), Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }
                
                val request = CAPRequest.LoginRequest(login, password)
                val responseResult = networkManager.sendRequest(request)
                
                responseResult.onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        if (response.success) {
                            val doctorData = response.data
                            if (doctorData != null) {
                                try {
                                    val doctorClass = doctorData.javaClass
                                    val getIdMethod = doctorClass.getMethod("getId")
                                    val id = getIdMethod.invoke(doctorData) as? Int
                                    if (id != null) {
                                        networkManager.setDoctorId(id)
                                        sharedPreferences.edit().putInt("doctor_id", id).apply()
                                    }
                                } catch (e: Exception) {
                                    // Erreur silencieuse
                                }
                            }
                            
                            sharedPreferences.edit()
                                .putString("doctor_login", login)
                                .putString("doctor_password", password)
                                .putString("doctor_data", Gson().toJson(response.data))
                                .apply()
                            
                            var successMessage = getString(R.string.login_success)
                            if (response.message.isNotEmpty()) {
                                successMessage = successMessage + "\n" + response.message
                            }
                            
                            Toast.makeText(this@LoginActivity, successMessage, Toast.LENGTH_LONG).show()
                            
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            binding.loginButton.isEnabled = true
                            binding.progressBar.visibility = android.view.View.GONE
                            
                            var errorMessage = response.message
                            if (errorMessage.isEmpty()) {
                                errorMessage = getString(R.string.error_connection_failed)
                            }
                            
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }.onFailure { error ->
                    withContext(Dispatchers.Main) {
                        binding.loginButton.isEnabled = true
                        binding.progressBar.visibility = android.view.View.GONE
                        var errorMsg = "Erreur inconnue"
                        if (error.message != null) {
                            errorMsg = error.message!!
                        }
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.loginButton.isEnabled = true
                    binding.progressBar.visibility = android.view.View.GONE
                    var errorMsg = "Erreur"
                    if (e.message != null) {
                        errorMsg = e.message!!
                    }
                    Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
