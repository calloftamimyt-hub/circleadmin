package com.example.data.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "feature_settings")
data class FeatureSetting(
    @PrimaryKey val id: String,
    val displayNameBangla: String,
    val displayNameEnglish: String,
    val isEnabled: Boolean = true,
    val extraConfig: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "income_records")
data class IncomeRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val featureId: String,
    val amount: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reportedBy: String = "Admin"
)

@Entity(tableName = "app_users")
data class AppUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val phone: String = "",
    val role: String = "Operator", // Admin, Operator, User
    val status: String = "Active", // Active, Suspended
    val allowedFeaturesJson: String = "", // Comma-separated list of allowed featureIds
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "micro_job_requests")
data class MicroJobRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val username: String,
    val jobTitle: String,
    val proofText: String,
    val proofImgUrl: String = "",
    val payoutAmount: Double,
    val status: String = "Pending", // Pending, Approved, Rejected
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "account_sell_requests")
data class AccountSellRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val featureId: String, // "telegram" or "whatsapp"
    val userId: String,
    val username: String,
    val accountInfo: String, // e.g., @my_channel or +88017x...
    val additionalAddress: String, // e.g., bKash wallet or crypto address
    val price: Double,
    val status: String = "Pending", // Pending, Approved, Rejected
    val submittedAt: Long = System.currentTimeMillis()
)

@Dao
interface AdminDao {
    // Feature Settings
    @Query("SELECT * FROM feature_settings")
    fun getAllFeatureSettings(): Flow<List<FeatureSetting>>

    @Query("SELECT * FROM feature_settings WHERE id = :id")
    suspend fun getFeatureSettingById(id: String): FeatureSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatureSetting(setting: FeatureSetting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatureSettings(settings: List<FeatureSetting>)

    // Income Records
    @Query("SELECT * FROM income_records ORDER BY timestamp DESC")
    fun getAllIncomeRecords(): Flow<List<IncomeRecord>>

    @Query("SELECT * FROM income_records WHERE featureId = :featureId ORDER BY timestamp DESC")
    fun getIncomeRecordsByFeature(featureId: String): Flow<List<IncomeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomeRecord(record: IncomeRecord)

    @Query("DELETE FROM income_records WHERE id = :id")
    suspend fun deleteIncomeRecord(id: Int)

    // App Users
    @Query("SELECT * FROM app_users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<AppUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AppUser)

    @Update
    suspend fun updateUser(user: AppUser)

    @Query("DELETE FROM app_users WHERE id = :id")
    suspend fun deleteUser(id: Int)

    // Micro Job Requests
    @Query("SELECT * FROM micro_job_requests ORDER BY submittedAt DESC")
    fun getAllMicroJobRequests(): Flow<List<MicroJobRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicroJobRequest(request: MicroJobRequest)

    @Update
    suspend fun updateMicroJobRequest(request: MicroJobRequest)

    @Query("DELETE FROM micro_job_requests WHERE id = :id")
    suspend fun deleteMicroJobRequest(id: Int)

    // Account Sell Requests
    @Query("SELECT * FROM account_sell_requests ORDER BY submittedAt DESC")
    fun getAllAccountSellRequests(): Flow<List<AccountSellRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountSellRequest(request: AccountSellRequest)

    @Update
    suspend fun updateAccountSellRequest(request: AccountSellRequest)

    @Query("DELETE FROM account_sell_requests WHERE id = :id")
    suspend fun deleteAccountSellRequest(id: Int)
}

@Database(entities = [FeatureSetting::class, IncomeRecord::class, AppUser::class, MicroJobRequest::class, AccountSellRequest::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adminDao(): AdminDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "circle_admin_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
