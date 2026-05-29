package com.deoony.app.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deoony.app.data.database.PersonEntity
import com.deoony.app.ui.theme.BorrowedRose
import com.deoony.app.ui.theme.LentEmerald
import com.deoony.app.ui.util.getIconByName
import com.deoony.app.ui.viewmodel.DebtViewModel

@Composable
fun PersonsDialog(
    onDismissRequest: () -> Unit,
    viewModel: DebtViewModel
) {
    val persons by viewModel.persons.collectAsStateWithLifecycle()
    val allDebts by viewModel.allDebts.collectAsStateWithLifecycle()
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var selectedPersonHistory by remember { mutableStateOf<PersonEntity?>(null) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "جهات الاتصال المستمرة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPersonHistory = person },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()) {
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
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

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
            properties = DialogProperties(usePlatformDefaultWidth = false)
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
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                        .clickable { selectedDefaultType = type }
                                        .padding(8.dp),
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
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
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
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
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
