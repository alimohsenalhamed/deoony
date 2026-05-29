package com.deoony.app.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deoony.app.data.database.DebtEntity
import com.deoony.app.ui.theme.BorrowedRose
import com.deoony.app.ui.theme.LentEmerald
import com.deoony.app.ui.theme.PendingAmber
import com.deoony.app.ui.viewmodel.DebtViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DebtItem(
    debt: DebtEntity,
    viewModel: DebtViewModel,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val isPaid = debt.isPaid

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("debt_item_card_${debt.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isPaid) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (isPaid) 0.05f else 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Settle completion checkbox on the right index
            Checkbox(
                checked = isPaid,
                onCheckedChange = { viewModel.toggleDebtPaid(debt) },
                colors = CheckboxDefaults.colors(
                    checkedColor = LentEmerald,
                    uncheckedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                ),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Content Area
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title of debt
                    Text(
                        text = debt.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isPaid) {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        textDecoration = if (isPaid) TextDecoration.LineThrough else null,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Direction chip (لي / عليّ)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (debt.isLentByMe) LentEmerald.copy(alpha = 0.15f)
                                else BorrowedRose.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (debt.isLentByMe) "لي" else "عليّ",
                            color = if (debt.isLentByMe) LentEmerald else BorrowedRose,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitles: Person, due date, alarms
                Text(
                    text = if (debt.isLentByMe) "الدائن (له الأولوية): ${debt.personName}" else "المدين (صاحب المال): ${debt.personName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (isPaid) 0.4f else 0.7f)
                )

                if (debt.dueDate.isNotEmpty() || debt.reminderEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (debt.dueDate.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "تاريخ الاستحقاق",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "سداد: ${debt.dueDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            }
                        }

                        if (debt.reminderEnabled) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    // Instantly trigger simulated alarm notification
                                    viewModel.simulateNotificationTrigger(
                                        debt.title,
                                        debt.personName,
                                        debt.isLentByMe,
                                        debt.amount
                                    )
                                    Toast.makeText(context, "تمت محاكاة تنبيه السداد لتجربته الآن!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = "التنبيه الآمن مفعل",
                                    tint = PendingAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تنبيه غداً (جرب محاكاته)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PendingAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (debt.notes.isNotEmpty() && !isPaid) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ملاحظة: ${debt.notes}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "أُدرج في: ${SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(debt.createdAt))}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Left Side: Amount and modifiers
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${debt.amount} ر.س",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isPaid) {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    } else if (debt.isLentByMe) {
                        LentEmerald
                    } else {
                        BorrowedRose
                    }
                )

                if (!isPaid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.deleteDebt(debt) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = BorrowedRose.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    // Simple Delete icon even for paid debts history database cleanup
                    IconButton(
                        onClick = { viewModel.deleteDebt(debt) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف السجل",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
