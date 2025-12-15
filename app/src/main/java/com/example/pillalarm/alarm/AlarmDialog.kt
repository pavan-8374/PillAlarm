package com.example.pillalarm.alarm

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AlarmDialog(
    medicineId: String, // We need the ID to load specific medicines to specific alarms
    medicineName: String,
    medicineImageUrl: String,
    viewModel: AlarmViewModel, // Pass the ViewModel
    onDismiss: () -> Unit
) {
    // 1. Load data when dialog opens
    LaunchedEffect(medicineId) {
        viewModel.load(medicineId)
    }

    // 2. Observe the Live Data from Room Database
    val savedAlarms by viewModel.alarms.collectAsState()

    // 3. Local State for the "New Alarm" inputs
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
                    .verticalScroll(rememberScrollState()), // Make content scrollable
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- TIME INPUT UI ---
                Text("Set Time", style = MaterialTheme.typography.labelLarge, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeSpinner(value = selectedHour, range = 1..12, onValueChange = { selectedHour = it })
                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    TimeSpinner(value = selectedMinute, range = 0..59, onValueChange = { selectedMinute = it }, format = { "%02d".format(it) })
                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E0E0))
                            .clickable { isPM = !isPM }
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Text(text = if (isPM) "PM" else "AM", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- DAYS SELECTION UI ---
                Text("Select Days", style = MaterialTheme.typography.labelLarge, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    daysOfWeek.forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray)
                                .clickable {
                                    if (isSelected) selectedDays.remove(day) else selectedDays.add(day)
                                }
                        ) {
                            Text(text = day.take(1), color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- ADD BUTTON (Immediate Save) ---
                Button(
                    onClick = {
                        // 4. Call ViewModel to add to DB
                        viewModel.addAlarm(
                            medicineId = medicineId,
                            medicineName = medicineName,
                            medicineImageUrl = medicineImageUrl,
                            hour = selectedHour,
                            minute = selectedMinute,
                            pm = isPM,
                            days = selectedDays.toList()
                        )
                        // Reset inputs
                        selectedDays.clear()
                    },
                    enabled = selectedDays.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Alarm")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- EXISTING ALARMS LIST (From ViewModel) ---
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

// Helper composable for the time spinners

@Composable
fun TimeSpinner(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    format: (Int) -> String = { it.toString() }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(48.dp)
    ) {
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) else onValueChange(range.first) }) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up")
        }
        Text(text = format(value), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) else onValueChange(range.last) }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down")
        }
    }
}

// Display an individual AlarmEntity
@Composable
fun AlarmEntityRow(
    // Assuming 'AlarmEntity' is your Room Entity class
    alarm: AlarmEntity,
    onDelete: () -> Unit
) {
    // Construct display string from Entity fields
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