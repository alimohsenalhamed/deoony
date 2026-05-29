package com.deoony.app.ui.screens.home

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deoony.app.data.database.DebtWithDetails
import com.deoony.app.data.database.TabEntity
import com.deoony.app.ui.dialogs.*
import com.deoony.app.ui.viewmodel.DebtViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: DebtViewModel = viewModel()) {
    val context = LocalContext.current

    // State bindings
    val tabs by viewModel.tabs.collectAsState()
    val persons by viewModel.persons.collectAsState()
    val filteredDebts by viewModel.filteredDebts.collectAsState()
    val selectedTabId by viewModel.selectedTabId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val totalToReceive by viewModel.totalToReceive.collectAsState()
    val totalToPay by viewModel.totalToPay.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    // Dialog flags
    var showAddDebtDialog by remember { mutableStateOf(false) }
    var showAddTabDialog by remember { mutableStateOf(false) }
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var showBackupRestoreDialog by remember { mutableStateOf(false) }
    var showAddPaymentDialogForDebt by remember { mutableStateOf<DebtWithDetails?>(null) }
    var showDebtDetailsDialog by remember { mutableStateOf<DebtWithDetails?>(null) }

    // Display messages as toasts
    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearUiMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ديوني",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.triggerCloudSync() },
                        modifier = Modifier.testTag("sync_button")
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = "المزامنة السحابية")
                    }
                    IconButton(
                        onClick = { showBackupRestoreDialog = true },
                        modifier = Modifier.testTag("backup_button")
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = "النسخ الاحتياطي")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDebtDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_debt_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة دين جديد")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // --- Overview Financial Cards ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Receivable Card (مستحق لي)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("receivable_card"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "مستحق لي",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${"%,.2f".format(totalToReceive)} د.أ",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Payable Card (مستحق علي)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("payable_card"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "مستحق علي",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${"%,.2f".format(totalToPay)} د.أ",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // --- Search Field ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("بحث عن شخص أو ملاحظة...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_field"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // --- Tabs Section (التبويبات المخصصة) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأقسام والتبويبات",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(
                    onClick = { showAddTabDialog = true },
                    modifier = Modifier.testTag("add_tab_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة قسم")
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // All Tab card
                item {
                    FilterChip(
                        selected = selectedTabId == null,
                        onClick = { viewModel.selectTab(null) },
                        label = { Text("الكل") },
                        modifier = Modifier.testTag("tab_all_chip")
                    )
                }

                items(tabs, key = { it.id }) { tab ->
                    FilterChip(
                        selected = selectedTabId == tab.id,
                        onClick = { viewModel.selectTab(tab.id) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(tab.color))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(tab.name)
                            }
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "حذف القسم",
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { viewModel.deleteTab(tab) }
                            )
                        },
                        modifier = Modifier.testTag("tab_chip_${tab.id}")
                    )
                }
            }

            // --- Debts List ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "الديون النشطة والمستحقة",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showAddPersonDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "إضافة شخص")
                }
            }

            if (filteredDebts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد أي ديون مسجلة.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(filteredDebts, key = { it.debt.id }) { details ->
                        val debt = details.debt
                        val person = details.person
                        val tab = details.tab
                        val totalPaid = details.payments.sumOf { it.amountPaid }
                        val remainingAmount = if (debt.amount > 0) {
                            debt.amount - totalPaid
                        } else {
                            Math.abs(debt.amount) - totalPaid
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (debt.isPaid) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("debt_card_${debt.id}")
                                .combinedClickable(
                                    onClick = { showDebtDetailsDialog = details },
                                    onLongClick = { showAddPaymentDialogForDebt = details }
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left status sign indicator
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (debt.isPaid) {
                                                Color.Gray.copy(alpha = 0.2f)
                                            } else if (debt.amount > 0) {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            } else {
                                                MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (debt.isPaid) {
                                            Icons.Default.CheckCircle
                                        } else if (debt.amount > 0) {
                                            Icons.Default.TrendingUp
                                        } else {
                                            Icons.Default.TrendingDown
                                        },
                                        contentDescription = null,
                                        tint = if (debt.isPaid) {
                                            Color.Gray
                                        } else if (debt.amount > 0) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.error
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Person Name and details
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = person.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(tab.color).copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = tab.name,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(tab.color)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (debt.dueDate != null) {
                                        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                                        Text(
                                            text = "الاستحقاق: ${format.format(Date(debt.dueDate))}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                    if (debt.notes.isNotEmpty()) {
                                        Text(
                                            text = debt.notes,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Balance block
                                Column(horizontalAlignment = Alignment.End) {
                                    val formattedBalance = "%,.2f".format(remainingAmount)
                                    Text(
                                        text = "$formattedBalance د.أ",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (debt.isPaid) {
                                            Color.Gray
                                        } else if (debt.amount > 0) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.error
                                        }
                                    )
                                    if (totalPaid > 0 && !debt.isPaid) {
                                        Text(
                                            text = "المدفوع: ${"%,.2f".format(totalPaid)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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

    // --- Render Dialog components cleanly ---
    if (showAddDebtDialog) {
        AddDebtDialog(
            persons = persons,
            tabs = tabs,
            onDismiss = { showAddDebtDialog = false },
            onConfirm = { personId, tabId, amount, notes, dueDate ->
                viewModel.addDebt(personId, tabId, amount, notes, dueDate)
                showAddDebtDialog = false
            },
            onAddPerson = { showAddPersonDialog = true }
        )
    }

    if (showAddTabDialog) {
        AddTabDialog(
            onDismiss = { showAddTabDialog = false },
            onConfirm = { name, color, icon ->
                viewModel.addTab(name, color, icon)
                showAddTabDialog = false
            }
        )
    }

    if (showAddPersonDialog) {
        AddPersonDialog(
            onDismiss = { showAddPersonDialog = false },
            onConfirm = { name, phone, email ->
                viewModel.addPerson(name, phone, email)
                showAddPersonDialog = false
            }
        )
    }

    if (showBackupRestoreDialog) {
        BackupRestoreDialog(
            onDismiss = { showBackupRestoreDialog = false },
            onExport = { callback ->
                viewModel.exportBackupData(callback)
            },
            onImport = { json, isMerge ->
                viewModel.importBackupData(json, isMerge)
                showBackupRestoreDialog = false
            }
        )
    }

    showAddPaymentDialogForDebt?.let { details ->
        AddPaymentDialog(
            debtWithDetails = details,
            onDismiss = { showAddPaymentDialogForDebt = null },
            onConfirm = { amount, notes ->
                viewModel.addPayment(details.debt.id, amount, notes)
                showAddPaymentDialogForDebt = null
            }
        )
    }

    showDebtDetailsDialog?.let { details ->
        DebtDetailsDialog(
            details = details,
            onDismiss = { showDebtDetailsDialog = null },
            onPayFullToggle = {
                viewModel.toggleDebtPaid(details)
                showDebtDetailsDialog = null
            },
            onCancelToggle = {
                viewModel.cancelDebt(details)
                showDebtDetailsDialog = null
            },
            onDelete = {
                viewModel.deleteDebt(details.debt)
                showDebtDetailsDialog = null
            }
        )
    }
}
