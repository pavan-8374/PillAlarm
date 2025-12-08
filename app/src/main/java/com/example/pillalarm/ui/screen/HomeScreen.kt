package com.example.pillalarm.ui.screen

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pillalarm.auth.ImageFiles
import com.example.pillalarm.data.MedicineRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    val username = user?.email ?: "Guest User"

    val medicineList = remember { mutableStateListOf<Medicine>() }
    var showNameDialog by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Fetch medicines from Firestore
    LaunchedEffect(Unit) {
        val userId = user?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("medicines")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    snapshot ?: return@addSnapshotListener
                    medicineList.clear()
                    snapshot.documents.forEach {
                        it.toObject(Medicine::class.java)?.copy(id = it.id)?.let { med ->
                            medicineList.add(med)
                        }
                    }
                }
        }
    }

    // Camera Launcher to take medicine image
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && tempUri != null) {
                capturedImageUri = tempUri
                showNameDialog = true
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val file = ImageFiles.createImageFile(context)
                val uri = ImageFiles.getPhotoUri(context, file)
                tempUri = uri
                cameraLauncher.launch(uri)
            }
        }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenu(
                username = username,
                onHome = { scope.launch { drawerState.close() } },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                onMyMedicines = {
                    scope.launch { drawerState.close() }
                    navController.navigate("my_medicines")
                }
            )
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Icon(Icons.Default.Add, null)
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text("Pill Alarm Home") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Text("My Medicines", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))

                if (medicineList.isEmpty()) {
                    Text("No medicines added yet.")
                } else {
                    LazyRow {
                        items(medicineList) { med ->
                            MedicineCard(
                                medicine = med,
                                onDeleteConfirmed = { MedicineRepository.delete(med.id) },
                                onAlarmSave = { t -> MedicineRepository.updateAlarm(med.id, t) }
                            )

                        }
                    }
                }
            }
        }
    }

    // Show Name Dialog
    if (showNameDialog && capturedImageUri != null) {
        MedicineDetailsDialog(
            onDismiss = { showNameDialog = false },
            onSave = { name, time ->
                MedicineRepository.upload(
                    context,
                    capturedImageUri!!,
                    name,
                    time
                )
                showNameDialog = false
            }
        )
    }
}
