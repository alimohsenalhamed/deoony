package com.deoony.app.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deoony.app.data.database.DebtWithDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentDialog(
    debtWithDetails: DebtWithDetails,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, notes: String) -> Unit
) {
    val debt = debtWithDetails.debt
    val person = debtWithDetails.person
    val totalPaid = debtWithDetails.payments.sumOf { it.amountPaid }
    val remainingAmount = if (debt.amount > 0) {
        debt.amount - totalPaid
    } else {
        Math.abs(debt.amount) - totalPaid
    }

    var paymentAmountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل دفعة سداد") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "المدين/الدائن: ${person.name}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "المبلغ المتبقي غير المسدد: ${"%,.2f".format(remainingAmount)} د.أ",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = paymentAmountText,
                    onValueChange = { paymentAmountText = it },
                    label = { Text("مبلغ الدفعة المسدد *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("payment_amount_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات الدفعة") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = paymentAmountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount, notes)
                    }
                },
                enabled = paymentAmountText.toDoubleOrNull() != null,
                modifier = Modifier.testTag("payment_confirm_button")
            ) {
                Text("تسجيل سداد")
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
