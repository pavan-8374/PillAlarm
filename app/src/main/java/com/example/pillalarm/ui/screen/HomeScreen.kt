package com.example.pillalarm.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.pillalarm.ui.theme.PillAlarmTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.google.firebase.firestore.firestore
import androidx.compose.material.icons.filled.Add
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (navController: NavController) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    val username = user?.email ?: "Guest User"

    // Camera launcher to capture image
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                uploadImageToFirebase(bitmap)
            } else {
                Log.e("Camera", "Image capture failed")
            }
        }

    // Camera Permission launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(null) // USED HERE
            } else {
                Log.e("Camera", "Permission denied")
            }
        }

    // Side menu features
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenu(
                username = username,
                onHome = {
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0)        // Clears entire backstack
                        launchSingleTop = true
                    }
                    scope.launch { drawerState.close() } // Close drawer after navigating
                },
                onMyMedicines = { /* Navigate later */ },
                onProfile = { /* Navigate later */ }
            )
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Medicine")
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text("PillAlarm Home") },
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
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome to PillAlarm Home!",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}
fun uploadImageToFirebase(bitmap: Bitmap) {
    val storageRef = Firebase.storage.reference
    val medicineRef = storageRef.child("medicines/${System.currentTimeMillis()}.jpg")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
    val data = baos.toByteArray()

    medicineRef.putBytes(data).addOnSuccessListener {
        medicineRef.downloadUrl.addOnSuccessListener { uri ->
            saveMedicineRecord(uri.toString())
        }
    }.addOnFailureListener {
        Log.e("Firebase", "Upload failed", it)
    }
}

fun saveMedicineRecord(imageUrl: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
    val db = Firebase.firestore

    val medicine = hashMapOf(
        "userId" to userId,
        "timestamp" to System.currentTimeMillis(),
        "imageUrl" to imageUrl
    )

    db.collection("medicines").add(medicine).addOnSuccessListener {
        Log.d("Firebase", "Medicine saved!")
    }.addOnFailureListener {
        Log.e("Firebase", "Failed to save medicine", it)
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PillAlarmTheme {
        HomeScreen(navController = rememberNavController())
    }
}