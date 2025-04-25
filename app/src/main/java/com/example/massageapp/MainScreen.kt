package com.example.massageapp

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigation(backgroundColor = Color.White) {
                BottomNavigationItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == "home",
                    onClick = {
                        if (currentRoute != "home") navController.navigate("home")
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_map), contentDescription = "Map") },
                    label = { Text("Map") },
                    selected = currentRoute == "maps",
                    onClick = {
                        if (currentRoute != "maps") navController.navigate("maps")
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_booking), contentDescription = "Booking") },
                    label = { Text("Booking") },
                    selected = currentRoute == "booking",
                    onClick = {
                        if (currentRoute != "booking") navController.navigate("booking")
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_profile), contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentRoute == "profile",
                    onClick = {
                        if (currentRoute != "profile") navController.navigate("profile")
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("maps") { MapsScreen() }
            composable("booking") { BookingScreen() }
            composable("profile") { ProfileScreen(navController) }
        }
    }
}