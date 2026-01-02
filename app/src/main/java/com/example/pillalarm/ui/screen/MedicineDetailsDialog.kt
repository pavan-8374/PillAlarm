package com.example.pillalarm.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MedicineDetailsDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Medicine Name") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medicine Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))
            }
        },
        confirmButton = { // Save button to save medicine name
            Button(
                onClick = { onSave(name) },
                enabled = name.isNotBlank()
            ) { Text("Save Medicine") }
        },
        dismissButton = { // Cancel button to dismiss dialog
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
