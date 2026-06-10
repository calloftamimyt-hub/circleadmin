package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.AppUser
import com.example.data.database.FeatureSetting
import com.example.data.database.IncomeRecord
import com.example.data.database.MicroJobRequest
import com.example.data.database.AccountSellRequest
import com.example.data.repository.AdminRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AdminRepository

    val featureSettings: StateFlow<List<FeatureSetting>>
    val incomeRecords: StateFlow<List<IncomeRecord>>
    val users: StateFlow<List<AppUser>>
    val microJobRequests: StateFlow<List<MicroJobRequest>>
    val accountSellRequests: StateFlow<List<AccountSellRequest>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AdminRepository(database.adminDao())

        // Initial seeding on launch
        viewModelScope.launch {
            repository.preseedDataIfEmpty()
        }

        featureSettings = repository.allFeatureSettings
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        incomeRecords = repository.allIncomeRecords
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        users = repository.allUsers
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        microJobRequests = repository.allMicroJobRequests
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        accountSellRequests = repository.allAccountSellRequests
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun toggleFeature(setting: FeatureSetting, andEnable: Boolean) {
        viewModelScope.launch {
            repository.updateFeatureSetting(
                setting.copy(
                    isEnabled = andEnable,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateFeatureConfig(setting: FeatureSetting, newConfig: String) {
        viewModelScope.launch {
            repository.updateFeatureSetting(
                setting.copy(
                    extraConfig = newConfig,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun addIncomeRecord(featureId: String, amount: Double, description: String, reportedBy: String = "Admin") {
        viewModelScope.launch {
            repository.insertIncomeRecord(
                IncomeRecord(
                    featureId = featureId,
                    amount = amount,
                    description = description,
                    reportedBy = reportedBy,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteIncomeRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteIncomeRecord(id)
        }
    }

    fun addUser(username: String, email: String, phone: String, role: String, allowedFeatures: List<String>) {
        viewModelScope.launch {
            repository.insertUser(
                AppUser(
                    username = username,
                    email = email,
                    phone = phone,
                    role = role,
                    status = "Active",
                    allowedFeaturesJson = allowedFeatures.joinToString(",")
                )
            )
        }
    }

    fun updateUser(user: AppUser) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            repository.deleteUser(id)
        }
    }

    fun insertMicroJobRequest(userId: String, username: String, jobTitle: String, proofText: String, payoutAmount: Double) {
        viewModelScope.launch {
            repository.insertMicroJobRequest(
                MicroJobRequest(
                    userId = userId,
                    username = username,
                    jobTitle = jobTitle,
                    proofText = proofText,
                    payoutAmount = payoutAmount,
                    status = "Pending",
                    submittedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateMicroJobRequestStatus(request: MicroJobRequest, newStatus: String) {
        viewModelScope.launch {
            repository.updateMicroJobRequest(
                request.copy(status = newStatus)
            )
        }
    }

    fun deleteMicroJobRequest(id: Int) {
        viewModelScope.launch {
            repository.deleteMicroJobRequest(id)
        }
    }

    fun insertAccountSellRequest(featureId: String, userId: String, username: String, accountInfo: String, additionalAddress: String, price: Double) {
        viewModelScope.launch {
            repository.insertAccountSellRequest(
                AccountSellRequest(
                    featureId = featureId,
                    userId = userId,
                    username = username,
                    accountInfo = accountInfo,
                    additionalAddress = additionalAddress,
                    price = price,
                    status = "Pending",
                    submittedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateAccountSellRequestStatus(request: AccountSellRequest, newStatus: String) {
        viewModelScope.launch {
            repository.updateAccountSellRequest(
                request.copy(status = newStatus)
            )
        }
    }

    fun deleteAccountSellRequest(id: Int) {
        viewModelScope.launch {
            repository.deleteAccountSellRequest(id)
        }
    }

    fun resetDatabase() {
        viewModelScope.launch {
            val database = AppDatabase.getDatabase(getApplication())
            database.clearAllTables()
            repository.preseedDataIfEmpty()
        }
    }
}
