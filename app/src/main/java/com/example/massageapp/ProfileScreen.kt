package com.example.massageapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController, modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    var points by remember { mutableStateOf(0) }
    var bookings by remember { mutableStateOf(listOf<Booking>()) }
    var showRedeemDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            listenToUserPoints(uid) { points = it }
            FirestoreService.listenToBookingsForUser(uid) { bookings = it }
        } ?: run {
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Please log in to view your bookings.")
            }
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("My Account", fontSize = 24.sp, modifier = Modifier.padding(bottom = 12.dp))
            Text("🎯 Points: $points", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

            Button(
                onClick = { showRedeemDialog = true },
                enabled = points >= 100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Redeem for Free Massage (100 pts)")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("📅 My Bookings", fontSize = 20.sp, modifier = Modifier.padding(bottom = 12.dp))

            if (bookings.isEmpty()) {
                Text("No upcoming bookings.")
            } else {
                bookings.forEach { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Name: ${booking.name}")
                            Text("Phone: ${booking.phone}")
                            Text("Service: ${booking.service}")
                            Text("Date: ${booking.date}")
                            Text("Time: ${booking.time}")

                            Text(
                                text = "Cancel",
                                modifier = Modifier
                                    .clickable {
                                        FirestoreService.cancelBooking(booking)
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar("Booking canceled.")
                                        }
                                    }
                                    .padding(top = 8.dp),
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = MaterialTheme.colors.onError)
            }
        }
    }

    if (showRedeemDialog) {
        AlertDialog(
            onDismissRequest = { showRedeemDialog = false },
            title = { Text("Redeem Points") },
            text = { Text("Redeem 100 points for a free massage?") },
            confirmButton = {
                TextButton(onClick = {
                    currentUser?.uid?.let { uid ->
                        val newPoints = points - 100
                        db.collection("users").document(uid)
                            .update("points", newPoints)
                            .addOnSuccessListener {
                                showRedeemDialog = false
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Redemption successful!")
                                }
                            }
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRedeemDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun listenToUserPoints(uid: String, onPointsUpdated: (Int) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users").document(uid)

    docRef.addSnapshotListener { snapshot, _ ->
        if (snapshot != null && snapshot.exists()) {
            val points = snapshot.getLong("points")?.toInt() ?: 0
            onPointsUpdated(points)
        } else {
            docRef.set(mapOf("points" to 20))
            onPointsUpdated(20)
        }
    }
}
