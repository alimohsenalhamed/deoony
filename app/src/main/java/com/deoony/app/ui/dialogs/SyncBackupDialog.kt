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
import com.deoony.app.ui.viewmodel.SyncState
import kotlinx.coroutines.launch

@Composable
fun SyncBackupDialog(
    onDismissRequest: () -> Unit,
    viewModel: DebtViewModel,
    syncState: SyncState
) {
    var backupPasteText by remember { mutableStateOf("") }
    var isManualImportMode by remember { mutableStateOf(false) }
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
                            text = if (BuildConfig.GOOGLE_CLIENT_ID.isEmpty()) {
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
                                            .setServerClientId(if (BuildConfig.GOOGLE_CLIENT_ID.isNotEmpty()) BuildConfig.GOOGLE_CLIENT_ID else "100000000000-dummy.apps.googleusercontent.com")
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
                                            viewModel.triggerCloudSync()
                                        } else {
                                            Toast.makeText(context, "لم يتم التعرف على الحساب", Toast.LENGTH_SHORT).show()
                                            viewModel.dismissSyncState()
                                        }
                                    } catch (e: Exception) {
                                        if (BuildConfig.GOOGLE_CLIENT_ID.isEmpty()) {
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
                                                onDismissRequest()
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
                                onDismissRequest()
                            }
                        ) {
                            Text("إغلاق وإتمام")
                        }
                    }
                }
            }
        }
    }
}
