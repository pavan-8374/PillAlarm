package com.example.pillalarm.ui.screen

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pillalarm.alarm.AlarmDialog
import com.example.pillalarm.alarm.AlarmEntity
import com.example.pillalarm.alarm.AlarmViewModel
import com.example.pillalarm.alarm.AlarmViewModelFactory

@SuppressLint("ObsoleteSdkInt")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MedicineCard(
    medicine: Medicine,
    onDeleteConfirmed: () -> Unit
) {
    val context = LocalContext.current
    var flipped by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAlarmDialog by remember { mutableStateOf(false) }

    // Creating a ViewModel specifically for this card to watch for alarms
    val alarmViewModel: AlarmViewModel = viewModel(
        key = medicine.id, // Unique key ensures each card gets its own alarm data
        factory = AlarmViewModelFactory(context)
    )

    // this loads alarm for this medicine immediately
    LaunchedEffect(medicine.id) {
        alarmViewModel.load(medicine.id)
    }

    // This observes the live list of alarms
    val liveAlarms by alarmViewModel.alarms.collectAsState()
    val rotation = animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        label = "CardRotation"
    )

    Card(
        modifier = Modifier
            .size(width = 140.dp, height = 190.dp)
            .padding(4.dp)
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12 * density
            }
            .clickable { flipped = !flipped },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        if (rotation.value <= 90f) {
            // Pass the live list of alarms to FrontSide
            FrontSide(medicine, liveAlarms)
        } else {
            Box(
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                BackSide(
                    onAlarmClick = {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            context.startActivity(intent)
                        } else {
                            showAlarmDialog = true
                        }
                    },
                    onDeleteClick = { showDeleteDialog = true }
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete") },
            text = { Text("Are you sure you want to delete this medicine?") },
            confirmButton = {
                Button(onClick = {
                    onDeleteConfirmed()
                    showDeleteDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    if (showAlarmDialog) {
        // Pass the SAME ViewModel so the dialog updates the card automatically
        AlarmDialog(
            medicineId = medicine.id,
            medicineName = medicine.name,
            medicineImageUrl = medicine.imageUrl,
            viewModel = alarmViewModel,
            onDismiss = { showAlarmDialog = false }
        )
    }
}

/**
 * Shows the front side of the card with the medicine image and name.
 */
@Composable
fun FrontSide(
    medicine: Medicine,
    alarms: List<AlarmEntity> // Change this type to match what your ViewModel returns (likely AlarmEntity)
) {
    Column(
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        AsyncImage(
            model = medicine.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Check the passed 'alarms' list instead of 'medicine.alarms'
            if (alarms.isNotEmpty()) {
                alarms.take(2).forEach { alarm ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // You might need to format these manually if AlarmEntity doesn't have the properties
                        val amPm = if(alarm.pm) "PM" else "AM"
                        val time = "${alarm.hour}:${"%02d".format(alarm.minute)} $amPm"
                        val days = alarm.days.joinToString(" ")

                        Text(
                            text = days,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (alarms.size > 2) {
                    Text(text = "+${alarms.size - 2} more", fontSize = 10.sp, color = Color.Gray)
                }
            } else {
                Text(
                    text = "No alarm set",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
* Shows the Back side of the card with the medicine image and name.
*/
@Composable
fun BackSide(
    onAlarmClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onAlarmClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Alarm")
        }

        OutlinedButton(
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Delete")
        }
    }
}