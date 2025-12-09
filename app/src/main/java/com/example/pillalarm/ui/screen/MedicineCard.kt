package com.example.pillalarm.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun MedicineCard(
    medicine: Medicine,
    onDeleteConfirmed: () -> Unit,
    onAlarmSave: (Long) -> Unit
) {
    // UI States
    var flipped by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAlarmDialog by remember { mutableStateOf(false) }

    val rotation = animateFloatAsState(
        targetValue = if (flipped) 180f else 0f
    )

    // Card with flip animation
    Card(
        modifier = Modifier
            .size(width = 140.dp, height = 170.dp)
            .padding(4.dp)
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12 * density
            }
            .clickable { flipped = !flipped },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        if (rotation.value <= 90f) {
            // FRONT
            FrontSide(medicine)
        } else {
            Box(
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f        // FIX TEXT MIRRORING
                }
            ){
            // BACK MENU
            BackSide(
                onAlarmClick = { showAlarmDialog = true },
                onDeleteClick = { showDeleteDialog = true }
            )}
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Medicine") },
            text = { Text("Are you sure you want to delete this medicine?") },
            confirmButton = {
                Button(onClick = {
                    onDeleteConfirmed()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    //Alarm Time Picker
    if (showAlarmDialog) {
        AlarmPickerDialog(
            onDismiss = { showAlarmDialog = false },
            onTimeSelected = {
                onAlarmSave(it)
                showAlarmDialog = false
            }
        )
    }
}

// FRONT SIDE (Medicine image + medicine name)
@Composable
fun FrontSide(medicine: Medicine) {
    Column {
        AsyncImage(
            model = medicine.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = medicine.name,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// BACK SIDE (menu with two buttons)
@Composable
fun BackSide(
    onAlarmClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(onClick = onAlarmClick) {
            Text("Set Alarm")
        }
        TextButton(onClick = onDeleteClick){
            Text("Delete", color = MaterialTheme.colorScheme.error)
        }
    }

}

// Simple Alarm Input Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmPickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit
) {
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Alarm Time") },
        text = {
            Column {
                OutlinedTextField(
                    value = hour,
                    onValueChange = { hour = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Hour (0–23)") }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = minute,
                    onValueChange = { minute = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Minute (0–59)") }
                )
            }
        },
        confirmButton = {
            Button(
                enabled = hour.isNotBlank() && minute.isNotBlank(),
                onClick = {
                    val alarm = calculateAlarmMillis(hour, minute)
                    onTimeSelected(alarm)
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Converting hour + minute into millis
fun calculateAlarmMillis(hour: String, minute: String): Long {
    val h = hour.toIntOrNull() ?: 0
    val m = minute.toIntOrNull() ?: 0
    return System.currentTimeMillis() + ((h * 60 + m) * 60 * 1000)
}
