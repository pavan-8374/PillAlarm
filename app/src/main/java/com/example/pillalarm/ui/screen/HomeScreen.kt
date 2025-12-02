package com.example.pillalarm.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.* // Added for mutable state
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pillalarm.auth.ImageFiles
import com.example.pillalarm.ui.theme.PillAlarmTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment

data class Medicine(
    val id: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0,
    val userId: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current // Needed to create files
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    val username = user?.email ?: "Guest User"

    // STATE: Holds the list of medicines fetched from Firebase
    val medicineList = remember { mutableStateListOf<Medicine>() }

    // STATE: We need to remember where we told the camera to save the photo
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // 1. REAL-TIME LISTENER: Fetches data when screen loads & updates automatically
    LaunchedEffect(Unit) {
        val userId = user?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("medicines")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        medicineList.clear() // Clear old list
                        for (doc in snapshot.documents) {
                            val med = doc.toObject(Medicine::class.java)?.copy(id = doc.id)
                            if (med != null) {
                                medicineList.add(med)
                            }
                        }
                    }
                }
        }
    }

    // 2. Uses TakePicture instead of 'Preview'
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && tempUri != null) {
                // The image is now saved at 'tempUri'
                uploadImageToFirebase(context, tempUri!!)
            } else {
                Log.e("Camera", "Image capture failed")
            }
        }

    // 3. Permission Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Create the file and launch camera
                val file = ImageFiles.createImageFile(context)
                val uri = ImageFiles.getPhotoUri(context, file)
                tempUri = uri // Save URI to state so we can use it after camera closes
                cameraLauncher.launch(uri)
            } else {
                Log.e("Camera", "Permission denied")
            }
        }

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
                    // Check permission -> Create File -> Launch Camera
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
                Spacer(modifier = Modifier.height(24.dp))

                // 3. UI SECTION: My Medicines Header
                Text(
                    text = "My Medicines",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 4. UI SECTION: The Horizontal List of Images
                if (medicineList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("No medicines added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(medicineList) { medicine ->
                            MedicineCard(medicine)
                        }
                    }
                }
            }
        }
    }
}

// 5. COMPONENT: How each medicine looks
@Composable
fun MedicineCard(medicine: Medicine) {
    Card(
        modifier = Modifier
            .size(120.dp, 160.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // The Image loaded from URL
            AsyncImage(
                model = medicine.imageUrl,
                contentDescription = "Medicine Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Takes up most of the card
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            // Placeholder text (since we haven't added Names yet)
            Text(
                text = "Medicine",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// 6.Now takes a URI and Context, compresses using ImageUtils
fun uploadImageToFirebase(context: Context, imageUri: Uri) {
    val storageRef = FirebaseStorage.getInstance().reference
    val medicineRef = storageRef.child("medicines/${System.currentTimeMillis()}.jpg")

    // Use the ImageUtils helper to compress the file from the URI
    val data = ImageFiles.reduceImageSize(context, imageUri)

    if (data != null) {
        medicineRef.putBytes(data).addOnSuccessListener {
            medicineRef.downloadUrl.addOnSuccessListener { uri ->
                saveMedicineRecord(uri.toString())
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Upload failed", it)
        }
    }
}

fun saveMedicineRecord(imageUrl: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
    val db = FirebaseFirestore.getInstance()

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