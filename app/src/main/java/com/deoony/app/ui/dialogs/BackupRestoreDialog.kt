package com.deoony.app.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreDialog(
    onDismiss: () -> Unit,
    onExport: (callback: (String) -> Unit) -> Unit,
    onImport: (json: String, isMerge: Boolean) -> Unit
) {
    val context = LocalContext.current
    var backupJsonText by remember { mutableStateOf("") }
    var inputJsonText by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) } // 0: Export, 1: Import

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إدارة النسخ الاحتياطي المحلي") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Segmented tab headers
                TabRow(selectedTabIndex = activeTab) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("تصدير البيانات") }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("استيراد البيانات") }
                    )
                }

                if (activeTab == 0) {
                    // Export Tab
                    Text(
                        text = "اضغط على الزر لتوليد نص المزامنة والنسخة الاحتياطية الخاصة بك، ثم انسخه واحفظه بأمان:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = {
                            onExport { json ->
                                backupJsonText = json
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Deoony Backup", json)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم تصدير ونسخ نص النسخة الاحتياطية للحافظة!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("export_backup_generate_btn")
                    ) {
                        Text("توليد ونسخ كود الاحتياط")
                    }

                    if (backupJsonText.isNotEmpty()) {
                        OutlinedTextField(
                            value = backupJsonText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("نص النسخ المتولد") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5
                        )
                    }
                } else {
                    // Import Tab
                    Text(
                        text = "ألصق نص النسخة الاحتياطية هنا ثم اختر طريقة الاستيراد للبدء بالمعالجة:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = inputJsonText,
                        onValueChange = { inputJsonText = it },
                        placeholder = { Text("ألصق نص الـ JSON الاحتياطي هنا...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("import_backup_input"),
                        maxLines = 10
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Merge Button
                        Button(
                            onClick = {
                                if (inputJsonText.isNotBlank()) {
                                    onImport(inputJsonText, true)
                                }
                            },
                            enabled = inputJsonText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.weight(1f).testTag("import_merge_btn")
                        ) {
                            Text("دمج (Merge)")
                        }

                        // Replace Button
                        Button(
                            onClick = {
                                if (inputJsonText.isNotBlank()) {
                                    onImport(inputJsonText, false)
                                }
                            },
                            enabled = inputJsonText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.weight(1f).testTag("import_replace_btn")
                        ) {
                            Text("استبدال كلي (Replace)")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
