package com.example.pillalarm.ui.screen

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun SideMenu(
    username: String,
    onLogout: () -> Unit,
    onMyMedicines: () -> Unit = {},
    onHome: () -> Unit
) {
    // Setting drawer width = 85% of screen width
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.85f

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(drawerWidth),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // Profile Section
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.4f))
            Spacer(Modifier.height(24.dp))

            // Menu Items
            DrawerItem("Home", Icons.Default.Home, onHome)
            DrawerItem("My Medicines", Icons.Default.Medication, onMyMedicines)

            Spacer(Modifier.weight(1f))

            // Sign Out Button
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text("Sign Out From App", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun DrawerItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = title, tint = Color.White)
        Spacer(Modifier.width(12.dp))
        Text(title, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun SideMenuPreview() {
    SideMenu(
        username = "Preview User",
        onLogout = {},
        onHome = {}
    )
}