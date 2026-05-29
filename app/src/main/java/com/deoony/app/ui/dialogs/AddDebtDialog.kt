package com.deoony.app.ui.dialogs

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deoony.app.data.database.PersonEntity
import com.deoony.app.data.database.TabEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtDialog(
    persons: List<PersonEntity>,
    tabs: List<TabEntity>,
    onDismiss: () -> Unit,
    onConfirm: (personId: Long, tabId: Long, amount: Double, notes: String, dueDate: Long?) -> Unit,
    onAddPerson: () -> Unit
) {
    val context = LocalContext.current

    var selectedPerson by remember { mutableStateOf<PersonEntity?>(persons.firstOrNull()) }
    var selectedTab by remember { mutableStateOf<TabEntity?>(tabs.firstOrNull()) }
    var amountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isReceivable by remember { mutableStateOf(true) } // true: receivable, false: payable
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    var personExpanded by remember { mutableStateOf(false) }
    var tabExpanded by remember { mutableStateOf(false) }

    // Synchronize default selection if state changes
    LaunchedEffect(persons) {
        if (selectedPerson == null) {
            selectedPerson = persons.firstOrNull()
        }
    }
    LaunchedEffect(tabs) {
        if (selectedTab == null) {
            selectedTab = tabs.firstOrNull()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل دين أو مطالبة جديدة") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Direction Toggle (Segmented Control Representation)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { isReceivable = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReceivable) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isReceivable) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("مستحق لي (سلف)")
                    }

                    Button(
                        onClick = { isReceivable = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isReceivable) MaterialTheme.colorScheme.error else Color.Transparent,
                            contentColor = if (!isReceivable) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("مستحق علي (دين)")
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("المبلغ (د.أ) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_amount_input")
                )

                // Select Person Dropdown
                if (persons.isEmpty()) {
                    Button(
                        onClick = onAddPerson,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إضافة شخص لربط الدين به أولاً")
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedPerson?.name ?: "اختر شخصاً...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("المدين / الدائن *") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { personExpanded = true }
                        )
                        DropdownMenu(
                            expanded = personExpanded,
                            onDismissRequest = { personExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            persons.forEach { person ->
                                DropdownMenuItem(
                                    text = { Text(person.name) },
                                    onClick = {
                                        selectedPerson = person
                                        personExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Select Tab Dropdown
                if (tabs.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedTab?.name ?: "اختر قسماً...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("القسم / التبويب *") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tabExpanded = true }
                        )
                        DropdownMenu(
                            expanded = tabExpanded,
                            onDismissRequest = { tabExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tabs.forEach { tab ->
                                DropdownMenuItem(
                                    text = { Text(tab.name) },
                                    onClick = {
                                        selectedTab = tab
                                        tabExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Due Date Picker
                OutlinedTextField(
                    value = selectedDate?.let {
                        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(it))
                    } ?: "لا يوجد تاريخ استحقاق محدد",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("تاريخ الاستحقاق") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val targetCalendar = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    }
                                    selectedDate = targetCalendar.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "اختر تاريخ")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    val finalSelectedPerson = selectedPerson
                    val finalSelectedTab = selectedTab
                    if (amount > 0 && finalSelectedPerson != null && finalSelectedTab != null) {
                        // If it is payable, we save it as a NEGATIVE amount in the db
                        val finalAmount = if (isReceivable) amount else -amount
                        onConfirm(
                            finalSelectedPerson.id,
                            finalSelectedTab.id,
                            finalAmount,
                            notes,
                            selectedDate
                        )
                    }
                },
                enabled = amountText.toDoubleOrNull() != null && selectedPerson != null && selectedTab != null,
                modifier = Modifier.testTag("debt_confirm_button")
            ) {
                Text("تأكيد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
