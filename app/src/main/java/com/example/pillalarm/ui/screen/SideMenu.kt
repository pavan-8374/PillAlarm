package com.example.pillalarm.ui.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SideMenu(
    username: String,
    onLogout: () -> Unit,
    onMyMedicines: () -> Unit = {},
    onProfile: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        // Profile Icon + Username will be added here
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            modifier = Modifier.size(64.dp)
        )
        Text(text = username, style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))

        // Menu Items
        DrawerItem("Home", Icons.Default.Home, onClick = { /* navigate */ },
        textColor = Color.White, iconTint = Color.White)
        DrawerItem("My Medicines", Icons.Default.Medication, onMyMedicines,Color.White, iconTint = Color.White)
        DrawerItem("Profile", Icons.Default.Person, onProfile, Color.White, iconTint = Color.White)

        Spacer(Modifier.weight(1f))

        // Sign out Button
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
            Spacer(Modifier.width(8.dp))
            Text("Sign Out")
        }
    }
}

@Composable
fun DrawerItem(title: String, icon: ImageVector,
               onClick: () -> Unit,
               textColor: Color = Color.Black,
               iconTint: Color = Color.Black
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint)
            Spacer(Modifier.width(12.dp))
            Text(title, color = textColor)
        }
    }
}
