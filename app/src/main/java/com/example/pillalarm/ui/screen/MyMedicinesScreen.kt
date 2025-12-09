package com.example.pillalarm.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pillalarm.data.MedicineRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMedicinesScreen(navController: NavController) {

    val medicineList = remember { mutableStateListOf<Medicine>() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Load all medicines for this user
    LaunchedEffect(Unit) {
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("medicines")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    snapshot ?: return@addSnapshotListener
                    medicineList.clear()
                    snapshot.documents.forEach { doc ->
                        doc.toObject(Medicine::class.java)
                            ?.copy(id = doc.id)
                            ?.let { medicineList.add(it) }
                    }

                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Medicines") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (medicineList.isEmpty()) {
                Text("No medicines added yet.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(medicineList) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            onAlarmSave = { newAlarmTime ->
                                MedicineRepository.updateAlarm(medicine.id, newAlarmTime)
                            },
                            onDeleteConfirmed = {
                                MedicineRepository.delete(medicine.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
