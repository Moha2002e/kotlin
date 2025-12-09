package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.controller.NetworkManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.CAPRequest
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var appBarConfiguration: AppBarConfiguration? = null
    private lateinit var networkManager: NetworkManager
    private lateinit var sharedPreferences: SharedPreferences
    
    companion object {
        @Volatile
        private var instance: MainActivity? = null
        
        fun getInstance(): MainActivity? {
            return instance
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        instance = this
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.appBarMain.toolbar)
        
        networkManager = NetworkManager()
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        val doctorId = sharedPreferences.getInt("doctor_id", -1)
        if (doctorId != -1) {
            networkManager.setDoctorId(doctorId)
        }
        
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        
        binding.root.post {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_consultations,
                    R.id.nav_add_consultation,
                    R.id.nav_add_patient,
                    R.id.nav_search
                ),
                drawerLayout
            )
            
            if (appBarConfiguration != null) {
                setupActionBarWithNavController(navController, appBarConfiguration!!)
            }
            navView.setupWithNavController(navController)
            
            navView.setNavigationItemSelectedListener { menuItem ->
                if (menuItem.itemId == R.id.nav_logout) {
                    performLogout()
                    true
                } else {
                    menuItem.isChecked = true
                    drawerLayout.closeDrawers()
                    navController.navigate(menuItem.itemId)
                    true
                }
            }
        }
        
        lifecycleScope.launch {
            if (!networkManager.isConnected()) {
                val connectResult = networkManager.connect()
                if (connectResult.isFailure) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, getString(R.string.error_connection_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        try {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            if (appBarConfiguration != null) {
                val result = navController.navigateUp(appBarConfiguration!!)
                if (!result) {
                    return super.onSupportNavigateUp()
                }
                return result
            } else {
                return super.onSupportNavigateUp()
            }
        } catch (e: Exception) {
            return super.onSupportNavigateUp()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
    
    private fun performLogout() {
        lifecycleScope.launch {
            try {
                val request = CAPRequest.LogoutRequest
                val responseResult = networkManager.sendRequest(request)
                
                responseResult.onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        networkManager.disconnect()
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }.onFailure { error ->
                    withContext(Dispatchers.Main) {
                        networkManager.disconnect()
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    networkManager.disconnect()
                    sharedPreferences.edit().clear().apply()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
    
    fun getNetworkManager(): NetworkManager {
        return networkManager
    }
}
