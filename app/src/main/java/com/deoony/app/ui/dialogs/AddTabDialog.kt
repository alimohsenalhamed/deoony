package com.deoony.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTabDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: Int, icon: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    
    val colorsList = listOf(
        0xFF388BFD, // Blue
        0xFF30D158, // Green
        0xFFFF9F0A, // Orange
        0xFFBF5AF2, // Purple
        0xFFFF375F, // Red
        0xFF64D2FF  // Cyan
    )
    var selectedColor by remember { mutableStateOf(colorsList[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة قسم أو تبويب جديد") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم القسم *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tab_name_input")
                )

                Text("اختر لون القسم المميز:", style = MaterialTheme.typography.bodyMedium)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(colorsList) { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, selectedColor.toInt(), "ic_tag") },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("tab_confirm_button")
            ) {
                Text("إضافة")
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
