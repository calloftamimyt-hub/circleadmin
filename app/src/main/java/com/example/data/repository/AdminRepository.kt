package com.example.data.repository

import com.example.data.database.AccountSellRequest
import com.example.data.database.AdminDao
import com.example.data.database.AppUser
import com.example.data.database.FeatureSetting
import com.example.data.database.IncomeRecord
import com.example.data.database.MicroJobRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AdminRepository(private val adminDao: AdminDao) {

    val allFeatureSettings: Flow<List<FeatureSetting>> = adminDao.getAllFeatureSettings()
    val allIncomeRecords: Flow<List<IncomeRecord>> = adminDao.getAllIncomeRecords()
    val allUsers: Flow<List<AppUser>> = adminDao.getAllUsers()
    val allMicroJobRequests: Flow<List<MicroJobRequest>> = adminDao.getAllMicroJobRequests()
    val allAccountSellRequests: Flow<List<AccountSellRequest>> = adminDao.getAllAccountSellRequests()

    fun getIncomeRecordsByFeature(featureId: String): Flow<List<IncomeRecord>> {
        return adminDao.getIncomeRecordsByFeature(featureId)
    }

    suspend fun updateFeatureSetting(setting: FeatureSetting) {
        adminDao.insertFeatureSetting(setting)
    }

    suspend fun insertMicroJobRequest(request: MicroJobRequest) {
        adminDao.insertMicroJobRequest(request)
    }

    suspend fun updateMicroJobRequest(request: MicroJobRequest) {
        adminDao.updateMicroJobRequest(request)
    }

    suspend fun deleteMicroJobRequest(id: Int) {
        adminDao.deleteMicroJobRequest(id)
    }

    suspend fun insertAccountSellRequest(request: AccountSellRequest) {
        adminDao.insertAccountSellRequest(request)
    }

    suspend fun updateAccountSellRequest(request: AccountSellRequest) {
        adminDao.updateAccountSellRequest(request)
    }

    suspend fun deleteAccountSellRequest(id: Int) {
        adminDao.deleteAccountSellRequest(id)
    }

    suspend fun insertIncomeRecord(record: IncomeRecord) {
        adminDao.insertIncomeRecord(record)
    }

    suspend fun deleteIncomeRecord(id: Int) {
        adminDao.deleteIncomeRecord(id)
    }

    suspend fun insertUser(user: AppUser) {
        adminDao.insertUser(user)
    }

    suspend fun updateUser(user: AppUser) {
        adminDao.updateUser(user)
    }

    suspend fun deleteUser(id: Int) {
        adminDao.deleteUser(id)
    }

    suspend fun preseedDataIfEmpty() {
        val existingSettings = adminDao.getAllFeatureSettings().firstOrNull()
        if (existingSettings.isNullOrEmpty()) {
            val defaultSettings = listOf(
                FeatureSetting("settings", "সেটিংস", "Settings", true, "currency: ৳\nlanguage: bn"),
                FeatureSetting("recharge", "মোবাইল রিচার্জ সেটিং", "Mobile Recharge Setting", true, "charge_fee: 1.5%\nmin_recharge: 10"),
                FeatureSetting("drive", "ড্রাইভ অফার সেটিং", "Drive Offer Setting", true, "max_offers_per_day: 50\ndiscount: 5%"),
                FeatureSetting("reselling", "রিসেলিং সেটিংস", "Reselling Settings", true, "commission_rate: 10%\nauto_approve: true"),
                FeatureSetting("blood", "ব্লাড সেটিং", "Blood Setting", true, "urgency_threshold: high\nallow_guest_post: true"),
                FeatureSetting("microjob", "মাইক্রো জব সেটিংস", "Micro Job Settings", true, "min_payout: 20\nfee_per_job: 5%"),
                FeatureSetting("gmail", "জিমেইল সেল সেটিংস", "Gmail Sell Settings", true, "min_account_age_days: 30\nprice_per_acc: 15"),
                FeatureSetting("telegram", "টেলিগ্রাম সেল সেটিংস", "Telegram Sell Settings", true, "price_per_member: 0.50\nchannel_verification: true"),
                FeatureSetting("instagram", "ইনস্টাগ্রাম সেল সেটিংস", "Instagram Sell Settings", true, "price_per_follower: 0.25"),
                FeatureSetting("facebook", "ফেসবুক সেল সেটিং", "Facebook Sell Setting", true, "min_friends: 100\nverification_required: true"),
                FeatureSetting("whatsapp", "whatsapp সেল সেটিংস", "Whatsapp Sell Settings", true, "price_per_number: 12\nspam_check_enabled: true"),
                FeatureSetting("typing", "টাইপিং জব সেটিংস", "Typing Job Settings", true, "pay_per_word: 0.10\nprecision_required: 95%"),
                FeatureSetting("quiz", "কুইজ জব সেটিংস", "Quiz Job Settings", true, "points_per_quiz: 50\ncorrect_answer_pct: 100%"),
                FeatureSetting("ad_view", "অ্যাড ভিউ সেটিংস", "Ad View Settings", true, "pay_per_ad_view: 0.20\ncooldown_sec: 30"),
                FeatureSetting("management", "ম্যানেজমেন্ট", "Management", true, "allowed_roles: admin,operator")
            )
            adminDao.insertFeatureSettings(defaultSettings)
        }

        val existingUsers = adminDao.getAllUsers().firstOrNull()
        if (existingUsers.isNullOrEmpty()) {
            val defaultUsers = listOf(
                AppUser(
                    username = "তামিম আহমেদ",
                    email = "tamim@circle.com",
                    phone = "01700000001",
                    role = "Admin",
                    status = "Active",
                    allowedFeaturesJson = "settings,recharge,drive,reselling,blood,microjob,gmail,telegram,instagram,facebook,whatsapp,typing,quiz,ad_view,management"
                ),
                AppUser(
                    username = "রাশেদ খান",
                    email = "rashed@circle.com",
                    phone = "01700000002",
                    role = "Operator",
                    status = "Active",
                    allowedFeaturesJson = "recharge,drive,blood,microjob"
                ),
                AppUser(
                    username = "মইনুল ইসলাম",
                    email = "moinul@circle.com",
                    phone = "01700000003",
                    role = "Operator",
                    status = "Active",
                    allowedFeaturesJson = "gmail,telegram,instagram,facebook,whatsapp"
                ),
                AppUser(
                    username = "সুমন রেজা",
                    email = "sumon@circle.com",
                    phone = "01700000004",
                    role = "Operator",
                    status = "Suspended",
                    allowedFeaturesJson = "typing,quiz,ad_view"
                )
            )
            for (user in defaultUsers) {
                adminDao.insertUser(user)
            }
        }

        // Add some default income records for reporting charts and tracking
        val existingIncome = adminDao.getAllIncomeRecords().firstOrNull()
        if (existingIncome.isNullOrEmpty()) {
            val incomeList = listOf(
                IncomeRecord(featureId = "recharge", amount = 1250.00, description = "সেলার রিচার্জ ট্রানজেকশন ফি"),
                IncomeRecord(featureId = "drive", amount = 3400.00, description = "ঈদ স্পেশাল ড্রাইভ অফার প্যাক কমিশন"),
                IncomeRecord(featureId = "reselling", amount = 1850.50, description = "রিসেলিং মার্চেন্ট প্রফিট"),
                IncomeRecord(featureId = "microjob", amount = 980.00, description = "মাইক্রো টাস্ক সাবমিশন সার্ভিস ফি"),
                IncomeRecord(featureId = "gmail", amount = 1500.00, description = "১০০টি জিমেইল একাউন্ট ভেরিফাইড সেল"),
                IncomeRecord(featureId = "telegram", amount = 650.00, description = "টেলিগ্রাম প্রমোশন ফি"),
                IncomeRecord(featureId = "typing", amount = 1200.00, description = "পিডিএফ টু ডক টাইপিং প্রজেক্ট আর্নিং"),
                IncomeRecord(featureId = "ad_view", amount = 2300.00, description = "ফেসবুক ও গুগল এড ইন্টিগ্রেশন ইনকাম"),
                // Old dummy reports for dashboard visualizers
                IncomeRecord(featureId = "recharge", amount = 850.0, description = "আগের রিচার্জ ফি", timestamp = System.currentTimeMillis() - 86400000),
                IncomeRecord(featureId = "drive", amount = 2200.0, description = "আগের ড্রাইভ অফার প্যাক কমিশন", timestamp = System.currentTimeMillis() - 86400000)
            )
            for (income in incomeList) {
                adminDao.insertIncomeRecord(income)
            }
        }

        // Add default Micro Job Requests if empty
        val existingRequests = adminDao.getAllMicroJobRequests().firstOrNull()
        if (existingRequests.isNullOrEmpty()) {
            val defaultRequests = listOf(
                MicroJobRequest(
                    userId = "user_102",
                    username = "আব্দুর রহমান",
                    jobTitle = "টেলিগ্রাম চ্যানেলে জয়েন করুন",
                    proofText = "@user_rahman7 জয়েন করেছি স্ক্রিনশট সহ।",
                    payoutAmount = 2.50,
                    status = "Pending"
                ),
                MicroJobRequest(
                    userId = "user_105",
                    username = "ফাতেমা আক্তার",
                    jobTitle = "ফেসবুক পেজে লাইক ও শেয়ার",
                    proofText = "শেয়ার লিংক: https://facebook.com/share/92842491, লাইক ডান।",
                    payoutAmount = 3.20,
                    status = "Pending"
                ),
                MicroJobRequest(
                    userId = "user_109",
                    username = "আরিফুল ইসলাম",
                    jobTitle = "ইউটিউব সাবস্ক্রাইব ও বেল বাটন ক্লিক",
                    proofText = "ইমেইল: arif.sub@gmail.com থেকে সাবস্ক্রাইব করেছি।",
                    payoutAmount = 4.00,
                    status = "Approved"
                ),
                MicroJobRequest(
                    userId = "user_112",
                    username = "নুসরাত জাহান",
                    jobTitle = "অ্যাপলিকেশন ডাউনলোড ও ৫ স্টার রেটিং",
                    proofText = "নুসরাত জাহান নাম দিয়ে রিভিউ লিখেছি, সব ঠিক আছে।",
                    payoutAmount = 15.00,
                    status = "Pending"
                ),
                MicroJobRequest(
                    userId = "user_115",
                    username = "জাহিদ হাসান",
                    jobTitle = "টুইটার/X ফলো ও রিটুইট",
                    proofText = "পেন্ডিং কাজ সম্পন্ন করেছি। ইউজার @zahid_tweet",
                    payoutAmount = 1.80,
                    status = "Rejected"
                )
            )
            for (req in defaultRequests) {
                adminDao.insertMicroJobRequest(req)
            }
        }

        val existingSellRequests = adminDao.getAllAccountSellRequests().firstOrNull()
        if (existingSellRequests.isNullOrEmpty()) {
            val defaultSellRequests = listOf(
                AccountSellRequest(
                    featureId = "telegram",
                    userId = "user_204",
                    username = "ইমরান আহমেদ",
                    accountInfo = "@tech_bengal (৫কে মেম্বার চ্যানেল)",
                    additionalAddress = "bkash: 01712345678",
                    price = 450.0,
                    status = "Pending"
                ),
                AccountSellRequest(
                    featureId = "whatsapp",
                    userId = "user_305",
                    username = "সাবিহা সুলতানা",
                    accountInfo = "+8801912345678 (একটিভ বিজনেস হোয়াটসঅ্যাপ)",
                    additionalAddress = "nagad: 01912345678",
                    price = 80.0,
                    status = "Pending"
                ),
                AccountSellRequest(
                    featureId = "telegram",
                    userId = "user_401",
                    username = "জুলফিকার আলি",
                    accountInfo = "@sports_bd (১২০০ মেম্বার গ্রুপ)",
                    additionalAddress = "TRC20 Wallet: TYx884729482",
                    price = 150.0,
                    status = "Approved"
                )
            )
            for (req in defaultSellRequests) {
                adminDao.insertAccountSellRequest(req)
            }
        }
    }
}
