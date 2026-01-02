package com.example.pillalarm.alarm

import android.annotation.SuppressLint
import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AlarmDialog(
    medicineId: String,
    medicineName: String,
    medicineImageUrl: String,
    viewModel: AlarmViewModel,
    onDismiss: () -> Unit
) {
    // This loads data when dialog opens
    LaunchedEffect(medicineId) {
        viewModel.load(medicineId)
    }

    // This observes data changes and updates the UI accordingly
    val savedAlarms by viewModel.alarms.collectAsState()

    // Local State Variables for Time and Days Selection
    var selectedHour by remember { mutableIntStateOf(12) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var isPM by remember { mutableStateOf(false) }

    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val selectedDays = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = "Schedule Medicine",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Scrollable Column for Time Selection (Hour, Minute, AM/PM)
                Text("Set Time", style = MaterialTheme.typography.labelLarge, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour Scroll Picker
                    WheelTimePicker(
                        value = selectedHour,
                        range = 1..12,
                        onValueChange = { selectedHour = it }
                    )

                    Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))

                    // Minute Scroll Picker
                    WheelTimePicker(
                        value = selectedMinute,
                        range = 0..59,
                        onValueChange = { selectedMinute = it }
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    // AM/PM toggle (Yellow for AM, Orange for PM)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))

                            // Logic: AM = Yellow, PM = Orange
                            .background(if (isPM) Color(0xFFFF9800) else Color(0xFFFF5722))
                            .clickable { isPM = !isPM }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = if (isPM) "PM" else "AM",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Days of the Week Selection. User can select multiple days.
                Text("Select Days", style = MaterialTheme.typography.labelLarge, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)

                                // when user selected days = Orange, Unselected = Light Gray
                                .background(if (isSelected) Color(0xFFFF5722) else Color.LightGray)
                                .clickable {
                                    if (isSelected) selectedDays.remove(day) else selectedDays.add(day)
                                }
                        ) {
                            Text(
                                text = day.take(1),
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Add Alarm Button. This will add the alarm to the database when clicked.
                Button(
                    onClick = {
                        viewModel.addAlarm(
                            medicineId = medicineId,
                            medicineName = medicineName,
                            medicineImageUrl = medicineImageUrl,
                            hour = selectedHour,
                            minute = selectedMinute,
                            pm = isPM,
                            days = selectedDays.toList()
                        )
                        selectedDays.clear()
                    },
                    enabled = selectedDays.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Alarm")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Displays saved alarms.
                if (savedAlarms.isNotEmpty()) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Scheduled Alarms:", modifier = Modifier.align(Alignment.Start), style = MaterialTheme.typography.labelMedium)

                    savedAlarms.forEach { alarmEntity ->
                        AlarmEntityRow(
                            alarm = alarmEntity,
                            onDelete = { viewModel.deleteAlarm(alarmEntity) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

// Using wraps standard Android NumberPicker to allow scrolling
@SuppressLint("DefaultLocale")
@Composable
fun WheelTimePicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    AndroidView(
        modifier = Modifier.width(60.dp),
        factory = { context ->
            NumberPicker(context).apply {
                minValue = range.first
                maxValue = range.last
                setFormatter { i -> String.format("%02d", i) } // Always 2 digits
                wrapSelectorWheel = true
                setOnValueChangedListener { _, _, newVal ->
                    onValueChange(newVal)
                }
            }
        },
        update = { view -> // This updates the view if the state changes externally
            if (view.value != value) {
                view.value = value
            }
        }
    )
}

// Display an individual AlarmEntity
@Composable
fun AlarmEntityRow(
    alarm: AlarmEntity,
    onDelete: () -> Unit
) {
    val timeString = "${alarm.hour}:${"%02d".format(alarm.minute)} ${if(alarm.pm) "PM" else "AM"}"
    val daysDisplay = alarm.days.joinToString(", ")

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = timeString, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = daysDisplay, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}