package com.deoony.app.ui.screens.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deoony.app.data.database.DebtEntity
import com.deoony.app.data.database.TabEntity
import com.deoony.app.ui.IconLibrary
import com.deoony.app.ui.components.DebtItem
import com.deoony.app.ui.components.DeyoniLogo
import com.deoony.app.ui.dialogs.*
import com.deoony.app.ui.theme.*
import com.deoony.app.ui.util.safeParseColor
import com.deoony.app.ui.viewmodel.DebtViewModel
import com.deoony.app.ui.viewmodel.ThemePreference
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: DebtViewModel,
    modifier: Modifier = Modifier,
    showWelcomeInitially: Boolean = false
) {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "لن تظهر التذكيرات حتى تسمح بالإشعارات.", Toast.LENGTH_LONG).show()
            }
        }
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Observe State
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val debts by viewModel.debtsForSelectedTab.collectAsStateWithLifecycle()
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
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Summary calculations (overall or tab-wise)
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
            WelcomeDialog(
                onDismissRequest = { showWelcomeDialog = false }
            )
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

            // 3. User tabs container: Add & Edit
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
                                    imageVector = IconLibrary.getIconByName(tab.iconName),
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
                        DebtItem(
                            debt = debt,
                            viewModel = viewModel,
                            onEditClick = { debtToEdit = debt }
                        )
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
            Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة دين جديد")
        }
    }

    // Dialog Rendering Switches
    if (showAddTabDialog) {
        AddTabDialog(
            onDismissRequest = { showAddTabDialog = false },
            viewModel = viewModel
        )
    }

    tabToEdit?.let { tab ->
        EditTabDialog(
            tab = tab,
            onDismissRequest = { tabToEdit = null },
            viewModel = viewModel
        )
    }

    if (showAddDebtDialog) {
        AddDebtDialog(
            onDismissRequest = { showAddDebtDialog = false },
            viewModel = viewModel
        )
    }

    debtToEdit?.let { debt ->
        EditDebtDialog(
            debt = debt,
            onDismissRequest = { debtToEdit = null },
            viewModel = viewModel
        )
    }

    if (showSyncBackupDialog) {
        SyncBackupDialog(
            onDismissRequest = { showSyncBackupDialog = false },
            viewModel = viewModel,
            syncState = syncState
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            onDismissRequest = { showSettingsDialog = false },
            onShowSyncBackup = { showSyncBackupDialog = true },
            viewModel = viewModel,
            isDark = isDark
        )
    }

    if (showPersonsDialog) {
        PersonsDialog(
            onDismissRequest = { showPersonsDialog = false },
            viewModel = viewModel
        )
    }
}
