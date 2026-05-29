package com.deoony.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deoony.app.ui.util.safeParseColor
import com.deoony.app.ui.viewmodel.DebtViewModel

@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    onShowSyncBackup: () -> Unit,
    viewModel: DebtViewModel,
    isDark: Boolean
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "الإعدادات",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Theme background colors (Only in Light Mode)
                if (!isDark) {
                    Text("سمة لون الخلفية (الوضع النهاري)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    val bgOptions = listOf(
                        "افتراضي" to null,
                        "أبيض ناصع" to "#FFFFFF",
                        "رصاصي فاتح" to "#F5F5F5",
                        "بنفسجي هادئ" to "#F9F8FF",
                        "أزرق ثلجي" to "#F0F8FF",
                        "بيج دافئ" to "#FDF5E6"
                    )
                    val currentHex by viewModel.backgroundHex.collectAsStateWithLifecycle()
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(bgOptions) { (label, hex) ->
                            val isSelected = currentHex == hex
                            Box(
                                modifier = Modifier
                                    .size(60.dp, 60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (hex != null) safeParseColor(hex) else MaterialTheme.colorScheme.background)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setBackgroundHex(hex) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 10.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Google Sync Setting
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismissRequest()
                            onShowSyncBackup()
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "المزامنة عبر حساب Google",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "استرداد و ربط بياناتك عبر أجهزة متعددة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("إغلاق")
                }
            }
        }
    }
}
