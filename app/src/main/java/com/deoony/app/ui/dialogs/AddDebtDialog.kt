package com.deoony.app.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deoony.app.data.database.DebtEntity
import com.deoony.app.ui.theme.BorrowedRose
import com.deoony.app.ui.theme.LentEmerald
import com.deoony.app.ui.theme.PendingAmber
import com.deoony.app.ui.util.getIconByName
import com.deoony.app.ui.viewmodel.DebtViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddDebtDialog(
    onDismissRequest: () -> Unit,
    viewModel: DebtViewModel
) {
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

    Dialog(onDismissRequest = onDismissRequest) {
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
                            DropdownMenu(
                                expanded = showCurrencyMenu,
                                onDismissRequest = { showCurrencyMenu = false }
                            ) {
                                topCurrencies.forEach { curr ->
                                    DropdownMenuItem(
                                        text = { Text(curr, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            selectedCurrency = curr
                                            showCurrencyMenu = false
                                        }
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                otherCurrencies.forEach { curr ->
                                    DropdownMenuItem(
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
                                    onDismissRequest()
                                    Toast.makeText(context, "تم تسجيل عملية الدين وحفظ تفاصيلها بأمان والحمد لله", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("confirm_create_debt_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("إضافة العملية")
                        }
                        OutlinedButton(
                            onClick = onDismissRequest,
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

@Composable
fun EditDebtDialog(
    debt: DebtEntity,
    onDismissRequest: () -> Unit,
    viewModel: DebtViewModel
) {
    var debtTitle by remember { mutableStateOf(debt.title) }
    var partnerName by remember { mutableStateOf(debt.personName) }
    var debtAmountStr by remember { mutableStateOf(debt.amount.toString()) }
    var isLentByMe by remember { mutableStateOf(debt.isLentByMe) }
    var hasReminder by remember { mutableStateOf(debt.reminderEnabled) }
    var notes by remember { mutableStateOf(debt.notes) }

    var selectedDaysPreset by remember { mutableStateOf("بدون تغيير") }
    val presets = listOf("بدون تغيير", "غداً", "بعد أسبوع", "بعد شهر", "مفتوح")
    
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismissRequest) {
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
                                    onDismissRequest()
                                    Toast.makeText(context, "تم حفظ تعديلات عملية الدين بأمان", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("تأكيد التعديل")
                        }
                        OutlinedButton(
                            onClick = onDismissRequest,
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
