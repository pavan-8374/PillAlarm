package com.example.pillalarm.ui.screen

import android.annotation.SuppressLint
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
    onAlarmSaveList: (List<AlarmModel>) -> Unit

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
                    rotationY = 180f
                }
            ) {
                // BACK MENU
                BackSide(
                    onAlarmClick = { showAlarmDialog = true },
                    onDeleteClick = { showDeleteDialog = true }
                )
            }
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
            onSaveAll = { alarmList ->
                onAlarmSaveList(alarmList)   // NEW CALLBACK
                showAlarmDialog = false
            }
        )
    }
}

    // FRONT SIDE (Medicine image + medicine name + Scheduled Alarms)
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

            // SHOW ALL SCHEDULED ALARMS
            medicine.alarms.forEach { alarm ->
                Text(
                    text = "${alarm.formattedTime} — ${alarm.formattedDays}",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
            TextButton(onClick = onDeleteClick) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        }

    }

    // Simple Alarm Input Dialog
    @SuppressLint("MutableCollectionMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AlarmPickerDialog(
        onDismiss: () -> Unit,
        onSaveAll: (List<AlarmModel>) -> Unit
    ) {
        var alarms by remember { mutableStateOf(mutableListOf<AlarmModel>()) }

        // UI state holder for NEW alarm being created
        var hour by remember { mutableStateOf("") }
        var minute by remember { mutableStateOf("") }
        var isPM by remember { mutableStateOf(false) }
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        var selectedDays by remember { mutableStateOf(mutableSetOf<String>()) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Medicine Alarm Schedule") },
            text = {

                Column {

                    //
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        OutlinedTextField(
                            value = hour,
                            onValueChange = { hour = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("HH") },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(6.dp))

                        OutlinedTextField(
                            value = minute,
                            onValueChange = { minute = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("MM") },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(8.dp))

                        Switch(
                            checked = isPM,
                            onCheckedChange = { isPM = it }
                        )
                        Text(if (isPM) "PM" else "AM")
                    }

                    Spacer(Modifier.height(12.dp))

                    //-------------------- DAYS ROWS --------------------
                    Text("Days:")
                    Spacer(Modifier.height(4.dp))

                    FlowRow {
                        daysOfWeek.forEach { day ->
                            FilterChip(
                                selected = selectedDays.contains(day),
                                onClick = {
                                    if (selectedDays.contains(day)) {
                                        selectedDays.remove(day)
                                    } else {
                                        selectedDays.add(day)
                                    }
                                },
                                label = { Text(day) },
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    //-------------------- ADD ALARM --------------------
                    Button(
                        onClick = {
                            if (hour.isNotBlank() && minute.isNotBlank() && selectedDays.isNotEmpty()) {
                                alarms.add(
                                    AlarmModel(
                                        hour = hour.toInt(),
                                        minute = minute.toInt(),
                                        isPM = isPM,
                                        days = selectedDays.toList()
                                    )
                                )
                                // Reset UI for next alarm
                                hour = ""
                                minute = ""
                                isPM = false
                                selectedDays.clear()
                            }
                        }
                    ) {
                        Text("+ Add Time Schedule")
                    }

                    Spacer(Modifier.height(12.dp))

                    //-------------------- PREVIEW SAVED (INSIDE DIALOG) --------------------
                    alarms.forEach { alarm ->
                        Text(
                            "${alarm.formattedTime} — ${alarm.formattedDays}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = alarms.isNotEmpty(),
                    onClick = {
                        onSaveAll(alarms)
                        onDismiss()
                    }
                ) {
                    Text("Save All")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }




