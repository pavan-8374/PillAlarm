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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pillalarm.alarm.AlarmDialog
import com.example.pillalarm.alarm.AlarmViewModel
import com.example.pillalarm.alarm.AlarmViewModelFactory


@SuppressLint("ObsoleteSdkInt")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MedicineCard(
    medicine: Medicine,
    onDeleteConfirmed: () -> Unit
    // Removed 'onAlarmSaveList' as the ViewModel handles DB operations now
) {
    val context = LocalContext.current

    var flipped by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAlarmDialog by remember { mutableStateOf(false) }

    val rotation = animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        label = "CardRotation"
    )

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
            FrontSide(medicine)
        } else {
            Box(
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                BackSide(
                    onAlarmClick = {
                        // Check exact alarm permission for Android 12+
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        // Note: If targeting Android 13/14, you might also need notification permissions check here
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

    // --- Delete Confirmation Dialog ---
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

    // --- Alarm Schedule Dialog ---
    if (showAlarmDialog) {
        // Create the ViewModel using the Factory
        val alarmViewModel: AlarmViewModel = viewModel(
            factory = AlarmViewModelFactory(context)
        )

        AlarmDialog(
            medicineId = medicine.id, // Ensure your Medicine class has an 'id' property
            viewModel = alarmViewModel,
            onDismiss = { showAlarmDialog = false }
        )
    }
}

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
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2
        )
    }
}

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
            Text("Delete Medicine")
        }
    }
}