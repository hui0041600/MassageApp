package com.example.massageapp

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.navigation.NavController

@Composable
fun AdminScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var bookings by remember { mutableStateOf(listOf<Booking>()) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        FirestoreService.listenToAllBookings { updatedBookings ->
            bookings = updatedBookings.sortedBy { it.date + it.time }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("Admin - All Bookings", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(24.dp))

            if (bookings.isEmpty()) {
                Text("No bookings found.")
            } else {
                bookings.forEach { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Date: ${booking.date}")
                            Text("Time: ${booking.time}")
                            Text("Service: ${booking.service}")
                            Text("Name: ${booking.name}")
                            Text("Phone: ${booking.phone}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cancel Appointment",
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.clickable {
                                    FirestoreService.cancelBooking(booking)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Booking canceled.")
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
