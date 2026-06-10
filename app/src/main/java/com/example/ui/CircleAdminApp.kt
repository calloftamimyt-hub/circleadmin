package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.AppUser
import com.example.data.database.FeatureSetting
import com.example.data.database.IncomeRecord
import com.example.data.database.MicroJobRequest
import com.example.data.database.AccountSellRequest
import com.example.ui.viewmodel.AdminViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

fun getFeatureAccentColors(id: String): Pair<Color, Color> {
    return when (id) {
        "settings" -> Pair(Color(0xFFF1F5F9), Color(0xFF475569)) // Slate
        "recharge" -> Pair(Color(0xFFDBEAFE), Color(0xFF2563EB)) // Blue
        "drive" -> Pair(Color(0xFFE0E7FF), Color(0xFF4F46E5)) // Indigo
        "reselling" -> Pair(Color(0xFFD1FAE5), Color(0xFF059669)) // Green
        "blood" -> Pair(Color(0xFFFEE2E2), Color(0xFFDC2626)) // Red
        "microjob" -> Pair(Color(0xFFFEF3C7), Color(0xFFD97706)) // Amber
        "gmail" -> Pair(Color(0xFFF3F4F6), Color(0xFF4B5563)) // Gray
        "telegram" -> Pair(Color(0xFFE0F2FE), Color(0xFF0284C7)) // Sky
        "instagram" -> Pair(Color(0xFFFCE7F3), Color(0xFFDB2777)) // Pink
        "facebook" -> Pair(Color(0xFFDBEAFE), Color(0xFF1D4ED8)) // Facebook Blue
        "whatsapp" -> Pair(Color(0xFFD1FAE5), Color(0xFF10B981)) // Emerald
        "typing" -> Pair(Color(0xFFF3E8FF), Color(0xFF9333EA)) // Purple
        "quiz" -> Pair(Color(0xFFFFE4E6), Color(0xFFE11D48)) // Rose
        "ad_view" -> Pair(Color(0xFFFEF9C3), Color(0xFFCA8A04)) // Yellow
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }
}

// Sidebar list item description structure
data class NavigationItem(
    val id: String,
    val titleBangla: String,
    val titleEnglish: String,
    val icon: ImageVector,
    val isCategory: Boolean = false,
    val categoryName: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircleAdminApp(viewModel: AdminViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var currentScreenId by remember { mutableStateOf("dashboard") }

    // Collect data state from ViewModel
    val featureSettings by viewModel.featureSettings.collectAsStateWithLifecycle()
    val incomeRecords by viewModel.incomeRecords.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val microJobRequests by viewModel.microJobRequests.collectAsStateWithLifecycle()

    // Navigation items definitions mapping to Bengali requirement
    val navigationItems = remember {
        listOf(
            NavigationItem("dashboard", "ড্যাশবোর্ড", "Dashboard", Icons.Default.Dashboard),
            
            // Core Admin Setups
            NavigationItem("settings", "সেটিংস", "Settings", Icons.Default.Settings),
            NavigationItem("recharge", "মোবাইল রিচার্জ সেটিং", "Mobile Recharge Setting", Icons.Default.PhoneAndroid),
            NavigationItem("drive", "ড্রাইভ অফার সেটিং", "Drive Offer Setting", Icons.Default.LocalOffer),
            NavigationItem("reselling", "রিসেলিং সেটিংস", "Reselling Settings", Icons.Default.Storefront),
            NavigationItem("blood", "ব্লাড সেটিং", "Blood Setting", Icons.Default.Bloodtype),
            
            // Job & Sell Setups
            NavigationItem("microjob", "মাইক্রো জব সেটিংস", "Micro Job Settings", Icons.Default.Work),
            NavigationItem("gmail", "জিমেইল সেল সেটিংস", "Gmail Sell Settings", Icons.Default.Email),
            NavigationItem("telegram", "টেলিগ্রাম সেল সেটিংস", "Telegram Sell Settings", Icons.Default.Send),
            NavigationItem("instagram", "ইনস্টাগ্রাম সেল সেটিংস", "Instagram Sell Settings", Icons.Default.PhotoCamera),
            NavigationItem("facebook", "ফেসবুক সেল সেটিং", "Facebook Sell Setting", Icons.Default.ThumbUp),
            NavigationItem("whatsapp", "whatsapp সেল সেটিংস", "WhatsApp Sell Settings", Icons.Default.Forum),
            
            // Other Setups
            NavigationItem("typing", "টাইপিং জব সেটিংস", "Typing Job Settings", Icons.Default.Keyboard),
            NavigationItem("quiz", "কুইজ জব সেটিংস", "Quiz Job Settings", Icons.Default.Lightbulb),
            NavigationItem("ad_view", "অ্যাড ভিউ সেটিংস", "Ad View Settings", Icons.Default.PlayCircle),
            
            // System management
            NavigationItem("management", "ম্যানেজমেন্ট ও ইউজার", "Management", Icons.Default.People)
        )
    }

    // Modal navigation drawer wrap
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight(),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerTonalElevation = 6.dp
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Avatar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "সার্কেল এডমিন",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "সিস্টেম কন্ট্রোল প্যানেল",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

                // Scrollable list of sidebar navigation items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(navigationItems) { item ->
                        val isSelected = currentScreenId == item.id
                        NavigationDrawerItem(
                            icon = { 
                                Icon(
                                    imageVector = item.icon, 
                                    contentDescription = item.titleBangla,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            label = { 
                                Column {
                                    Text(
                                        text = item.titleBangla, 
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = item.titleEnglish, 
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            selected = isSelected,
                            onClick = {
                                currentScreenId = item.id
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedContainerColor = Color.Transparent,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("nav_item_${item.id}")
                        )
                    }
                }
            }
        }
    ) {
        val selectedItem = navigationItems.find { it.id == currentScreenId } ?: navigationItems[0]

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = selectedItem.titleBangla,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = selectedItem.titleEnglish,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    },
                    navigationIcon = {
                        if (currentScreenId != "dashboard") {
                            IconButton(onClick = { currentScreenId = "dashboard" }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "ড্যাশবোর্ডে ফিরে যান"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "মেনু"
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.resetDatabase() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "রিসেট ডাটা")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Navigation Screen Router
                AnimatedContent(
                    targetState = currentScreenId,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "ScreenTransition"
                ) { screenId ->
                    when (screenId) {
                        "dashboard" -> {
                            DashboardView(
                                settings = featureSettings,
                                records = incomeRecords,
                                users = users,
                                onNavigateToScreen = { currentScreenId = it },
                                viewModel = viewModel
                            )
                        }
                        "management" -> {
                            UserManagementView(
                                users = users,
                                features = featureSettings.filter { it.id != "settings" && it.id != "management" },
                                onAddUser = { name, email, phone, role, perms ->
                                    viewModel.addUser(name, email, phone, role, perms)
                                },
                                onUpdateUser = { viewModel.updateUser(it) },
                                onDeleteUser = { viewModel.deleteUser(it) }
                            )
                        }
                        "typing" -> {
                            val currentSetting = featureSettings.find { it.id == screenId }
                            if (currentSetting != null) {
                                TypingJobSettingsView(
                                    setting = currentSetting,
                                    records = incomeRecords.filter { it.featureId == screenId },
                                    onToggleEnable = { checked ->
                                        viewModel.toggleFeature(currentSetting, checked)
                                    },
                                    onSaveConfig = { configText ->
                                        viewModel.updateFeatureConfig(currentSetting, configText)
                                    },
                                    onAddIncome = { amount, desc ->
                                        viewModel.addIncomeRecord(screenId, amount, desc)
                                    },
                                    onDeleteIncome = { id ->
                                        viewModel.deleteIncomeRecord(id)
                                    }
                                )
                            }
                        }
                        "quiz" -> {
                            val currentSetting = featureSettings.find { it.id == screenId }
                            if (currentSetting != null) {
                                QuizJobSettingsView(
                                    setting = currentSetting,
                                    records = incomeRecords.filter { it.featureId == screenId },
                                    onToggleEnable = { checked ->
                                        viewModel.toggleFeature(currentSetting, checked)
                                    },
                                    onSaveConfig = { configText ->
                                        viewModel.updateFeatureConfig(currentSetting, configText)
                                    },
                                    onAddIncome = { amount, desc ->
                                        viewModel.addIncomeRecord(screenId, amount, desc)
                                    },
                                    onDeleteIncome = { id ->
                                        viewModel.deleteIncomeRecord(id)
                                    }
                                )
                            }
                        }
                        "ad_view" -> {
                            val currentSetting = featureSettings.find { it.id == screenId }
                            if (currentSetting != null) {
                                AdViewJobSettingsView(
                                    setting = currentSetting,
                                    records = incomeRecords.filter { it.featureId == screenId },
                                    onToggleEnable = { checked ->
                                        viewModel.toggleFeature(currentSetting, checked)
                                    },
                                    onSaveConfig = { configText ->
                                        viewModel.updateFeatureConfig(currentSetting, configText)
                                    },
                                    onAddIncome = { amount, desc ->
                                        viewModel.addIncomeRecord(screenId, amount, desc)
                                    },
                                    onDeleteIncome = { id ->
                                        viewModel.deleteIncomeRecord(id)
                                    }
                                )
                            }
                        }
                        "microjob" -> {
                            val currentSetting = featureSettings.find { it.id == screenId }
                            if (currentSetting != null) {
                                MicroJobSettingsView(
                                    setting = currentSetting,
                                    requests = microJobRequests,
                                    records = incomeRecords.filter { it.featureId == screenId },
                                    onToggleEnable = { checked ->
                                        viewModel.toggleFeature(currentSetting, checked)
                                    },
                                    onSaveConfig = { configText ->
                                        viewModel.updateFeatureConfig(currentSetting, configText)
                                    },
                                    onUpdateStatus = { req, status ->
                                        viewModel.updateMicroJobRequestStatus(req, status)
                                        if (status == "Approved") {
                                            viewModel.addIncomeRecord(
                                                featureId = "microjob",
                                                amount = req.payoutAmount,
                                                description = "টাস্ক অনুমোদন: ${req.username} - ${req.jobTitle}"
                                            )
                                        }
                                    },
                                    onDeleteRequest = { id ->
                                        viewModel.deleteMicroJobRequest(id)
                                    },
                                    onGenerateDemo = {
                                        val titles = listOf(
                                            "ফেসবুক গ্রুপে ৫০ ফ্রেন্ড ইনভাইট",
                                            "লাইকি ভিডিও ৫ সেকেন্ড দেখা",
                                            "প্লে স্টোরে পজিটিভ রিভিউ ও রেটিং",
                                            "টেলিগ্রাম এয়ারড্রপ গ্রুপে মেম্বার জয়েন",
                                            "লিঙ্কডইন পেজে ফলো ও কানেক্ট",
                                            "ওয়েবসাইটে ১ মিনিট ভিজিট ও স্ক্রিনশট"
                                        )
                                        val names = listOf("সাকিব আল হাসান", "রফিকুল ইসলাম", "তাসনিম চৌধুরী", "মাহমুদুল হাসান", "তানজিলা বেগম")
                                        val usersList = listOf("user_332", "user_401", "user_198", "user_550", "user_121")
                                        val sampleProofs = listOf(
                                            "স্ক্রিনশট লিংক: imgbb.com/xyz123, ৫০ জনকে কমপ্লিট ইনভাইট করেছি।",
                                            "আমার ইউজারনেম @liker_king। লাইক দিয়েছি।",
                                            "ইউজার রিভিউর নাম: Sakib_5Star। চমৎকার অ্যাপ!",
                                            "মেম্বার আইডি: 92842419, এয়ারড্রপে জয়েন ডান।",
                                            "ফলো স্ক্রিনশট: postimg.cc/abc789। থ্যাংকস!",
                                            "আমার আইপি: 103.45.2.1, পুরো ওয়েবসাইট ১ মিনিট ব্রাউজ করেছি।"
                                        )
                                        val randomIdx = (0..5).random()
                                        viewModel.insertMicroJobRequest(
                                            userId = usersList.random(),
                                            username = names.random(),
                                            jobTitle = titles[randomIdx],
                                            proofText = sampleProofs[randomIdx],
                                            payoutAmount = (1..10).random() * 2.50
                                        )
                                    },
                                    onAddIncome = { amount, desc ->
                                        viewModel.addIncomeRecord(screenId, amount, desc)
                                    },
                                    onDeleteIncome = { id ->
                                        viewModel.deleteIncomeRecord(id)
                                    }
                                )
                            }
                        }
                        "gmail", "facebook", "instagram" -> {
                            val currentSetting = featureSettings.find { it.id == screenId }
                            if (currentSetting != null) {
                                SalesJobSettingsView(
                                    setting = currentSetting,
                                    records = incomeRecords.filter { it.featureId == screenId },
                                    onToggleEnable = { checked ->
                                        viewModel.toggleFeature(currentSetting, checked)
                                    },
                                    onSaveConfig = { configText ->
                                        viewModel.updateFeatureConfig(currentSetting, configText)
                                    },
                                    onAddIncome = { amount, desc ->
                                        viewModel.addIncomeRecord(screenId, amount, desc)
                                    },
                                    onDeleteIncome = { id ->
                                        viewModel.deleteIncomeRecord(id)
                                    }
                                )
                            }
                        }
                        "telegram", "whatsapp" -> {
                            val currentSetting = featureSettings.find { it.id == screenId }
                            if (currentSetting != null) {
                                val sellRequests = viewModel.accountSellRequests.collectAsStateWithLifecycle().value
                                    .filter { it.featureId == screenId }
                                TelegramWhatsAppSettingsView(
                                    setting = currentSetting,
                                    records = incomeRecords.filter { it.featureId == screenId },
                                    sellRequests = sellRequests,
                                    onToggleEnable = { checked ->
                                        viewModel.toggleFeature(currentSetting, checked)
                                    },
                                    onSaveConfig = { configText ->
                                        viewModel.updateFeatureConfig(currentSetting, configText)
                                    },
                                    onAddIncome = { amount, desc ->
                                        viewModel.addIncomeRecord(screenId, amount, desc)
                                    },
                                    onDeleteIncome = { id ->
                                        viewModel.deleteIncomeRecord(id)
                                    },
                                    onAddSellRequest = { accountInfo, additionalAddress, price ->
                                        val usersList = listOf("তামিম আহমেদ", "রাশেদ খান", "মইনুল ইসলাম", "আব্দুর রহমান", "সাদিয়া চৌধুরী")
                                        val randomUser = usersList.random()
                                        viewModel.insertAccountSellRequest(
                                            featureId = screenId,
                                            userId = "user_" + (100..999).random(),
                                            username = randomUser,
                                            accountInfo = accountInfo,
                                            additionalAddress = additionalAddress,
                                            price = price
                                        )
                                    },
                                    onUpdateSellRequestStatus = { request, newStatus ->
                                        viewModel.updateAccountSellRequestStatus(request, newStatus)
                                        if (newStatus == "Approved") {
                                            viewModel.addIncomeRecord(
                                                featureId = screenId,
                                                amount = request.price,
                                                description = "${currentSetting.displayNameBangla} বিক্রয় অনুমোদন: ${request.accountInfo}"
                                            )
                                        }
                                    },
                                    onDeleteSellRequest = { id ->
                                        viewModel.deleteAccountSellRequest(id)
                                    }
                                )
                            }
                        }
                        else -> {
                            // Render Feature-specific setting, tracker and report
                            val currentSetting = featureSettings.find { it.id == screenId }
                            if (currentSetting != null) {
                                FeatureSettingsView(
                                    setting = currentSetting,
                                    records = incomeRecords.filter { it.featureId == screenId },
                                    onToggleEnable = { checked ->
                                        viewModel.toggleFeature(currentSetting, checked)
                                    },
                                    onSaveConfig = { configText ->
                                        viewModel.updateFeatureConfig(currentSetting, configText)
                                    },
                                    onAddIncome = { amount, desc ->
                                        viewModel.addIncomeRecord(screenId, amount, desc)
                                    },
                                    onDeleteIncome = { id ->
                                        viewModel.deleteIncomeRecord(id)
                                    }
                                )
                            } else {
                                // Fallback loading
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// FORMAT TIME HELPER
fun formatTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}

// --- SUB-SCREEN: DASHBOARD VIEW ---
@Composable
fun DashboardView(
    settings: List<FeatureSetting>,
    records: List<IncomeRecord>,
    users: List<AppUser>,
    onNavigateToScreen: (String) -> Unit,
    viewModel: AdminViewModel
) {
    val totalRevenue = remember(records) { records.sumOf { it.amount } }
    val activeFeaturesCount = remember(settings) { settings.count { it.isEnabled } }
    val totalOperatorsCount = remember(users) { users.size }

    var showAddIncomeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick statistics highlight header cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Master revenue high contrast card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "সর্বমোট ইনকাম (Total Revenue)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color.White.copy(alpha = 0.50f),
                                            shape = RoundedCornerShape(100.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "+১২.৫%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Trend Icon",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "৳ ${String.format(Locale.US, "%,.2f", totalRevenue)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${settings.size}টি ফিচার থেকে প্রাপ্ত রিপোর্ট",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showAddIncomeDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ইনকাম যোগ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                 // Two sub cards for indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "সক্রিয় ফিচার",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$activeFeaturesCount / ${settings.size}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "সক্রিয় ইউজার ও অপারেটর",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$totalOperatorsCount জন",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }

        // Feature Income Chart (Visual Canvas report system)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ফিচার ভিত্তিক আয় বিশ্লেষণ (Revenue Share)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (records.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("কোন ইনকাম ডাটা পাওয়া যায়নি", fontSize = 12.sp, color = Color.Gray)
                        }
                    } else {
                        // Custom vector canvas bar visual reporting matching Material guidelines
                        val featureRevenueMap = remember(records) {
                            records.groupBy { it.featureId }.mapValues { entry -> entry.value.sumOf { it.amount } }
                        }
                        val maxRevenue = remember(featureRevenueMap) {
                            featureRevenueMap.values.maxOrNull() ?: 1.0
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            featureRevenueMap.entries.sortedByDescending { it.value }.take(5).forEach { entry ->
                                val feature = settings.find { it.id == entry.key }
                                val bName = feature?.displayNameBangla ?: entry.key
                                val ratio = (entry.value / maxRevenue).toFloat()

                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(bName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("৳ ${String.format(Locale.US, "%.1f", entry.value)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .background(
                                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                                RoundedCornerShape(4.dp)
                                            )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(ratio.coerceIn(0.01f, 1f))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            MaterialTheme.colorScheme.primary,
                                                            MaterialTheme.colorScheme.tertiary
                                                        )
                                                    ),
                                                    RoundedCornerShape(4.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Grid listing layout for quick accesses to features
        item {
            Text(
                text = "ফিচার সমূহের তালিকা (Quick Access)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        // We can render list of features with state toggle indicators
        items(settings) { setting ->
            val featureItemRevenue = records.filter { it.featureId == setting.id }.sumOf { it.amount }
            
            // Draw custom cards for each feature
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToScreen(setting.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (setting.isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (setting.isEnabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val (badgeBg, badgeFg) = getFeatureAccentColors(setting.id)
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    if (setting.isEnabled) badgeBg else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (setting.id) {
                                "settings" -> Icons.Default.Settings
                                "recharge" -> Icons.Default.PhoneAndroid
                                "drive" -> Icons.Default.LocalOffer
                                "reselling" -> Icons.Default.Storefront
                                "blood" -> Icons.Default.Bloodtype
                                "microjob" -> Icons.Default.Work
                                "gmail" -> Icons.Default.Email
                                "telegram" -> Icons.Default.Send
                                "instagram" -> Icons.Default.PhotoCamera
                                "facebook" -> Icons.Default.ThumbUp
                                "whatsapp" -> Icons.Default.Forum
                                "typing" -> Icons.Default.Keyboard
                                "quiz" -> Icons.Default.Lightbulb
                                "ad_view" -> Icons.Default.PlayCircle
                                else -> Icons.Default.People
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = setting.displayNameBangla,
                                tint = if (setting.isEnabled) badgeFg else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = setting.displayNameBangla,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (setting.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "আয়: ৳ ${String.format(Locale.US, "%,.2f", featureItemRevenue)}",
                                fontSize = 11.sp,
                                color = if (setting.isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Simple chips displaying status (Enabled vs Disabled)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val statusText = if (setting.isEnabled) "চালু" else "বন্ধ"
                        val statusColor = if (setting.isEnabled) Color(0xFF4CAF50) else Color(0xFFF44336)
                        
                        Box(
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Edit Feature",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Modal dialog to quickly add incoming logs from dashboard
    if (showAddIncomeDialog) {
        var selectedFeatureId by remember { mutableStateOf(settings.getOrNull(1)?.id ?: "recharge") }
        var amountText by remember { mutableStateOf("") }
        var descText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddIncomeDialog = false },
            title = {
                Text("নতুন ইনকাম যোগ করুন", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("ফিচার নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    // Simple wrap list to select features
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .height(100.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            settings.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedFeatureId = item.id }
                                        .background(if (selectedFeatureId == item.id) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedFeatureId == item.id,
                                        onClick = { selectedFeatureId = item.id }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(item.displayNameBangla, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("টাকার পরিমাণ (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = descText,
                        onValueChange = { descText = it },
                        label = { Text("বৃত্তান্ত / রিমার্কস") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.addIncomeRecord(selectedFeatureId, amt, descText)
                            showAddIncomeDialog = false
                        }
                    },
                    enabled = amountText.isNotEmpty()
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddIncomeDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

// --- SUB-SCREEN: INDIVIDUAL FEATURE SETTING VIEW ---
@Composable
fun FeatureSettingsView(
    setting: FeatureSetting,
    records: List<IncomeRecord>,
    onToggleEnable: (Boolean) -> Unit,
    onSaveConfig: (String) -> Unit,
    onAddIncome: (Double, String) -> Unit,
    onDeleteIncome: (Int) -> Unit
) {
    var isEnabledState by remember(setting.isEnabled) { mutableStateOf(setting.isEnabled) }
    var configTextState by remember(setting.extraConfig) { mutableStateOf(setting.extraConfig) }

    var incomeAmountText by remember { mutableStateOf("") }
    var incomeDescriptionText by remember { mutableStateOf("") }

    val featureTotalIncome = remember(records) { records.sumOf { it.amount } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feature access control master panel card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ফিচার অ্যাক্সেস কন্ট্রোল (Access Control)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnabledState) "ফিচারটি বর্তমানে সচল আছে" else "ফিচারটি বর্তমানে নিষ্ক্রিয় আছে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEnabledState) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                            Text(
                                text = "এই ফিচারটি সাধারণ ব্যবহারকারীরা ব্যবহার করতে পারবে কি না নিয়ন্ত্রণ করুন।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = isEnabledState,
                            onCheckedChange = { checked ->
                                isEnabledState = checked
                                onToggleEnable(checked)
                            },
                            modifier = Modifier.testTag("feature_toggle_${setting.id}")
                        )
                    }
                }
            }
        }

        // Settings / Config editor section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ফিচার সেটিংস প্যারামিটারস (Configurations)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ফিচারটির বিভিন্ন চার্জ বা ভ্যালু কি-প্যারামিটার আকারে কনফিগার করতে পারেন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = configTextState,
                        onValueChange = { configTextState = it },
                        label = { Text("কনফিগারেশন প্রোপার্টিজ (Key: Value)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onSaveConfig(configTextState) },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "সেভ", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("সেটিংস সেভ করুন", fontSize = 12.sp)
                    }
                }
            }
        }

        // Interactive Income tracker / recorder form block
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "আয় ট্র্যাকিং এন্ট্রি (Income Tracker)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "এই ফিচারের অধীনের যেকোনো কমিশন বা আয় ম্যানুয়ালি লেজারে যুক্ত করুন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = incomeAmountText,
                        onValueChange = { incomeAmountText = it },
                        label = { Text("ইনকাম পরিমাণ (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = incomeDescriptionText,
                        onValueChange = { incomeDescriptionText = it },
                        label = { Text("কাজের বিবরণ / ওপারেশন বিবরণ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val amt = incomeAmountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onAddIncome(amt, incomeDescriptionText)
                                incomeAmountText = ""
                                incomeDescriptionText = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = incomeAmountText.isNotEmpty()
                    ) {
                        Text("যোগ করুন (+)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Income reports system (List + summary metrics)
        item {
            Text(
                text = "ইনকাম রিপোর্ট ও ইতিহাস (Revenue Logs)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
            )
        }

        item {
            // Feature summary box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("এই ফিচারে সর্বমোট আয়:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("৳ ${String.format(Locale.US, "%,.2f", featureTotalIncome)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (records.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোন ইনকাম ট্র্যাকিং রেকর্ড পাওয়া যায়নি।\nউপরে ফরমটি দিয়ে যুক্ত করুন।",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(records) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "৳ ${String.format(Locale.US, "%,.2f", record.amount)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (record.description.isEmpty()) "কোন রিমার্কস নেই" else record.description,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "রিপোর্টার: ${record.reportedBy} • ${formatTimestamp(record.timestamp)}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(onClick = { onDeleteIncome(record.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "ডিলিট করুন",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-SCREEN: MANAGEMENT & USER MANAGEMENT VIEW ---
@Composable
fun UserManagementView(
    users: List<AppUser>,
    features: List<FeatureSetting>,
    onAddUser: (String, String, String, String, List<String>) -> Unit,
    onUpdateUser: (AppUser) -> Unit,
    onDeleteUser: (Int) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var userToEditPerms by remember { mutableStateOf<AppUser?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ইউজার ও অপারেটর রোল ম্যানেজমেন্ট",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ম্যানেজার এবং অপারেটরদের জন্য ফিচার কন্ট্রোল পারমিশন ও এক্সেস অ্যাসাইন করুন।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add User", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("নতুন অপারেটর", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "ইউজার তালিকা (System Users)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        if (users.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("কোন রেজিস্টার্ড অপারেটর নেই", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (user.status == "Suspended") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (user.status == "Suspended") Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User avatar",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = user.username,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (user.status == "Suspended") Color.Gray else MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (user.role == "Admin") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(user.role, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        
                                        // Status representation chip
                                        val isSuspended = user.status == "Suspended"
                                        Text(
                                            text = if (isSuspended) "স্থগিত (Suspended)" else "সক্রিয় (Active)",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSuspended) Color.Red else Color(0xFF4CAF50)
                                        )
                                    }
                                }
                            }

                            // Active controls for managing users
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val nextStatus = if (user.status == "Active") "Suspended" else "Active"
                                        onUpdateUser(user.copy(status = nextStatus))
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (user.status == "Active") Icons.Default.Block else Icons.Default.CheckCircle,
                                        contentDescription = "Toggle status",
                                        tint = if (user.status == "Active") Color.Red else Color(0xFF4CAF50),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { userToEditPerms = user },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = "Edit permissions",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteUser(user.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Features accessibility status listing
                        Text(
                            text = "অ্যাসাইনকৃত ফিচারসমূহ (Allowed Access):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val allowedList = remember(user.allowedFeaturesJson) {
                            user.allowedFeaturesJson.split(",").filter { it.isNotEmpty() }
                        }

                        if (allowedList.isEmpty()) {
                            Text("❌ কোন ফিচার অ্যাক্সেস বরাদ্দ নেই", fontSize = 11.sp, color = Color.Red)
                        } else {
                            // Draw wrap flow of chips
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    allowedList.forEach { fId ->
                                        val fObj = features.find { it.id == fId }
                                        val caption = fObj?.displayNameBangla ?: fId
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    MaterialTheme.colorScheme.surfaceVariant,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(caption, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialogue container: Add user dialog
    if (showAddDialog) {
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var selectedRole by remember { mutableStateOf("Operator") }
        val selectedPerms = remember { mutableStateListOf<String>() }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("নতুন অপারেটর যোগ করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("ব্যবহারকারীর নাম (Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("ইমেইল এড্রেস (Email)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("মোবাইল নম্বর") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Text("রোল নির্বাচন করুন (Role):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedRole == "Operator", onClick = { selectedRole = "Operator" })
                            Text("অপারেটর", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedRole == "Admin", onClick = { selectedRole = "Admin" })
                            Text("এডমিন", fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text("অ্যাক্সেসের জন্য ফিচার সিলেক্ট করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    features.forEach { ft ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedPerms.contains(ft.id)) {
                                        selectedPerms.remove(ft.id)
                                    } else {
                                        selectedPerms.add(ft.id)
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedPerms.contains(ft.id),
                                onCheckedChange = { checked ->
                                    if (checked == true) {
                                        selectedPerms.add(ft.id)
                                    } else {
                                        selectedPerms.remove(ft.id)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(ft.displayNameBangla, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (username.isNotEmpty() && email.isNotEmpty()) {
                            onAddUser(username, email, phone, selectedRole, selectedPerms.toList())
                            showAddDialog = false
                        }
                    },
                    enabled = username.isNotEmpty() && email.isNotEmpty()
                ) {
                    Text("অপারেটর তৈরি করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Modal dialogue container: Edit permissions dialog
    if (userToEditPerms != null) {
        val user = userToEditPerms!!
        val selectedPerms = remember(user.allowedFeaturesJson) {
            mutableStateListOf<String>().apply {
                addAll(user.allowedFeaturesJson.split(",").filter { it.isNotEmpty() })
            }
        }

        AlertDialog(
            onDismissRequest = { userToEditPerms = null },
            title = {
                Text(
                    text = "${user.username} এর পারমিশন এডিট", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("ফিচার লেভেল অ্যাক্সেস কন্ট্রোল:", fontSize = 12.sp, color = Color.Gray)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    
                    features.forEach { ft ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedPerms.contains(ft.id)) {
                                        selectedPerms.remove(ft.id)
                                    } else {
                                        selectedPerms.add(ft.id)
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedPerms.contains(ft.id),
                                onCheckedChange = { checked ->
                                    if (checked == true) {
                                        selectedPerms.add(ft.id)
                                    } else {
                                        selectedPerms.remove(ft.id)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(ft.displayNameBangla, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateUser(user.copy(allowedFeaturesJson = selectedPerms.joinToString(",")))
                        userToEditPerms = null
                    }
                ) {
                    Text("আপডেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToEditPerms = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

// --- SPECIALIZED CONFIGURATION PARSERS ---

fun parseTypingConfig(config: String): Triple<String, String, String> {
    val map = mutableMapOf<String, String>()
    config.split("\n").forEach { line ->
        val parts = line.split(":", limit = 2)
        if (parts.size == 2) {
            map[parts[0].trim()] = parts[1].trim()
        }
    }
    val pay = map["pay_per_5_items"] ?: map["pay_per_word"] ?: "1.50"
    val items = map["items_before_break"] ?: "5"
    val breakMin = map["break_time_minutes"] ?: "10"
    return Triple(pay, items, breakMin)
}

fun parseQuizConfig(config: String): Triple<String, String, String> {
    val map = mutableMapOf<String, String>()
    config.split("\n").forEach { line ->
        val parts = line.split(":", limit = 2)
        if (parts.size == 2) {
            map[parts[0].trim()] = parts[1].trim()
        }
    }
    val pay = map["pay_per_5_quizzes"] ?: map["points_per_quiz"] ?: "2.00"
    val quizzes = map["quizzes_before_break"] ?: "5"
    val breakMin = map["break_time_minutes"] ?: "10"
    return Triple(pay, quizzes, breakMin)
}

fun parseAdViewConfig(config: String): Triple<String, String, String> {
    val map = mutableMapOf<String, String>()
    config.split("\n").forEach { line ->
        val parts = line.split(":", limit = 2)
        if (parts.size == 2) {
            map[parts[0].trim()] = parts[1].trim()
        }
    }
    val ads = map["ads_before_break"] ?: "3"
    val pay = map["pay_per_ads_limit"] ?: map["pay_per_ad_view"] ?: "1.20"
    val breakMin = map["break_time_minutes"] ?: "15"
    return Triple(ads, pay, breakMin)
}

// --- REUSABLE INCOME TRACKING SECTION ---

@Composable
fun FeatureIncomeTrackingSection(
    featureId: String,
    records: List<IncomeRecord>,
    onAddIncome: (Double, String) -> Unit,
    onDeleteIncome: (Int) -> Unit
) {
    var incomeAmountText by remember { mutableStateOf("") }
    var incomeDescriptionText by remember { mutableStateOf("") }
    val featureTotalIncome = remember(records) { records.sumOf { it.amount } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "আয় ট্র্যাকিং এন্ট্রি (Income Tracker)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "এই ফিচারের অধীনের যেকোনো কমিশন বা আয় ম্যানুয়ালি লেজারে যুক্ত করুন।",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = incomeAmountText,
                onValueChange = { incomeAmountText = it },
                label = { Text("ইনকাম পরিমাণ (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = incomeDescriptionText,
                onValueChange = { incomeDescriptionText = it },
                label = { Text("কাজের বিবরণ / ওপারেশন বিবরণ") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val amt = incomeAmountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onAddIncome(amt, incomeDescriptionText)
                        incomeAmountText = ""
                        incomeDescriptionText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = incomeAmountText.isNotEmpty()
            ) {
                Text("যোগ করুন (+)", fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "ইনকাম রিপোর্ট ও ইতিহাস (Revenue Logs)",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("এই ফিচারে সর্বমোট আয়:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("৳ ${String.format(Locale.US, "%,.2f", featureTotalIncome)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (records.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "Empty",
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "কোন ইনকাম ট্র্যাকিং রেকর্ড পাওয়া যায়নি।\nউপরে ফরমটি দিয়ে যুক্ত করুন।",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            records.forEach { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "৳ ${String.format(Locale.US, "%,.2f", record.amount)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (record.description.isEmpty()) "কোন রিমার্কস নেই" else record.description,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "রিপোর্টার: ${record.reportedBy} • ${formatTimestamp(record.timestamp)}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(onClick = { onDeleteIncome(record.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "ডিলিট করুন",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SPECIALIZED SCREEN COMPOSABLES ---

@Composable
fun TypingJobSettingsView(
    setting: FeatureSetting,
    records: List<IncomeRecord>,
    onToggleEnable: (Boolean) -> Unit,
    onSaveConfig: (String) -> Unit,
    onAddIncome: (Double, String) -> Unit,
    onDeleteIncome: (Int) -> Unit
) {
    var isEnabledState by remember(setting.isEnabled) { mutableStateOf(setting.isEnabled) }
    val (origPay, origItems, origBreak) = remember(setting.extraConfig) { parseTypingConfig(setting.extraConfig) }
    
    var payField by remember(origPay) { mutableStateOf(origPay) }
    var itemsField by remember(origItems) { mutableStateOf(origItems) }
    var breakField by remember(origBreak) { mutableStateOf(origBreak) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Access Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "টাইপিং জব অ্যাক্সেস কন্ট্রোল (Access Control)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnabledState) "টাইপিং জব বর্তমানে সচল আছে" else "টাইপিং জব বর্তমানে নিষ্ক্রিয় আছে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEnabledState) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                            Text(
                                text = "এই ফিচারটি সাধারণ ব্যবহারকারীরা ব্যবহার করতে পারবে কি না নিয়ন্ত্রণ করুন।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = isEnabledState,
                            onCheckedChange = { checked ->
                                isEnabledState = checked
                                onToggleEnable(checked)
                            },
                            modifier = Modifier.testTag("feature_toggle_${setting.id}")
                        )
                    }
                }
            }
        }

        // Custom inputs card (3 fields)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "টাইপিং জব সেটিংস কনফিগারেশন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "টাইপিং জবের জন্য পেমেন্ট এবং ব্রেক টাইম পিরিয়ড নির্ধারণ করুন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    OutlinedTextField(
                        value = payField,
                        onValueChange = { payField = it },
                        label = { Text("৫টি টাইপিং করার পেমেন্ট (৳ অথবা সেন্ট)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = itemsField,
                        onValueChange = { itemsField = it },
                        label = { Text("কতটি কাজ সাবমিট করার পর ব্রেক আসবে?") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = breakField,
                        onValueChange = { breakField = it },
                        label = { Text("ব্রেক টাইম লিমিট (মিনিট)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val finalConfig = "pay_per_5_items: $payField\nitems_before_break: $itemsField\nbreak_time_minutes: $breakField"
                            onSaveConfig(finalConfig)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "সেভ", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("টাইপিং সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Shared Income tracking & history section
        item {
            FeatureIncomeTrackingSection(
                featureId = setting.id,
                records = records,
                onAddIncome = onAddIncome,
                onDeleteIncome = onDeleteIncome
            )
        }
    }
}

@Composable
fun QuizJobSettingsView(
    setting: FeatureSetting,
    records: List<IncomeRecord>,
    onToggleEnable: (Boolean) -> Unit,
    onSaveConfig: (String) -> Unit,
    onAddIncome: (Double, String) -> Unit,
    onDeleteIncome: (Int) -> Unit
) {
    var isEnabledState by remember(setting.isEnabled) { mutableStateOf(setting.isEnabled) }
    val (origPay, origQuizzes, origBreak) = remember(setting.extraConfig) { parseQuizConfig(setting.extraConfig) }
    
    var payField by remember(origPay) { mutableStateOf(origPay) }
    var quizzesField by remember(origQuizzes) { mutableStateOf(origQuizzes) }
    var breakField by remember(origBreak) { mutableStateOf(origBreak) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Access Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "কুইজ জব অ্যাক্সেস কন্ট্রোল (Access Control)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnabledState) "কুইজ জব বর্তমানে সচল আছে" else "কুইজ জব বর্তমানে নিষ্ক্রিয় আছে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEnabledState) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                            Text(
                                text = "এই ফিচারটি সাধারণ ব্যবহারকারীরা ব্যবহার করতে পারবে কি না নিয়ন্ত্রণ করুন।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = isEnabledState,
                            onCheckedChange = { checked ->
                                isEnabledState = checked
                                onToggleEnable(checked)
                            },
                            modifier = Modifier.testTag("feature_toggle_${setting.id}")
                        )
                    }
                }
            }
        }

        // Custom inputs card (3 fields)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "কুইজ জব সেটিংস কনফিগারেশন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "কুইজ জবের জন্য পেমেন্ট এবং ব্রেক টাইম পিরিয়ড নির্ধারণ করুন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    OutlinedTextField(
                        value = payField,
                        onValueChange = { payField = it },
                        label = { Text("৫টি কুইজ খেলার পেমেন্ট (৳ অথবা সেন্ট)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = quizzesField,
                        onValueChange = { quizzesField = it },
                        label = { Text("কতটি কুইজ খেলার পর ব্রেক আসবে?") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = breakField,
                        onValueChange = { breakField = it },
                        label = { Text("ব্রেক টাইম লিমিট (মিনিট)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val finalConfig = "pay_per_5_quizzes: $payField\nquizzes_before_break: $quizzesField\nbreak_time_minutes: $breakField"
                            onSaveConfig(finalConfig)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "সেভ", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কুইজ সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Shared Income tracking & history section
        item {
            FeatureIncomeTrackingSection(
                featureId = setting.id,
                records = records,
                onAddIncome = onAddIncome,
                onDeleteIncome = onDeleteIncome
            )
        }
    }
}

@Composable
fun AdViewJobSettingsView(
    setting: FeatureSetting,
    records: List<IncomeRecord>,
    onToggleEnable: (Boolean) -> Unit,
    onSaveConfig: (String) -> Unit,
    onAddIncome: (Double, String) -> Unit,
    onDeleteIncome: (Int) -> Unit
) {
    var isEnabledState by remember(setting.isEnabled) { mutableStateOf(setting.isEnabled) }
    val (origAds, origPay, origBreak) = remember(setting.extraConfig) { parseAdViewConfig(setting.extraConfig) }
    
    var adsField by remember(origAds) { mutableStateOf(origAds) }
    var payField by remember(origPay) { mutableStateOf(origPay) }
    var breakField by remember(origBreak) { mutableStateOf(origBreak) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Access Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "অ্যাড ভিউ অ্যাক্সেস কন্ট্রোল (Access Control)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnabledState) "অ্যাড ভিউ জোন বর্তমানে সচল আছে" else "অ্যাড ভিউ জোন বর্তমানে নিষ্ক্রিয় আছে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEnabledState) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                            Text(
                                text = "এই ফিচারটি সাধারণ ব্যবহারকারীরা ব্যবহার করতে পারবে কি না নিয়ন্ত্রণ করুন।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = isEnabledState,
                            onCheckedChange = { checked ->
                                isEnabledState = checked
                                onToggleEnable(checked)
                            },
                            modifier = Modifier.testTag("feature_toggle_${setting.id}")
                        )
                    }
                }
            }
        }

        // Custom inputs card (3 fields)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "অ্যাড ভিউ সেটিংস কনফিগারেশন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "অ্যাড ভিউ করার ডেইলি সাইকেল লিমিট ও পেমেন্ট নির্ধারণ করুন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    OutlinedTextField(
                        value = adsField,
                        onValueChange = { adsField = it },
                        label = { Text("কতটি অ্যাড দেখার পর ব্রেক আসবে?") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = payField,
                        onValueChange = { payField = it },
                        label = { Text("ঐ সংখ্যক অ্যাড দেখার পেমেন্ট (৳ অথবা সেন্ট)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = breakField,
                        onValueChange = { breakField = it },
                        label = { Text("ব্রেক টাইম লিমিট (মিনিট)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val finalConfig = "ads_before_break: $adsField\npay_per_ads_limit: $payField\nbreak_time_minutes: $breakField"
                            onSaveConfig(finalConfig)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "সেভ", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("অ্যাড ভিউ সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Shared Income tracking & history section
        item {
            FeatureIncomeTrackingSection(
                featureId = setting.id,
                records = records,
                onAddIncome = onAddIncome,
                onDeleteIncome = onDeleteIncome
            )
        }
    }
}

@Composable
fun MicroJobSettingsView(
    setting: FeatureSetting,
    requests: List<MicroJobRequest>,
    records: List<IncomeRecord>,
    onToggleEnable: (Boolean) -> Unit,
    onSaveConfig: (String) -> Unit,
    onUpdateStatus: (MicroJobRequest, String) -> Unit,
    onDeleteRequest: (Int) -> Unit,
    onGenerateDemo: () -> Unit,
    onAddIncome: (Double, String) -> Unit,
    onDeleteIncome: (Int) -> Unit
) {
    var isEnabledState by remember { mutableStateOf(setting.isEnabled) }
    var minPayoutField by remember { mutableStateOf("") }
    var feeField by remember { mutableStateOf("") }

    LaunchedEffect(setting.extraConfig) {
        val lines = setting.extraConfig.lines()
        minPayoutField = lines.find { it.startsWith("min_payout:") }?.substringAfter(":")?.trim() ?: "20"
        feeField = lines.find { it.startsWith("fee_per_job:") }?.substringAfter(":")?.trim() ?: "5%"
    }

    var selectedTab by remember { mutableStateOf("All") } // All, Pending, Approved, Rejected

    val filteredRequests = remember(requests, selectedTab) {
        when (selectedTab) {
            "Pending" -> requests.filter { it.status == "Pending" }
            "Approved" -> requests.filter { it.status == "Approved" }
            "Rejected" -> requests.filter { it.status == "Rejected" }
            else -> requests
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header/title card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = "Micro Job Settings Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = setting.displayNameBangla,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "পরিচালনা করুন এবং ব্যবহারকারীদের জমা দেওয়া মাইক্রো জব টাস্ক অনুমোদন করুন।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Active/Enable toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "মাইক্রো জব জোন অ্যাক্সেস কন্ট্রোল",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnabledState) "মাইক্রো জব জোন বর্তমানে সচল আছে" else "মাইক্রো জব জোন বর্তমানে নিষ্ক্রিয় আছে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEnabledState) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                            Text(
                                text = "এই ফিচারটি সচল থাকলে ব্যবহারকারী অ্যাপ থেকে মাইক্রো জব সাবমিট সম্ভব হবে।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = isEnabledState,
                            onCheckedChange = { checked ->
                                isEnabledState = checked
                                onToggleEnable(checked)
                            },
                            modifier = Modifier.testTag("feature_toggle_${setting.id}")
                        )
                    }
                }
            }
        }

        // Configuration setup card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "মাইক্রো জব বোনাস ও ফি সেটিংস",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ব্যবহারকারীর কাজের পেমেন্ট ফি এবং সর্বনিম্ন উইথড্রয়াল লিমিট কনফিগার করুন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    OutlinedTextField(
                        value = minPayoutField,
                        onValueChange = { minPayoutField = it },
                        label = { Text("সর্বনিম্ন পেআউট লিমিট (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = feeField,
                        onValueChange = { feeField = it },
                        label = { Text("টাস্ক প্রসেসিং চার্জ/সার্ভিস ফি (%)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val finalConfig = "min_payout: $minPayoutField\nfee_per_job: $feeField"
                            onSaveConfig(finalConfig)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_config_button"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "সেভ", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("মাইক্রো জব সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Incoming Job Requests Queue
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ইনকামিং মাইক্রো জব রিকোয়েস্ট (${requests.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Button(
                    onClick = onGenerateDemo,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("generate_demo_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Demo", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ডেমো রিকোয়েস্ট (+)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tab Row filtering
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("All" to "সকল", "Pending" to "পেন্ডিং", "Approved" to "অনুমোদিত", "Rejected" to "প্রত্যাখ্যাত")
                tabs.forEach { (key, label) ->
                    val isSelected = selectedTab == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = key }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // List of Requests
        if (filteredRequests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = "No requests found",
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "কোন মাইক্রো জব রিকোয়েস্ট পাওয়া যায়নি",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "ব্যবহারকারী এখনও কোন কাজ জমা দেয়নি বা ফিল্টারে জিরো রেকর্ড। ডানদিকের 'ডেমো রিকোয়েস্ট' বাটনে চাপুন।",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredRequests, key = { it.id }) { req ->
                val cardBorderColor = when (req.status) {
                    "Approved" -> Color(0xFF4CAF50).copy(alpha = 0.5f)
                    "Rejected" -> Color(0xFFF44336).copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                }
                
                val cardBgColor = when (req.status) {
                    "Approved" -> Color(0xFFE8F5E9).copy(alpha = 0.4f)
                    "Rejected" -> Color(0xFFFFEBEE).copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, cardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = req.jobTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ব্যবহারকারী: ${req.username} (${req.userId})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Payout Badge
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "আয়: ৳${String.format(Locale.US, "%,.2f", req.payoutAmount)}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        Text(
                            text = "জমা দেওয়া প্রমাণ (Proof of Work):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = req.proofText,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date
                            Text(
                                text = "জমা দেওয়ার সময়: ${formatTimestamp(req.submittedAt)}",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )

                            // Status Badge or buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (req.status == "Pending") {
                                    OutlinedButton(
                                        onClick = { onUpdateStatus(req, "Rejected") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336)),
                                        border = BorderStroke(1.dp, Color(0xFFF44336)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp).testTag("reject_button_${req.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("বাতিল", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { onUpdateStatus(req, "Approved") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp).testTag("approve_button_${req.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("অনুমোদন", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    val statusColor = if (req.status == "Approved") Color(0xFF4CAF50) else Color(0xFFF44336)
                                    Text(
                                        text = if (req.status == "Approved") "অনুমোদিত" else "প্রত্যাখ্যাত",
                                        color = statusColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                    
                                    IconButton(
                                        onClick = { onDeleteRequest(req.id) },
                                        modifier = Modifier.size(24.dp).testTag("delete_request_${req.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Shared Income tracking & history section
        item {
            FeatureIncomeTrackingSection(
                featureId = setting.id,
                records = records,
                onAddIncome = onAddIncome,
                onDeleteIncome = onDeleteIncome
            )
        }
    }
}

fun parseSalesConfig(config: String): Triple<String, String, String> {
    val map = mutableMapOf<String, String>()
    config.split("\n").forEach { line ->
        val parts = line.split(":", limit = 2)
        if (parts.size == 2) {
            map[parts[0].trim()] = parts[1].trim()
        }
    }
    // Set friendly defaults if empty or not fully configured
    val dailyLimit = map["daily_limit"] ?: "10"
    val amountPerEntry = map["amount_per_entry"] ?: "15"
    val password = map["password"] ?: "secret123"
    return Triple(dailyLimit, amountPerEntry, password)
}

@Composable
fun SalesJobSettingsView(
    setting: FeatureSetting,
    records: List<IncomeRecord>,
    onToggleEnable: (Boolean) -> Unit,
    onSaveConfig: (String) -> Unit,
    onAddIncome: (Double, String) -> Unit,
    onDeleteIncome: (Int) -> Unit
) {
    var isEnabledState by remember(setting.isEnabled) { mutableStateOf(setting.isEnabled) }
    val (origLimit, origAmount, origPassword) = remember(setting.extraConfig) { parseSalesConfig(setting.extraConfig) }
    
    var dailyLimitField by remember(origLimit) { mutableStateOf(origLimit) }
    var amountField by remember(origAmount) { mutableStateOf(origAmount) }
    var passwordField by remember(origPassword) { mutableStateOf(origPassword) }

    var incomeAmountText by remember { mutableStateOf("") }
    var incomeDescriptionText by remember { mutableStateOf("") }

    val featureTotalIncome = remember(records) { records.sumOf { it.amount } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Description Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when(setting.id) {
                        "gmail" -> Icons.Default.Email
                        "instagram" -> Icons.Default.PhotoCamera
                        "facebook" -> Icons.Default.ThumbUp
                        else -> Icons.Default.Share
                    }
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "${setting.displayNameEnglish} Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = setting.displayNameBangla + " (Sales সেটিংস)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ব্যবহারকারীদের জন্য ডেইলি লিমিট, প্রতিটি বিক্রয়ের রেট এবং অ্যাক্সেস পাসওয়ার্ড কনফিগার করুন।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Access Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "${setting.displayNameBangla} অ্যাক্সেস কন্ট্রোল",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEnabledState) "ফিচারটি বর্তমানে সচল আছে" else "ফিচারটি বর্তমানে নিষ্ক্রিয় আছে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEnabledState) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                            Text(
                                text = "এই ফিচারটি সচল থাকলে ব্যবহারকারী তার অ্যাপ্লিকেশন থেকে অ্যাকাউন্ট বা মেম্বার সেল করতে পারবে।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = isEnabledState,
                            onCheckedChange = { checked ->
                                isEnabledState = checked
                                onToggleEnable(checked)
                            },
                            modifier = Modifier.testTag("feature_toggle_${setting.id}")
                        )
                    }
                }
            }
        }

        // Sales Configuration inputs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "আমদানি বিক্রয় সেটিংস প্যারামিটারস",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "নিচের ৩টি বক্স কনফিগার করুন যা সরাসরি ব্যবহারকারীর অ্যাপে প্রদর্শিত হবে।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    OutlinedTextField(
                        value = dailyLimitField,
                        onValueChange = { dailyLimitField = it },
                        label = { Text("ডেইলি লিমিট (অবধি)") },
                        placeholder = { Text("যেমন: ১০") },
                        supportingText = { Text("ব্যবহারকারী সর্বোচ্চ কতটি অ্যাকাউন্ট/এন্ট্রি সাবমিট করতে পারবেন") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("sales_limit_${setting.id}"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = amountField,
                        onValueChange = { amountField = it },
                        label = { Text("প্রতি এন্ট্রি উপার্জনের পরিমাণ (৳)") },
                        placeholder = { Text("যেমন: ১৫") },
                        supportingText = { Text("সফলভাবে সাবমিট বা সেল নিশ্চিত হলে ইউজার ব্যালেন্সে কত টাকা জমা হবে") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("sales_amount_${setting.id}"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = passwordField,
                        onValueChange = { passwordField = it },
                        label = { Text("ভেরিফিকেশন পাসওয়ার্ড (Password)") },
                        placeholder = { Text("যেমন: p@ss123") },
                        supportingText = { Text("অ্যাকাউন্ট ভ্যালিডেশনের জন্য প্রয়োজনীয় সিক্রেট কী") },
                        modifier = Modifier.fillMaxWidth().testTag("sales_password_${setting.id}"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val finalConfig = "daily_limit: $dailyLimitField\namount_per_entry: $amountField\npassword: $passwordField"
                            onSaveConfig(finalConfig)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_sales_settings_${setting.id}"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "সেভ", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("বিক্রয় সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- EXPLICIT USER APPLICATION PREVIEW ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ব্যবহারকারী অ্যাপে কীভাবে প্রদর্শিত হবে (User End View)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Inside the simulated User app screen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "এন্ট্রি সাবমিশন ফর্ম (${setting.displayNameBangla})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isEnabledState) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isEnabledState) "Active" else "Disabled",
                                        color = if (isEnabledState) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Simulated box 1: Daily limit info
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "১. ডেইলি লিমিট (Daily Limit)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "আপনি সর্বোচ্চ $dailyLimitField টি এন্ট্রি সাবমিট করতে পারবেন",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "দৈনিক লিমিট অতিক্রম করলে পুনরায় আগামীকাল সাবমিট করতে হবে।",
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            // Simulated box 2: Amount earned
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "২. এন্ট্রি রেট (Amount Earned)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "প্রতিটি সঠিক অ্যাকাউন্টের জন্য পাবেন:",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "৳ $amountField",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF4CAF50)
                                        )
                                    }
                                }
                            }

                            // Simulated box 3: Password / Verification Code
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "৩. পাসওয়ার্ড সাবমিশন (Password Required)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "ভেরিফিকেশন সিক্রেট কোড কী?",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = passwordField,
                                                fontSize = 11.sp,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            text = "মোবাইল অ্যাপ ব্যবহারকারীকে কাজের প্রমাণ জমা দেওয়ার সময় এই কোডটি লিখতে হবে।",
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Shared Income tracking & history section
        item {
            FeatureIncomeTrackingSection(
                featureId = setting.id,
                records = records,
                onAddIncome = onAddIncome,
                onDeleteIncome = onDeleteIncome
            )
        }
    }
}

fun parseTelegramWhatsAppConfig(config: String): TableConfig {
    val map = mutableMapOf<String, String>()
    config.split("\n").forEach { line ->
        if (line.isNotBlank()) {
            val parts = line.split(":", limit = 2)
            if (parts.size == 2) {
                map[parts[0].trim()] = parts[1].trim()
            }
        }
    }
    return TableConfig(
        dailyLimit = map["daily_limit"] ?: "5",
        price = map["price_per_item"] ?: "120",
        password = map["password"] ?: "admin786",
        addressLabel = map["address_label"] ?: "BKash / Crypto Wallet Address"
    )
}

data class TableConfig(
    val dailyLimit: String,
    val price: String,
    val password: String,
    val addressLabel: String
)

@Composable
fun TelegramWhatsAppSettingsView(
    setting: FeatureSetting,
    records: List<IncomeRecord>,
    sellRequests: List<AccountSellRequest>,
    onToggleEnable: (Boolean) -> Unit,
    onSaveConfig: (String) -> Unit,
    onAddIncome: (Double, String) -> Unit,
    onDeleteIncome: (Int) -> Unit,
    onAddSellRequest: (String, String, Double) -> Unit,
    onUpdateSellRequestStatus: (AccountSellRequest, String) -> Unit,
    onDeleteSellRequest: (Int) -> Unit
) {
    var isEnabledState by remember(setting.isEnabled) { mutableStateOf(setting.isEnabled) }
    val parsedConfig = remember(setting.extraConfig) { parseTelegramWhatsAppConfig(setting.extraConfig) }

    var dailyLimitField by remember(parsedConfig.dailyLimit) { mutableStateOf(parsedConfig.dailyLimit) }
    var priceField by remember(parsedConfig.price) { mutableStateOf(parsedConfig.price) }
    var passwordField by remember(parsedConfig.password) { mutableStateOf(parsedConfig.password) }
    var addressLabelField by remember(parsedConfig.addressLabel) { mutableStateOf(parsedConfig.addressLabel) }

    var userInputAccount by remember { mutableStateOf("") }
    var userInputAddress by remember { mutableStateOf("") }
    var userSubmitPassword by remember { mutableStateOf("") }
    var submissionError by remember { mutableStateOf("") }
    var submissionSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Description Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = if (setting.id == "telegram") Icons.Default.Send else Icons.Default.Forum
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "${setting.displayNameEnglish} Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = setting.displayNameBangla + " (Account & Community Sell)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ব্যবহারকারীরা তাদের ${setting.displayNameEnglish} অ্যাকাউন্ট, চ্যানেল বা নম্বর এই প্ল্যাটফর্মে সরাসরি বিক্রি বা লিংক করে অর্থ উপার্জন করতে অনুরোধ পাঠাতে পারবেন।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Access Control Switch Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ফিচার স্ট্যাটাস (Feature Status)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEnabledState) "ফিচারটি বর্তমানে সক্রিয় আছে" else "ফিচারটি বর্তমানে নিষ্ক্রিয় আছে",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEnabledState) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }
                        Switch(
                            checked = isEnabledState,
                            onCheckedChange = { checked ->
                                isEnabledState = checked
                                onToggleEnable(checked)
                            },
                            modifier = Modifier.testTag("tg_wa_enable_toggle_${setting.id}")
                        )
                    }
                }
            }
        }

        // 3. Parameters configurations Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "বিক্রয় কন্ট্রোল সেটিংস এবং লিমিট",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ইউজার দ্বারা অ্যাকাউন্ট সেলিং নিয়ন্ত্রণ করার প্যারামিটারসমূহঃ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = dailyLimitField,
                        onValueChange = { dailyLimitField = it },
                        label = { Text("ব্যবহারকারী দৈনিক সর্বোচ্চ বিক্রয় অনুরোধ লিমিট") },
                        supportingText = { Text("প্রতিটি ইউজার দিনে কতটি সাবমিট করতে পারবেন।") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("config_limit_${setting.id}"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = priceField,
                        onValueChange = { priceField = it },
                        label = { Text("প্রতিটি অনুমোদিত অনুরোধের জন্য ইউজার প্রাইজ বা রেট (৳)") },
                        supportingText = { Text("একটি লিংক বা একাউন্ট সেলের জন্য প্রাপ্ত ব্যালেন্স।") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("config_price_${setting.id}"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = passwordField,
                        onValueChange = { passwordField = it },
                        label = { Text("ভেরিফিকেশন পাসওয়ার্ড / গোপন পাসকোড") },
                        supportingText = { Text("ব্যবহারকারী সাবমিট করার সময় এই পাসওয়ার্ডটি দিতে হবে।") },
                        modifier = Modifier.fillMaxWidth().testTag("config_password_${setting.id}"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = addressLabelField,
                        onValueChange = { addressLabelField = it },
                        label = { Text("মেম্বার পেমেন্ট বা অতিরিক্ত এড্রেস ফিল্ডের লেবেল এবং নির্দেশিকা") },
                        supportingText = { Text("ব্যবহারকারীর অ্যাপ্লিকেশনে অতিরিক্ত এড্রেস ফিল্ডটির টাইটেল।") },
                        modifier = Modifier.fillMaxWidth().testTag("config_label_${setting.id}"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val finalConfig = "daily_limit: $dailyLimitField\nprice_per_item: $priceField\npassword: $passwordField\naddress_label: $addressLabelField"
                            onSaveConfig(finalConfig)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_config_btn_${setting.id}"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("সেটিংস প্যারামিটার সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Queue of Incoming Sell Requests from Users
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "ইনকামিং অ্যাকাউন্ট বিক্রয় অনুরোধ কিউ (${sellRequests.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ব্যবহারকারীদের পাঠানো বিক্রি ও ডিরেক্ট লিংক করার অনুরোধসমূহ রিভিউ করুন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (sellRequests.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোন পেন্ডিং বিক্রয় অনুরোধ বা রিকোয়েস্ট পাওয়া যায়নি।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            sellRequests.forEach { req ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (req.status) {
                                            "Approved" -> Color(0xFFE8F5E9).copy(alpha = 0.4f)
                                            "Rejected" -> Color(0xFFFFEBEE).copy(alpha = 0.4f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        }
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(
                                        0.5.dp,
                                        when (req.status) {
                                            "Approved" -> Color(0xFF81C784)
                                            "Rejected" -> Color(0xFFE57373)
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        }
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = req.username,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "ইউজার আইডি: ${req.userId}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = when (req.status) {
                                                            "Approved" -> Color(0xFFE8F5E9)
                                                            "Rejected" -> Color(0xFFFFEBEE)
                                                            else -> MaterialTheme.colorScheme.primaryContainer
                                                        },
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = when(req.status) {
                                                        "Approved" -> "অনুমোদিত"
                                                        "Rejected" -> "বাতিল"
                                                        else -> "পেন্ডিং রিভিউ"
                                                    },
                                                    color = when(req.status) {
                                                        "Approved" -> Color(0xFF2E7D32)
                                                        "Rejected" -> Color(0xFFC62828)
                                                        else -> MaterialTheme.colorScheme.primary
                                                    },
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("অ্যাকাউন্ট/চ্যানেল লিঙ্ক: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(req.accountInfo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.testTag("info_acc_${req.id}"))
                                            }
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("$addressLabelField: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(req.additionalAddress, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.testTag("info_addr_${req.id}"))
                                            }
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("প্রস্তাবিত মূল্য: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("৳ ${req.price}", fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, modifier = Modifier.testTag("info_price_${req.id}"))
                                            }
                                        }

                                        if (req.status == "Pending") {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedButton(
                                                    onClick = { onUpdateSellRequestStatus(req, "Rejected") },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336)),
                                                    border = BorderStroke(1.dp, Color(0xFFF44336)),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(30.dp).testTag("wa_tg_reject_${req.id}")
                                                ) {
                                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("বাতিল করুন", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Button(
                                                    onClick = { onUpdateSellRequestStatus(req, "Approved") },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(30.dp).testTag("wa_tg_approve_${req.id}")
                                                ) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("অনুমোদন দিন", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                IconButton(
                                                    onClick = { onDeleteSellRequest(req.id) },
                                                    modifier = Modifier.size(32.dp).testTag("wa_tg_delete_${req.id}")
                                                ) {
                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Interactive User Application Screen View
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ব্যবহারকারীর মোবাইল অ্যাপ স্ক্রিন সিমুলেশন (Live Interactive Mock)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "${setting.displayNameBangla} থেকে ইনকাম করুন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "নিচের বক্সে আপনার ${setting.displayNameEnglish} অ্যাকাউন্ট এবং নির্দিষ্ট কোড দিয়ে সাবমিট করার মাধ্যমে অ্যাকাউন্ট বিক্রি করতে পারেন।",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("ডেইলি লিমিট", fontSize = 9.sp, color = Color.Gray)
                                    Text("$dailyLimitField টি", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("প্রতিটি সেলের রেট", fontSize = 9.sp, color = Color.Gray)
                                    Text("৳ $priceField", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = userInputAccount,
                                onValueChange = { userInputAccount = it },
                                label = { Text(if (setting.id == "telegram") "Telegram Channel / User ID (যেমন: @channel)" else "WhatsApp Phone Number (যেমন: +৮৮০১৭১২...)" ) },
                                modifier = Modifier.fillMaxWidth().testTag("user_input_acc_${setting.id}"),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )

                            OutlinedTextField(
                                value = userInputAddress,
                                onValueChange = { userInputAddress = it },
                                label = { Text(addressLabelField) },
                                placeholder = { Text("টাকা রিসিভ করার BKash/Nagad বা ক্রিপ্টো ওয়ালেট এড্রেস") },
                                modifier = Modifier.fillMaxWidth().testTag("user_input_addr_${setting.id}"),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )

                            OutlinedTextField(
                                value = userSubmitPassword,
                                onValueChange = { userSubmitPassword = it },
                                label = { Text("ভেরিফিকেশন পাসকোড (Verification Code)") },
                                modifier = Modifier.fillMaxWidth().testTag("user_input_pass_${setting.id}"),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )

                            if (submissionError.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFF44336), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(submissionError, color = Color(0xFFF44336), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (submissionSuccess) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Success", tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("বিক্রয় অনুরোধ সফলভাবে সাবমিট করা হয়েছে! উপরের কিউতে রিভিউ দেখুন।", color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    if (userInputAccount.isBlank() || userInputAddress.isBlank() || userSubmitPassword.isBlank()) {
                                        submissionError = "দয়া করে সকল বক্স পূরণ করুন!"
                                        submissionSuccess = false
                                        return@Button
                                    }
                                    if (userSubmitPassword != passwordField) {
                                        submissionError = "ভেরিফিকেশন সিক্রেট পাসকোডটি ভুল!"
                                        submissionSuccess = false
                                        return@Button
                                    }
                                    // Successfully submit request
                                    submissionError = ""
                                    submissionSuccess = true
                                    val finalPrice = priceField.toDoubleOrNull() ?: 120.0
                                    onAddSellRequest(userInputAccount, userInputAddress, finalPrice)
                                    // Clear interactive form inputs
                                    userInputAccount = ""
                                    userInputAddress = ""
                                    userSubmitPassword = ""
                                },
                                modifier = Modifier.fillMaxWidth().testTag("user_submit_sell_btn_${setting.id}"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Submit", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("বিক্রয়ের জন্য অনুরোধ পাঠান", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Shared Income tracking & history section
        item {
            FeatureIncomeTrackingSection(
                featureId = setting.id,
                records = records,
                onAddIncome = onAddIncome,
                onDeleteIncome = onDeleteIncome
            )
        }
    }
}
