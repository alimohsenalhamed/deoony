package com.deoony.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deoony.app.data.database.DebtWithDetails
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailsDialog(
    details: DebtWithDetails,
    onDismiss: () -> Unit,
    onPayFullToggle: () -> Unit,
    onCancelToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val debt = details.debt
    val person = details.person
    val tab = details.tab
    val payments = details.payments

    val format = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dueFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    val totalPaid = payments.sumOf { it.amountPaid }
    val remainingAmount = if (debt.amount > 0) {
        debt.amount - totalPaid
    } else {
        Math.abs(debt.amount) - totalPaid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "تفاصيل المعاملة المالية",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_debt_btn")) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف الدين", tint = MaterialTheme.colorScheme.error)
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Main info header
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "الاسم: ${person.name}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (!person.phoneNumber.isNullOrBlank()) {
                            Text("هاتف: ${person.phoneNumber}", fontSize = 13.sp)
                        }
                        if (!person.email.isNullOrBlank()) {
                            Text("بريد: ${person.email}", fontSize = 13.sp)
                        }
                    }
                }

                // Balance summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("مبلغ الدين الكلي:", fontSize = 13.sp)
                        Text(
                            text = "${"%,.2f".format(Math.abs(debt.amount))} د.أ",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("الرصيد المتبقي:", fontSize = 13.sp)
                        Text(
                            text = "${"%,.2f".format(remainingAmount)} د.أ",
                            fontWeight = FontWeight.Bold,
                            color = if (debt.isPaid) Color.Gray else if (debt.amount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Metadata blocks
                Divider()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("نوع الدين:", fontSize = 13.sp)
                    Text(
                        text = if (debt.amount > 0) "مستحق لي (سلف)" else "مستحق علي (دين)",
                        fontWeight = FontWeight.Bold,
                        color = if (debt.amount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("القسم / التبويب:", fontSize = 13.sp)
                    Text(tab.name, fontWeight = FontWeight.Bold, color = Color(tab.color))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("تاريخ التسجيل:", fontSize = 13.sp)
                    Text(format.format(Date(debt.createdAt)))
                }

                if (debt.dueDate != null) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("تاريخ الاستحقاق والتنبيه:", fontSize = 13.sp)
                        Text(dueFormat.format(Date(debt.dueDate)), fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("حالة السداد الصريحة:", fontSize = 13.sp)
                    Text(
                        text = if (debt.isPaid) "تم السداد كلياً" else if (debt.isCancelled) "ملغي" else "نشط وغير مسدد",
                        fontWeight = FontWeight.Bold,
                        color = if (debt.isPaid) MaterialTheme.colorScheme.tertiary else if (debt.isCancelled) Color.Gray else MaterialTheme.colorScheme.error
                    )
                }

                if (debt.notes.isNotBlank()) {
                    Text("ملاحظات:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(8.dp)
                    ) {
                        Text(debt.notes, fontSize = 13.sp)
                    }
                }

                // Display payment history list
                if (payments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("دفعات وسير السداد الجزئي:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.heightIn(max = 100.dp)) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(payments) { payment ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("دفعة: ${"%,.2f".format(payment.amountPaid)} د.أ", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                            if (payment.notes.isNotBlank()) {
                                                Text(payment.notes, fontSize = 10.sp, color = Color.Gray)
                                            }
                                        }
                                        Text(dueFormat.format(Date(payment.paymentDate)), fontSize = 11.sp)
                                    }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPayFullToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (debt.isPaid) Color.Gray else MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pay_full_toggle_btn")
                    ) {
                        Text(if (debt.isPaid) "تنشيط مجدداً" else "سداد كلي صريح")
                    }

                    if (!debt.isPaid) {
                        Button(
                            onClick = onCancelToggle,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (debt.isCancelled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("cancel_toggle_btn")
                        ) {
                            Text(if (debt.isCancelled) "إعادة تنشيط" else "إلغاء المعاملة")
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
