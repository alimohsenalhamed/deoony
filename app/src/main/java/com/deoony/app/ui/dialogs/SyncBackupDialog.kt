package com.deoony.app.ui.dialogs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.deoony.app.BuildConfig
import com.deoony.app.ui.theme.LentEmerald
import com.deoony.app.ui.viewmodel.DebtViewModel
import com.deoony.app.data.sync.SyncState
import kotlinx.coroutines.launch

@Composable
fun SyncBackupDialog(
    onDismissRequest: () -> Unit,
    viewModel: DebtViewModel,
    syncState: SyncState
) {
    var backupPasteText by remember { mutableStateOf("") }
    var isManualImportMode by remember { mutableStateOf(false) }
    var isMergeMode by remember { mutableStateOf(true) } // Mode selector (Merge vs Replace)
    
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Dialog(onDismissRequest = {
        viewModel.dismissSyncState()
        onDismissRequest()
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
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "مزامنة حساب Google",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "المزامنة السحابية والاحتياطية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "استيراد وتصدير بياناتك بأمان وسهولة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // RENDERING STATE: SYNCING
                item {
                    AnimatedVisibility(
                        visible = syncState is SyncState.SYNCING,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val state = syncState as? SyncState.SYNCING
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
                                text = state?.message ?: "جاري العمل...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // RENDERING STATE: SUCCESS
                item {
                    AnimatedVisibility(
                        visible = syncState is SyncState.SUCCESS,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val state = syncState as? SyncState.SUCCESS
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
                                text = state?.message ?: "اكتملت المزامنة بنجاح!",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = LentEmerald,
                                textAlign = TextAlign.Center
                            )
                            if (!state?.backupCode.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "الرمز البرمجي للنسخة:",
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
                                        text = state!!.backupCode.take(36) + "...",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(state.backupCode))
                                            Toast.makeText(context, "تم نسخ الرمز الاحتياطي بأمان للذاكرة!", Toast.LENGTH_SHORT).show()
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
                }

                // RENDERING STATE: FIREBASE_NOT_CONFIGURED
                item {
                    AnimatedVisibility(
                        visible = syncState is SyncState.FIREBASE_NOT_CONFIGURED,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "فشل",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "المزامنة السحابية غير مفعّلة بعد لعدم توفر ملف الإعدادات (google-services.json). يمكنك استخدام إمكانية النسخ الاحتياطي المحلي بالأسفل بالكامل.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // RENDERING STATE: OFFLINE
                item {
                    AnimatedVisibility(
                        visible = syncState is SyncState.OFFLINE,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "منقطع عن الشبكة",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "أنت غير متصل بالإنترنت حالياً، جاري حفظ كافة العمليات والتغييرات محلياً بأمان.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // RENDERING STATE: FAILED
                item {
                    AnimatedVisibility(
                        visible = syncState is SyncState.FAILED,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val state = syncState as? SyncState.FAILED
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "فشل",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state?.message ?: "عذراً، فشلت العملية.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // MAIN INTERACTION BUTTONS (when IDLE or finished/restored)
                item {
                    if (syncState is SyncState.IDLE || syncState is SyncState.FIREBASE_NOT_CONFIGURED || syncState is SyncState.SUCCESS || syncState is SyncState.FAILED) {
                        val coroutineScope = rememberCoroutineScope()
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.triggerCloudSync()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_sync_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "مزامنة سحابية")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("بدء محاولة المزامنة السحابية")
                            }

                            Button(
                                onClick = {
                                    try {
                                        val localBackup = viewModel.getLocalBackupJson()
                                        clipboardManager.setText(AnnotatedString(localBackup))
                                        Toast.makeText(context, "تم توليد ونسخ كود الاحتياطي المحلي عالي الأمان بنجاح للحافظة!", Toast.LENGTH_LONG).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "فشل إنشاء كود الاحتياطي المحلي: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LentEmerald)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "تصدير محلي")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("توليد ونسخ كود الاحتياطي المحلي")
                            }
                        }
                    }
                }

                // TOGGLE IMPORT / RESTORE SECTION
                item {
                    if (syncState is SyncState.IDLE || syncState is SyncState.FIREBASE_NOT_CONFIGURED || syncState is SyncState.SUCCESS || syncState is SyncState.FAILED) {
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
                                
                                // Selection of mode: Merge or Replace
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { isMergeMode = true }
                                    ) {
                                        RadioButton(
                                            selected = isMergeMode,
                                            onClick = { isMergeMode = true }
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("دمج ذكي (Merge)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { isMergeMode = false }
                                    ) {
                                        RadioButton(
                                            selected = !isMergeMode,
                                            onClick = { isMergeMode = false }
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("استبدال كلي (Replace)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Button(
                                    onClick = {
                                        viewModel.restoreFromBackupText(context, backupPasteText, isMergeMode) { success, msg ->
                                            if (success) {
                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                onDismissRequest()
                                            } else {
                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isMergeMode) LentEmerald else MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isMergeMode) "تأكيد دمج واسترجاع النسخة" else "تأكيد مسح البيانات واستبدال النسخة")
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
                                onDismissRequest()
                            }
                        ) {
                            Text("إغلاق")
                        }
                    }
                }
            }
        }
    }
}
