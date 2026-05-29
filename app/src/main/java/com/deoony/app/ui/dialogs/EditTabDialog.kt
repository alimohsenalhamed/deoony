package com.deoony.app.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.deoony.app.data.database.TabEntity
import com.deoony.app.ui.IconLibrary
import com.deoony.app.ui.theme.BorrowedRose
import com.deoony.app.ui.util.safeParseColor
import com.deoony.app.ui.viewmodel.DebtViewModel

@Composable
fun EditTabDialog(
    tab: TabEntity,
    onDismissRequest: () -> Unit,
    viewModel: DebtViewModel
) {
    val context = LocalContext.current
    var tabName by remember { mutableStateOf(tab.name) }
    val allColors = listOf(
        "#6750A4", "#7C4DFF", "#3F51B5", "#0D9488", "#2196F3", "#E91E63", "#FF9800",
        "#4CAF50", "#F44336", "#9C27B0", "#00BCD4", "#009688", "#8BC34A", "#CDDC39",
        "#FFEB3B", "#FFC107", "#795548", "#9E9E9E", "#607D8B", "#F06292", "#E53935"
    )
    var showAllColors by remember { mutableStateOf(false) }
    var selectedColorHex by remember { mutableStateOf(tab.colorHex) }
    var selectedIconName by remember { mutableStateOf(tab.iconName) }

    Dialog(onDismissRequest = onDismissRequest) {
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

                val iconChunks = IconLibrary.iconsList.chunked(6)
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
                                onDismissRequest()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("حفظ التعديل")
                    }

                    Button(
                        onClick = {
                            val hasDebts = viewModel.allDebts.value.any { it.tabId == tab.id }
                            if (hasDebts) {
                                Toast.makeText(context, "لا يمكن حذف هذا التبويب لأنه يحتوي على ديون. يرجى حذف الديون أولاً.", Toast.LENGTH_LONG).show()
                            } else {
                                viewModel.deleteTab(context, tab)
                                Toast.makeText(context, "تم حذف التبويب بنجاح", Toast.LENGTH_SHORT).show()
                                onDismissRequest()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BorrowedRose),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("حذف القسم بالكامل")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onDismissRequest) {
                    Text("تراجع")
                }
            }
        }
    }
}
