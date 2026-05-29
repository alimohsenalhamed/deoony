package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.AppDatabase
import com.example.data.database.DebtEntity
import com.example.data.database.TabEntity
import com.example.data.repository.DebtRepository
import com.example.ui.theme.BorrowedRose
import com.example.ui.theme.CardBackgroundDark
import com.example.ui.theme.CardBackgroundLight
import com.example.ui.theme.LentEmerald
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PendingAmber
import com.example.ui.viewmodel.DebtViewModel
import com.example.ui.viewmodel.DebtViewModelFactory
import com.example.ui.viewmodel.SyncState
import com.example.ui.viewmodel.ThemePreference
import java.text.SimpleDateFormat
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

fun getIconByName(name: String): ImageVector? {
    return when (name) {
        "person" -> androidx.compose.material.icons.Icons.Default.Person
        "work" -> androidx.compose.material.icons.Icons.Default.Work
        "home" -> androidx.compose.material.icons.Icons.Default.Home
        "star" -> androidx.compose.material.icons.Icons.Default.Star
        "favorite" -> androidx.compose.material.icons.Icons.Default.Favorite
        "shopping_cart" -> androidx.compose.material.icons.Icons.Default.ShoppingCart
        "school" -> androidx.compose.material.icons.Icons.Default.School
        "face" -> androidx.compose.material.icons.Icons.Default.Face
        else -> null
    }
}

@Composable
fun DeyoniLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(38.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8E24AA), // Elegant radiant Purple 600
                        Color(0xFF673AB7)  // Deep Royal Violet
                    )
                ),
                shape = CircleShape
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Draw an elegant, ultra-minimalist vector-like checklist card inside
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                
                // Draw white tiny checklist sheet
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.25f, h * 0.15f)
                    lineTo(w * 0.75f, h * 0.15f)
                    lineTo(w * 0.75f, h * 0.85f)
                    // zig-zag bottom
                    lineTo(w * 0.65f, h * 0.78f)
                    lineTo(w * 0.55f, h * 0.85f)
                    lineTo(w * 0.45f, h * 0.78f)
                    lineTo(w * 0.35f, h * 0.85f)
                    lineTo(w * 0.25f, h * 0.78f)
                    close()
                }
                drawPath(path, color = Color.White)
                
                // Draw modern tiny list lines
                val lineThickness = h * 0.05f
                val lineLeft = w * 0.35f
                val lineRight = w * 0.65f
                
                // First horizontal line
                drawLine(
                    color = Color(0xFF673AB7),
                    start = androidx.compose.ui.geometry.Offset(lineLeft, h * 0.32f),
                    end = androidx.compose.ui.geometry.Offset(lineRight, h * 0.32f),
                    strokeWidth = lineThickness,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                // Second horizontal line
                drawLine(
                    color = Color(0xFF673AB7),
                    start = androidx.compose.ui.geometry.Offset(lineLeft, h * 0.47f),
                    end = androidx.compose.ui.geometry.Offset(lineRight, h * 0.47f),
                    strokeWidth = lineThickness,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                // Third short horizontal line
                drawLine(
                    color = Color(0xFF673AB7),
                    start = androidx.compose.ui.geometry.Offset(lineLeft, h * 0.62f),
                    end = androidx.compose.ui.geometry.Offset(w * 0.52f, h * 0.62f),
                    strokeWidth = lineThickness,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                
                // Beautiful green badge checked badge status indicator at bottom right
                drawCircle(
                    color = Color(0xFF00E676), // Bright neon green for delightful checkmark success
                    radius = w * 0.18f,
                    center = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.74f)
                )
                
                // Miniature tick check symbol inside the badge
                val tickPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.71f, h * 0.74f)
                    lineTo(w * 0.77f, h * 0.80f)
                    lineTo(w * 0.85f, h * 0.68f)
                }
                drawPath(
                    tickPath,
                    color = Color.White,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = w * 0.045f,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }
        }
    }
}
private fun safeParseColor(colorHex: String, fallbackColor: Color = Color(0xFF6750A4)): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        fallbackColor
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: DebtViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup simple Room construction logic manually
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = DebtRepository(database.debtDao())
        val factory = DebtViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DebtViewModel::class.java]

        val prefs = applicationContext.getSharedPreferences("DeyoniPrefs", android.content.Context.MODE_PRIVATE)
        val currentVersionCode = try { 
            packageManager.getPackageInfo(packageName, 0).versionCode 
        } catch (e: Exception) { 1 }
        val savedVersionCode = prefs.getInt("version_code", -1)
        val showWelcomeInitially = savedVersionCode != currentVersionCode
        if (showWelcomeInitially) {
            prefs.edit().putInt("version_code", currentVersionCode).apply()
        }

        enableEdgeToEdge()
        setContent {
            val themePref by viewModel.themePreference.collectAsStateWithLifecycle()
            val bgHex by viewModel.backgroundHex.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themePref) {
                ThemePreference.SYSTEM -> systemDark
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                // Ensure correct RTL Arabic presentation layout direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val actualBackground = if (!darkTheme && bgHex != null) safeParseColor(bgHex!!) else MaterialTheme.colorScheme.background
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = actualBackground
                    ) { innerPadding ->
                        HomeScreen(
                            viewModel = viewModel,
                            showWelcomeInitially = showWelcomeInitially,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: DebtViewModel,
    showWelcomeInitially: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Observe State
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val debts by viewModel.debtsForSelectedTab.collectAsStateWithLifecycle()
    val allDebts by viewModel.allDebts.collectAsStateWithLifecycle()
    val themePref by viewModel.themePreference.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val triggeredReminders by viewModel.triggeredReminders.collectAsStateWithLifecycle()

    val systemDark = isSystemInDarkTheme()
    val isDark = when (themePref) {
        ThemePreference.SYSTEM -> systemDark
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    // Dialog state controllers
    var showAddTabDialog by remember { mutableStateOf(false) }
    var tabToEdit by remember { mutableStateOf<TabEntity?>(null) }
    var showAddDebtDialog by remember { mutableStateOf(false) }
    var debtToEdit by remember { mutableStateOf<DebtEntity?>(null) }
    var showSyncBackupDialog by remember { mutableStateOf(false) }
    var showPersonsDialog by remember { mutableStateOf(false) }

    // Summary calculations (overall or tab-wise)
    // We compute totals for active selected tab to let user analyze debts perfectly
    val totalsForTab = remember(debts) {
        val lent = debts.filter { it.isLentByMe && !it.isPaid }.sumOf { it.amount }
        val borrowed = debts.filter { !it.isLentByMe && !it.isPaid }.sumOf { it.amount }
        Pair(lent, borrowed)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        var showWelcomeDialog by remember { mutableStateOf(showWelcomeInitially) }
        
        if (showWelcomeDialog) {
            Dialog(onDismissRequest = { showWelcomeDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DeyoniLogo()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "أهلاً بك في ديوني",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "نشكر لك حرصك وتقواك في ضبط وسداد الديون، وهذا من كمال الأمانة. نأمل أن يكون هذا التطبيق عوناً لك على تذكر حقوقك والتزاماتك بكل يسر وطمأنينة.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showWelcomeDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("إغلاق", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Customized Calm Header Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clickable(
                        indication = null, 
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) { showWelcomeDialog = true }) {
                        DeyoniLogo()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ديوني",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "السجل الهادئ لالتزاماتك",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                // Header Utility Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var showSettingsDialog by remember { mutableStateOf(false) }

                    // Persons (Contacts) button
                    IconButton(
                        onClick = { showPersonsDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "جهات الاتصال والأشخاص",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }

                    // Settings button
                    IconButton(
                        onClick = { showSettingsDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "الإعدادات",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }

                    if (showSettingsDialog) {
                        Dialog(onDismissRequest = { showSettingsDialog = false }) {
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text("الإعدادات", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Theme background colors (Only in Light Mode)
                                    if (!isDark) {
                                        Text("سمة لون الخلفية (الوضع النهاري)", style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        val bgOptions = listOf(
                                            "افتراضي" to null,
                                            "أبيض ناصع" to "#FFFFFF",
                                            "رصاصي فاتح" to "#F5F5F5",
                                            "بنفسجي هادئ" to "#F9F8FF",
                                            "أزرق ثلجي" to "#F0F8FF",
                                            "بيج دافئ" to "#FDF5E6"
                                        )
                                        val currentHex by viewModel.backgroundHex.collectAsStateWithLifecycle()
                                        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(bgOptions) { (label, hex) ->
                                                val isSelected = currentHex == hex
                                                Box(
                                                    modifier = Modifier
                                                        .size(60.dp, 60.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(if (hex != null) safeParseColor(hex) else MaterialTheme.colorScheme.background)
                                                        .border(
                                                            width = if (isSelected) 3.dp else 1.dp,
                                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                        .clickable { viewModel.setBackgroundHex(hex) },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(label, fontSize = 10.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }

                                    // Google Sync Setting
                                    Card(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            showSettingsDialog = false
                                            showSyncBackupDialog = true
                                        },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text("المزامنة عبر حساب Google", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                                Text("استرداد و ربط بياناتك عبر أجهزة متعددة", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                    TextButton(
                                        onClick = { showSettingsDialog = false },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("إغلاق")
                                    }
                                }
                            }
                        }
                    }

                    // Theme Preference toggler sequence: System -> Light -> Dark
                    IconButton(
                        onClick = {
                            val nextPref = when (themePref) {
                                ThemePreference.SYSTEM -> ThemePreference.LIGHT
                                ThemePreference.LIGHT -> ThemePreference.DARK
                                ThemePreference.DARK -> ThemePreference.SYSTEM
                            }
                            viewModel.setTheme(nextPref)
                            val toastMsg = when (nextPref) {
                                ThemePreference.SYSTEM -> "تم اختيار موازنة النظام تلقائياً"
                                ThemePreference.LIGHT -> "تم تنشيط الوضع النهاري المضيء"
                                ThemePreference.DARK -> "تم تنشيط الوضع الليلي المريح للعين"
                            }
                            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        val icon = when (themePref) {
                            ThemePreference.SYSTEM -> Icons.Default.SettingsBackupRestore
                            ThemePreference.LIGHT -> Icons.Default.LightMode
                            ThemePreference.DARK -> Icons.Default.DarkMode
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = "تبديل المظهر مريح للعين",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Simulated Notification banner alerts listing
            if (triggeredReminders.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PendingAmber.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PendingAmber.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "تنبيه سداد معلق",
                            tint = PendingAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = triggeredReminders.first(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اضغط لتسوية الدين بالكامل أو حذف الإشعار تالياً.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.clearTriggeredReminder(0) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "إخلاء التنبيه",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 2. Tab totals Summary Block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lent Owed To Me Card ("لي")
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) CardBackgroundDark else CardBackgroundLight
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "مستحقات لي",
                                tint = LentEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "لي",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${totalsForTab.first} ر.س",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = LentEmerald
                        )
                        Text(
                            text = "ديون تستحقها أنت",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }

                // Borrowed Owed By Me Card ("عليّ")
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) CardBackgroundDark else CardBackgroundLight
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = "ديون عليّ",
                                tint = BorrowedRose,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "عليّ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${totalsForTab.second} ر.س",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BorrowedRose
                        )
                        Text(
                            text = "ديون من الغير يتوجب سدادها",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. User tabs container: Add & Edit ("عرض تبيويبات يضيفها ويعدلها المستخدم")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "أقسام الديون والتبويبات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { showAddTabDialog = true },
                    modifier = Modifier.testTag("add_tab_dialog_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة قسم")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "قسم جديد")
                }
            }

            // Scrollable tabs container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                if (tabs.isEmpty()) {
                    Text(
                        text = "جاري تهيئة التبويبات...",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tabs) { tab ->
                            val isSelected = selectedTab?.id == tab.id
                            val tabColor = safeParseColor(tab.colorHex)
                            val containerColor = if (isSelected) tabColor else tabColor.copy(alpha = 0.15f)
                            val textColor = if (isSelected) Color.White else tabColor

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(containerColor)
                                    .combinedClickable(
                                        onClick = { viewModel.selectTab(tab) },
                                        onLongClick = { tabToEdit = tab }
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = com.example.ui.IconLibrary.getIconByName(tab.iconName),
                                    contentDescription = "تبويب",
                                    tint = textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.name,
                                    color = textColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "تعديل القسم",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { tabToEdit = tab }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. List of debts belonging to selected tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentTabString = selectedTab?.name ?: "الأقسام"
                Text(
                    text = "سجل التبويب ($currentTabString)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${debts.size} ديون سارية",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            if (debts.isEmpty()) {
                // Beautiful empty state illustration with calm hint tips
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "السجل هادئ وفارغ",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "هذا التبويب هادئ وخالي من الديون التزاماً!",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اضغط على زر (+) في الأسفل لإضافة دين أو التزام جديد حالاً للبدء.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(debts, key = { it.id }) { debt ->
                        val isPaid = debt.isPaid
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("debt_item_card_${debt.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPaid) {
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 0.dp
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (isPaid) 0.05f else 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Settle completion checkbox on the right index
                                Checkbox(
                                    checked = isPaid,
                                    onCheckedChange = { viewModel.toggleDebtPaid(debt) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = LentEmerald,
                                        uncheckedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                // Content Area
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Title of debt
                                        Text(
                                            text = debt.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPaid) {
                                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                            } else {
                                                MaterialTheme.colorScheme.onBackground
                                            },
                                            textDecoration = if (isPaid) TextDecoration.LineThrough else null,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Direction chip (لي / عليّ)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (debt.isLentByMe) LentEmerald.copy(alpha = 0.15f)
                                                    else BorrowedRose.copy(alpha = 0.15f)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (debt.isLentByMe) "لي" else "عليّ",
                                                color = if (debt.isLentByMe) LentEmerald else BorrowedRose,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Subtitles: Person, due date, alarms
                                    Text(
                                        text = if (debt.isLentByMe) "الدائن (له الأولوية): ${debt.personName}" else "المدين (صاحب المال): ${debt.personName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (isPaid) 0.4f else 0.7f)
                                    )

                                    if (debt.dueDate.isNotEmpty() || debt.reminderEnabled) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (debt.dueDate.isNotEmpty()) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.CalendarMonth,
                                                        contentDescription = "تاريخ الاستحقاق",
                                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "سداد: ${debt.dueDate}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                                    )
                                                }
                                            }

                                            if (debt.reminderEnabled) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clickable {
                                                        // Instantly trigger simulated alarm notification
                                                        viewModel.simulateNotificationTrigger(
                                                            debt.title,
                                                            debt.personName,
                                                            debt.isLentByMe,
                                                            debt.amount
                                                        )
                                                        Toast.makeText(context, "تمت محاكاة تنبيه السداد لتجربته الآن!", Toast.LENGTH_SHORT).show()
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Alarm,
                                                        contentDescription = "التنبيه الآمن مفعل",
                                                        tint = PendingAmber,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "تنبيه غداً (جرب محاكاته)",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = PendingAmber,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (debt.notes.isNotEmpty() && !isPaid) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "ملاحظة: ${debt.notes}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "أُدرج في: ${SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(debt.createdAt))}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Left Side: Amount and modifiers
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "${debt.amount} ر.س",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPaid) {
                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                        } else if (debt.isLentByMe) {
                                            LentEmerald
                                        } else {
                                            BorrowedRose
                                        }
                                    )

                                    if (!isPaid) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row {
                                            IconButton(
                                                onClick = { debtToEdit = debt },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "تعديل",
                                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteDebt(debt) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "حذف",
                                                    tint = BorrowedRose.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        // Simple Delete icon even for paid debts history database cleanup
                                        IconButton(
                                            onClick = { viewModel.deleteDebt(debt) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "حذف السجل",
                                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // 5. FAB Floating Action Button for Adding New Debt
        FloatingActionButton(
            onClick = {
                if (selectedTab == null) {
                    Toast.makeText(context, "الرجاء إضافة قسم أولاً لتسجيل الدين فيه!", Toast.LENGTH_LONG).show()
                } else {
                    showAddDebtDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_debt_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "إضافة دين جديد بالقسم الحالي"
            )
        }
    }

    // ==========================================
    // TABS CRUD DIALOGS
    // ==========================================

    // ADD TAB DIALOG
    if (showAddTabDialog) {
        var tabName by remember { mutableStateOf("") }
        val allColors = listOf(
            "#6750A4", "#7C4DFF", "#3F51B5", "#0D9488", "#2196F3", "#E91E63", "#FF9800",
            "#4CAF50", "#F44336", "#9C27B0", "#00BCD4", "#009688", "#8BC34A", "#CDDC39",
            "#FFEB3B", "#FFC107", "#795548", "#9E9E9E", "#607D8B", "#F06292", "#E53935"
        )
        var showAllColors by remember { mutableStateOf(false) }
        var selectedColorHex by remember { mutableStateOf(allColors.first()) }
        var selectedIconName by remember { mutableStateOf("folder") }

        Dialog(onDismissRequest = { showAddTabDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "إنشاء قسم (تبويب) جديد",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = tabName,
                        onValueChange = { tabName = it },
                        label = { Text("اسم القسم (مثال: شخصي، عائلي، سفر)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_tab_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "اختر لوناً مميزاً للقسم:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(10.dp))

                    val displayColors = if (showAllColors) allColors else allColors.take(7)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        displayColors.chunked(if (showAllColors) 7 else 8).forEachIndexed { index, rowColors ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                            ) {
                                rowColors.forEach { hex ->
                                    val color = safeParseColor(hex)
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (selectedColorHex == hex) 3.dp else 0.dp,
                                                color = if (selectedColorHex == hex) MaterialTheme.colorScheme.onBackground.copy(alpha=0.5f) else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { selectedColorHex = hex }
                                    )
                                }
                                if (index == 0 && !showAllColors) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                            .clickable { showAllColors = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "المزيد", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "اختر أيقونة مميزة للقسم:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(10.dp))

                    val iconChunks = com.example.ui.IconLibrary.iconsList.chunked(6)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconChunks.forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                            ) {
                                chunk.forEach { item ->
                                    val isIconSelected = selectedIconName == item.name
                                    val currentAccent = safeParseColor(selectedColorHex)
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isIconSelected) currentAccent.copy(alpha = 0.2f) else Color.Transparent)
                                            .border(
                                                width = if (isIconSelected) 2.dp else 1.dp,
                                                color = if (isIconSelected) currentAccent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedIconName = item.name }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.labelAr,
                                            tint = if (isIconSelected) currentAccent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (tabName.trim().isNotEmpty()) {
                                    viewModel.addTab(tabName.trim(), selectedColorHex, selectedIconName)
                                    showAddTabDialog = false
                                } else {
                                    Toast.makeText(context, "الرجاء إدخال اسم التبويب!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("confirm_create_tab_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("تأكيد الإنشاء")
                        }
                        OutlinedButton(
                            onClick = { showAddTabDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("إلغاء")
                        }
                    }
                }
            }
        }
    }

    // EDIT/DELETE TAB DIALOG
    tabToEdit?.let { tab ->
        var tabName by remember { mutableStateOf(tab.name) }
        val allColors = listOf(
            "#6750A4", "#7C4DFF", "#3F51B5", "#0D9488", "#2196F3", "#E91E63", "#FF9800",
            "#4CAF50", "#F44336", "#9C27B0", "#00BCD4", "#009688", "#8BC34A", "#CDDC39",
            "#FFEB3B", "#FFC107", "#795548", "#9E9E9E", "#607D8B", "#F06292", "#E53935"
        )
        var showAllColors by remember { mutableStateOf(false) }
        var selectedColorHex by remember { mutableStateOf(tab.colorHex) }
        var selectedIconName by remember { mutableStateOf(tab.iconName) }

        Dialog(onDismissRequest = { tabToEdit = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تعديل أو حذف القسم: ${tab.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = tabName,
                        onValueChange = { tabName = it },
                        label = { Text("اسم القسم") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "اختر لوناً مميزاً للقسم:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(10.dp))

                    val displayColors = if (showAllColors) allColors else allColors.take(7)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        displayColors.chunked(if (showAllColors) 7 else 8).forEachIndexed { index, rowColors ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                            ) {
                                rowColors.forEach { hex ->
                                    val color = safeParseColor(hex)
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (selectedColorHex == hex) 3.dp else 0.dp,
                                                color = if (selectedColorHex == hex) MaterialTheme.colorScheme.onBackground.copy(alpha=0.5f) else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { selectedColorHex = hex }
                                    )
                                }
                                if (index == 0 && !showAllColors) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                            .clickable { showAllColors = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "المزيد", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "اختر أيقونة مميزة للقسم:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(10.dp))

                    val iconChunks = com.example.ui.IconLibrary.iconsList.chunked(6)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconChunks.forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                            ) {
                                chunk.forEach { item ->
                                    val isIconSelected = selectedIconName == item.name
                                    val currentAccent = safeParseColor(selectedColorHex)
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isIconSelected) currentAccent.copy(alpha = 0.2f) else Color.Transparent)
                                            .border(
                                                width = if (isIconSelected) 2.dp else 1.dp,
                                                color = if (isIconSelected) currentAccent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedIconName = item.name }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.labelAr,
                                            tint = if (isIconSelected) currentAccent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (tabName.trim().isNotEmpty()) {
                                    viewModel.editTab(tab, tabName.trim(), selectedColorHex, selectedIconName)
                                    tabToEdit = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("حفظ التعديل")
                        }

                        Button(
                            onClick = {
                                viewModel.deleteTab(tab)
                                Toast.makeText(context, "تم حذف التبويب والديون المترتبة بداخلة بنجاح", Toast.LENGTH_SHORT).show()
                                tabToEdit = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BorrowedRose),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("حذف القسم بالكامل")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(onClick = { tabToEdit = null }) {
                        Text("تراجع")
                    }
                }
            }
        }
    }

    // ==========================================
    // DEBTS CRUD DIALOGS
    // ==========================================

    // ADD DEBT DIALOG
    if (showAddDebtDialog) {
        var debtTitle by remember { mutableStateOf("") }
        var partnerName by remember { mutableStateOf("") }
        var debtAmountStr by remember { mutableStateOf("") }
        var isLentByMe by remember { mutableStateOf(false) } // default "علي" (I borrowed money)
        var notes by remember { mutableStateOf("") }

        // Currency
        var selectedCurrency by remember { mutableStateOf("ر.س") }
        var showCurrencyMenu by remember { mutableStateOf(false) }
        val topCurrencies = listOf("ر.س", "ر.ي", "$")
        val otherCurrencies = listOf("درهم.إ", "ج.م", "د.ك", "د.ب", "ر.ع", "ر.ق", "د.أ", "يورو", "£")

        // Human readable Arabic presets picker variables
        var selectedDaysPreset by remember { mutableStateOf<String?>(null) }
        val presets = listOf("غداً", "بعد أسبوع", "بعد شهر", "مخصص")
        var customDateTime by remember { mutableStateOf<Long?>(null) }
        
        val context = LocalContext.current

        Dialog(onDismissRequest = { showAddDebtDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "تسجيل عملية دين جديدة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Toggle Button for Direction ("لي" دائن vs "علي" مدين)
                    item {
                        Column {
                            Text(text = "نوع المعاملة المالية المعلقة:", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isLentByMe) LentEmerald else Color.Transparent)
                                        .clickable { isLentByMe = true }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "لي",
                                        color = if (isLentByMe) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (!isLentByMe) BorrowedRose else Color.Transparent)
                                        .clickable { isLentByMe = false }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "عليّ",
                                        color = if (!isLentByMe) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Contact Person Name (Move up before title)
                    item {
                        Column {
                            val persons = viewModel.persons.collectAsStateWithLifecycle().value
                            if (persons.isNotEmpty()) {
                                androidx.compose.foundation.lazy.LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    items(persons) { person ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (partnerName == person.name) MaterialTheme.colorScheme.primary.copy(alpha=0.2f) else MaterialTheme.colorScheme.onSurface.copy(alpha=0.05f))
                                                .clickable { 
                                                    partnerName = person.name 
                                                    if (person.defaultType == "لي") isLentByMe = true
                                                    if (person.defaultType == "علي") isLentByMe = false
                                                }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(getIconByName(person.iconName) ?: Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(person.name, style = MaterialTheme.typography.labelMedium)
                                            }
                                        }
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = partnerName,
                                onValueChange = { partnerName = it },
                                label = { Text("الاسم") },
                                modifier = Modifier.fillMaxWidth().testTag("add_debt_person_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Title Input
                    item {
                        OutlinedTextField(
                            value = debtTitle,
                            onValueChange = { debtTitle = it },
                            label = { Text("وصف أو اسم الدين (مثال: مستحقات تصميم شعار)") },
                            modifier = Modifier.fillMaxWidth().testTag("add_debt_title_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Amount & Currency
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = debtAmountStr,
                                onValueChange = { debtAmountStr = it },
                                label = { Text("قيمة المبلغ") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("add_debt_amount_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box {
                                OutlinedButton(
                                    onClick = { showCurrencyMenu = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(top = 6.dp)
                                ) {
                                    Text(text = selectedCurrency, style = MaterialTheme.typography.bodyLarge)
                                }
                                androidx.compose.material3.DropdownMenu(
                                    expanded = showCurrencyMenu,
                                    onDismissRequest = { showCurrencyMenu = false }
                                ) {
                                    topCurrencies.forEach { curr ->
                                        androidx.compose.material3.DropdownMenuItem(
                                            text = { Text(curr, fontWeight = FontWeight.Bold) },
                                            onClick = {
                                                selectedCurrency = curr
                                                showCurrencyMenu = false
                                            }
                                        )
                                    }
                                    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                    otherCurrencies.forEach { curr ->
                                        androidx.compose.material3.DropdownMenuItem(
                                            text = { Text(curr) },
                                            onClick = {
                                                selectedCurrency = curr
                                                showCurrencyMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Preset deadline date list
                    item {
                        Column {
                            Text(text = "تاريخ موعد السداد المقترح (اختياري):", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                presets.forEach { offset ->
                                    val isSelected = selectedDaysPreset == offset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                                            )
                                            .border(
                                                width = if (isSelected) 1.5.dp else 0.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                if (selectedDaysPreset == offset) {
                                                    selectedDaysPreset = null
                                                } else {
                                                    if (offset == "مخصص") {
                                                        val calendar = Calendar.getInstance()
                                                        val timePickerDialog = android.app.TimePickerDialog(
                                                            context,
                                                            { _, hour, minute ->
                                                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                                                calendar.set(Calendar.MINUTE, minute)
                                                                customDateTime = calendar.timeInMillis
                                                                selectedDaysPreset = "مخصص"
                                                            },
                                                            calendar.get(Calendar.HOUR_OF_DAY),
                                                            calendar.get(Calendar.MINUTE),
                                                            false
                                                        )
                                                        android.app.DatePickerDialog(
                                                            context,
                                                            { _, year, month, dayOfMonth ->
                                                                calendar.set(year, month, dayOfMonth)
                                                                timePickerDialog.show()
                                                            },
                                                            calendar.get(Calendar.YEAR),
                                                            calendar.get(Calendar.MONTH),
                                                            calendar.get(Calendar.DAY_OF_MONTH)
                                                        ).show()
                                                    } else {
                                                        selectedDaysPreset = offset
                                                    }
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = offset,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                            if (selectedDaysPreset != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "سيتم تفعيل التذكير الآلي تلقائياً للموعد المحدد.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Notes
                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("تفاصيل أو ملاحظات إضافية (اختياري)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Actions
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val amountDouble = debtAmountStr.toDoubleOrNull()
                                    if (debtTitle.trim().isEmpty() || partnerName.trim().isEmpty() || amountDouble == null) {
                                        Toast.makeText(context, "يرجى تعبئة كافة الحقول الأساسية بدقة وصحة القيم!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Construct calculated date text
                                        var finalDateTime: Long? = null
                                        val cal = Calendar.getInstance()
                                        when (selectedDaysPreset) {
                                            "غداً" -> {
                                                cal.add(Calendar.DAY_OF_YEAR, 1)
                                                finalDateTime = cal.timeInMillis
                                            }
                                            "بعد أسبوع" -> {
                                                cal.add(Calendar.WEEK_OF_YEAR, 1)
                                                finalDateTime = cal.timeInMillis
                                            }
                                            "بعد شهر" -> {
                                                cal.add(Calendar.MONTH, 1)
                                                finalDateTime = cal.timeInMillis
                                            }
                                            "مخصص" -> {
                                                finalDateTime = customDateTime
                                            }
                                        }
                                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                        val formattedDate = if (finalDateTime != null) {
                                            val c = Calendar.getInstance()
                                            c.timeInMillis = finalDateTime
                                            format.format(c.time)
                                        } else ""

                                        val titleWithCurrency = "${debtTitle.trim()} ($selectedCurrency)"

                                        viewModel.addDebt(
                                            title = titleWithCurrency,
                                            personName = partnerName.trim(),
                                            amount = amountDouble,
                                            isLentByMe = isLentByMe,
                                            dueDate = formattedDate,
                                            reminderEnabled = selectedDaysPreset != null,
                                            reminderDateTime = finalDateTime,
                                            notes = notes.trim()
                                        )
                                        showAddDebtDialog = false
                                        Toast.makeText(context, "تم تسجيل عملية الدين وحفظ تفاصيلها بأمان والحمد لله", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("confirm_create_debt_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("إضافة العملية")
                            }
                            OutlinedButton(
                                onClick = { showAddDebtDialog = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("تراجع")
                            }
                        }
                    }
                }
            }
        }
    }

    // EDIT DEBT DIALOG
    debtToEdit?.let { debt ->
        var debtTitle by remember { mutableStateOf(debt.title) }
        var partnerName by remember { mutableStateOf(debt.personName) }
        var debtAmountStr by remember { mutableStateOf(debt.amount.toString()) }
        var isLentByMe by remember { mutableStateOf(debt.isLentByMe) }
        var hasReminder by remember { mutableStateOf(debt.reminderEnabled) }
        var notes by remember { mutableStateOf(debt.notes) }

        var selectedDaysPreset by remember { mutableStateOf("بدون تغيير") }
        val presets = listOf("بدون تغيير", "غداً", "بعد أسبوع", "بعد شهر", "مفتوح")

        Dialog(onDismissRequest = { debtToEdit = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "تعديل تفاصيل عملية الدين",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Toggle Button for Direction
                    item {
                        Column {
                            Text(text = "نوع المعاملة المالية المعلقة:", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isLentByMe) LentEmerald else Color.Transparent)
                                        .clickable { isLentByMe = true }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "لي",
                                        color = if (isLentByMe) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (!isLentByMe) BorrowedRose else Color.Transparent)
                                        .clickable { isLentByMe = false }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "عليّ",
                                        color = if (!isLentByMe) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Title Input
                    item {
                        OutlinedTextField(
                            value = debtTitle,
                            onValueChange = { debtTitle = it },
                            label = { Text("اسم الدين") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Contact Name
                    item {
                        Column {
                            val persons = viewModel.persons.collectAsStateWithLifecycle().value
                            if (persons.isNotEmpty()) {
                                androidx.compose.foundation.lazy.LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    items(persons) { person ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (partnerName == person.name) MaterialTheme.colorScheme.primary.copy(alpha=0.2f) else MaterialTheme.colorScheme.onSurface.copy(alpha=0.05f))
                                                .clickable { 
                                                    partnerName = person.name 
                                                    if (person.defaultType == "لي") isLentByMe = true
                                                    if (person.defaultType == "علي") isLentByMe = false
                                                }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(getIconByName(person.iconName) ?: Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(person.name, style = MaterialTheme.typography.labelMedium)
                                            }
                                        }
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = partnerName,
                                onValueChange = { partnerName = it },
                                label = { Text("الطرف الآخر (الاسم)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Amount
                    item {
                        OutlinedTextField(
                            value = debtAmountStr,
                            onValueChange = { debtAmountStr = it },
                            label = { Text("قيمة المبلغ (ر.س)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Preset deadline date list
                    item {
                        Column {
                            Text(text = "تعديل موعد السداد المقترح:", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                presets.forEach { offset ->
                                    val isSelected = selectedDaysPreset == offset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                                            )
                                            .border(
                                                width = if (isSelected) 1.5.dp else 0.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedDaysPreset = offset }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = offset,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Alert check
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "تنبيهات سريعة",
                                tint = PendingAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تنشيط تذكير السداد",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Checkbox(
                                checked = hasReminder,
                                onCheckedChange = { hasReminder = it },
                                colors = CheckboxDefaults.colors(checkedColor = PendingAmber)
                            )
                        }
                    }

                    // Notes
                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("ملاحظات") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Actions
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val amountDouble = debtAmountStr.toDoubleOrNull()
                                    if (debtTitle.trim().isEmpty() || partnerName.trim().isEmpty() || amountDouble == null) {
                                        Toast.makeText(context, "يرجى ملء الحقول وادخال القيم بدقة!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Construct date
                                        var finalDate = debt.dueDate
                                        var finalReminderTime = debt.reminderDateTime
                                        if (selectedDaysPreset != "بدون تغيير") {
                                            val cal = Calendar.getInstance()
                                            when (selectedDaysPreset) {
                                                "غداً" -> cal.add(Calendar.DAY_OF_YEAR, 1)
                                                "بعد أسبوع" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
                                                "بعد شهر" -> cal.add(Calendar.MONTH, 1)
                                                else -> cal.add(Calendar.YEAR, 1)
                                            }
                                            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                            finalDate = if (selectedDaysPreset == "مفتوح") "" else format.format(cal.time)
                                            finalReminderTime = cal.timeInMillis
                                        }

                                        viewModel.editDebt(
                                            debt = debt,
                                            title = debtTitle.trim(),
                                            personName = partnerName.trim(),
                                            amount = amountDouble,
                                            isLentByMe = isLentByMe,
                                            dueDate = finalDate,
                                            reminderEnabled = hasReminder,
                                            reminderDateTime = if (hasReminder) finalReminderTime else null,
                                            notes = notes.trim()
                                        )
                                        debtToEdit = null
                                        Toast.makeText(context, "تم حفظ تعديلات عملية الدين بأمان", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("تأكيد التعديل")
                            }
                            OutlinedButton(
                                onClick = { debtToEdit = null },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("تراجع")
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // CLOUD SYNC & SECURE BACKUP INTERACTIVE PANEL
    // ==========================================
    if (showSyncBackupDialog) {
        var backupPasteText by remember { mutableStateOf("") }
        var isManualImportMode by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = {
            viewModel.dismissSyncState()
            showSyncBackupDialog = false
        }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle, // Changed icon
                                    contentDescription = "مزامنة حساب Google",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "المزامنة السحابية بحساب Google",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "استرداد و ربط بياناتك عبر أجهزة متعددة",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Progress or result state rendering
                    item {
                        AnimatedVisibility(
                            visible = syncState is SyncState.Syncing,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            val state = syncState as? SyncState.Syncing
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    progress = { state?.progress ?: 0.1f },
                                    modifier = Modifier.size(42.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.5.dp,
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = state?.currentStepString ?: "جاري العمل...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = syncState is SyncState.Success,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            val state = syncState as? SyncState.Success
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(LentEmerald.copy(alpha = 0.1f))
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "مكتمل",
                                    tint = LentEmerald,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state?.message ?: "اكتملت المزامنة وحفظ النسخة!",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LentEmerald,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "الرمز البرمجي للنسخة السحابية:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state?.backupCode?.take(36) + "...",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = {
                                            state?.backupCode?.let { code ->
                                                clipboardManager.setText(AnnotatedString(code))
                                                Toast.makeText(context, "تم نسخ الرمز الاحتياطي بأمان للذاكرة!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "نسخ الرمز الاحتياطي",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Main Info / Core Sync Button
                    item {
                        if (syncState is SyncState.Idle) {
                            Text(
                                text = if (com.example.BuildConfig.GOOGLE_CLIENT_ID.isEmpty()) {
                                    "يجب إضافة GOOGLE_CLIENT_ID في إعدادات الأسرار (Secrets) لتعمل المزامنة المباشرة. اضغط للمحاولة."
                                } else {
                                    "المزامنة الفعلية السحابية عبر حساب Google جاهزة للعمل. انقر أدناه للاتصال."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        if (syncState is SyncState.Idle) {
                            val coroutineScope = rememberCoroutineScope()
                            Button(
                                onClick = { 
                                    coroutineScope.launch {
                                        try {
                                            viewModel.updateSyncState(SyncState.Syncing("جاري طلب تسجيل الدخول عبر جوجل...", 0.1f))
                                            val credentialManager = androidx.credentials.CredentialManager.create(context)
                                            val getGoogleIdOption = com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
                                                .setFilterByAuthorizedAccounts(false)
                                                .setServerClientId(if (com.example.BuildConfig.GOOGLE_CLIENT_ID.isNotEmpty()) com.example.BuildConfig.GOOGLE_CLIENT_ID else "100000000000-dummy.apps.googleusercontent.com")
                                                .setAutoSelectEnabled(true)
                                                .build()
                                            
                                            val request = androidx.credentials.GetCredentialRequest.Builder()
                                                .addCredentialOption(getGoogleIdOption)
                                                .build()
                                                
                                            val result = credentialManager.getCredential(
                                                request = request,
                                                context = context
                                            )
                                            val credential = result.credential
                                            if (credential is androidx.credentials.CustomCredential && credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                                val googleIdTokenCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
                                                viewModel.updateSyncState(SyncState.Syncing("تم تسجيل الدخول: ${googleIdTokenCredential.id}", 0.5f))
                                                viewModel.triggerCloudSync() // Then proceed to real or simulated backup logic based on drive api
                                            } else {
                                                Toast.makeText(context, "لم يتم التعرف على الحساب", Toast.LENGTH_SHORT).show()
                                                viewModel.dismissSyncState()
                                            }
                                        } catch (e: Exception) {
                                            if (com.example.BuildConfig.GOOGLE_CLIENT_ID.isEmpty()) {
                                                Toast.makeText(context, "الرجاء إضافة GOOGLE_CLIENT_ID في نافذة Secrets في قائمة الإعدادات الجانبية", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "عذرا، فشل تسجيل الدخول: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                            viewModel.dismissSyncState()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_sync_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "مزامنة حساب")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("الارتباط بحساب Google (محاكاة)")
                            }
                        }
                    }

                    // Toggle import recovery view
                    item {
                        if (syncState is SyncState.Idle) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isManualImportMode = !isManualImportMode }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "استيراد",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "هل لديك رمز نسخة احتياطية تريد استيراده؟",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (isManualImportMode) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = backupPasteText,
                                        onValueChange = { backupPasteText = it },
                                        placeholder = { Text("ألصق نص الرمز الاحتياطي المنسوخ هنا...") },
                                        textStyle = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .testTag("backup_import_input"),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            viewModel.restoreFromBackupText(backupPasteText) { success, msg ->
                                                if (success) {
                                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                    showSyncBackupDialog = false
                                                } else {
                                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = LentEmerald),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("تأكيد دمج واسترجاع النسخة الاحتياطية")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.dismissSyncState()
                                    showSyncBackupDialog = false
                                }
                            ) {
                                Text("إغلاق وإتمام")
                            }
                        }
                    }
                }
            }
        }

        // PERSONS DIALOG
        if (showPersonsDialog) {
            val persons by viewModel.persons.collectAsStateWithLifecycle()
            val allDebts by viewModel.allDebts.collectAsStateWithLifecycle()
            var showAddPersonDialog by remember { mutableStateOf(false) }
            var selectedPersonHistory by remember { mutableStateOf<com.example.data.database.PersonEntity?>(null) }

            Dialog(
                onDismissRequest = { showPersonsDialog = false },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "جهات الاتصال المستمرة",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = { showPersonsDialog = false }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                                }
                            }
                            androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                            if (persons.isEmpty()) {
                                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "لا توجد جهات اتصال مسجلة حالياً.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                    contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(persons) { person ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth().clickable { selectedPersonHistory = person },
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = getIconByName(person.iconName) ?: Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = person.name,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    )
                                                    Text(
                                                        text = "النوع الافتراضي: ${person.defaultType}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                                    )
                                                }
                                                IconButton(onClick = { viewModel.deletePerson(person) }) {
                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف شخص", tint = BorrowedRose)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        FloatingActionButton(
                            onClick = { showAddPersonDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "إضافة شخص")
                        }
                    }
                }
            }

            if (selectedPersonHistory != null) {
                Dialog(onDismissRequest = { selectedPersonHistory = null }) {
                    Card(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "تاريخ معاملات: ${selectedPersonHistory?.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = { selectedPersonHistory = null }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                                }
                            }
                            androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                            val history = allDebts.filter { it.personName == selectedPersonHistory?.name }
                            if (history.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("لا توجد سجلات مالية لهذا الشخص.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    items(history) { debt ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                        ) {
                                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(debt.title, fontWeight = FontWeight.Bold)
                                                    Text(if (debt.isPaid) "تم السداد" else "غير مسدد", color = if (debt.isPaid) LentEmerald else BorrowedRose, fontSize = 12.sp)
                                                }
                                                Text(
                                                    text = "${debt.amount}",
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (debt.isLentByMe) LentEmerald else BorrowedRose
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showAddPersonDialog) {
                var newPersonName by remember { mutableStateOf("") }
                var selectedDefaultType by remember { mutableStateOf("غير محدد") }
                val types = listOf("غير محدد", "لي", "علي")
                var selectedIcon by remember { mutableStateOf("person") }
                val icons = listOf("person", "work", "home", "star", "favorite", "shopping_cart", "school", "face")

                Dialog(
                    onDismissRequest = { showAddPersonDialog = false },
                    properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Text("إضافة شخص لجهات الاتصال", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            item {
                                OutlinedTextField(
                                    value = newPersonName,
                                    onValueChange = { newPersonName = it },
                                    label = { Text("الاسم") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                Text("نوع المعاملة الافتراضي (اختياري)", style = MaterialTheme.typography.labelMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    types.forEach { type ->
                                        val isSelected = selectedDefaultType == type
                                        Box(
                                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                                .clickable { selectedDefaultType = type }.padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(type, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    }
                                }
                            }
                            item {
                                Text("اختر رمزاً معبراً", style = MaterialTheme.typography.labelMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    icons.take(4).forEach { iconName ->
                                        val isSelected = selectedIcon == iconName
                                        Box(
                                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                                .clickable { selectedIcon = iconName },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = getIconByName(iconName) ?: Icons.Default.Person, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    icons.drop(4).take(4).forEach { iconName ->
                                        val isSelected = selectedIcon == iconName
                                        Box(
                                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                                .clickable { selectedIcon = iconName },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = getIconByName(iconName) ?: Icons.Default.Person, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { showAddPersonDialog = false }) { Text("إلغاء") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (newPersonName.isNotBlank()) {
                                                viewModel.addPerson(newPersonName.trim(), selectedDefaultType, selectedIcon)
                                                showAddPersonDialog = false
                                            } else {
                                                Toast.makeText(context, "يرجى كتابة الاسم", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) { Text("إضافة للقائمة") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
