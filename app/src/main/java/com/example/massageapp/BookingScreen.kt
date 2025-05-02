package com.example.massageapp

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val today = LocalDate.now()
    val dateOptions = (0..29).map { today.plusDays(it.toLong()) }
    val timeOptions = generateTimeSlots()
    val services = listOf("Swedish Massage", "Deep Tissue", "Sports Massage", "Trigger Point Therapy", "Reflexology")

    var selectedDate by remember { mutableStateOf(today) }
    var selectedTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var selectedService by remember { mutableStateOf(services[0]) }

    var dateExpanded by remember { mutableStateOf(false) }
    var timeExpanded by remember { mutableStateOf(false) }
    var serviceExpanded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCVC by remember { mutableStateOf("") }

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("\uD83D\uDCC5 Select a Date", fontSize = 18.sp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { dateExpanded = true }
                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                .padding(16.dp)
        ) {
            Text(text = selectedDate.format(formatter))
            DropdownMenu(expanded = dateExpanded, onDismissRequest = { dateExpanded = false }) {
                dateOptions.forEach { date ->
                    DropdownMenuItem(onClick = {
                        selectedDate = date
                        dateExpanded = false
                    }) {
                        Text(date.format(formatter))
                    }
                }
            }
        }

        Text("⏰ Select a Time", fontSize = 18.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { timeExpanded = true }
                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                .padding(16.dp)
        ) {
            Text(text = selectedTime.format(timeFormatter))
            DropdownMenu(expanded = timeExpanded, onDismissRequest = { timeExpanded = false }) {
                timeOptions.forEach { time ->
                    DropdownMenuItem(onClick = {
                        selectedTime = time
                        timeExpanded = false
                    }) {
                        Text(time.format(timeFormatter))
                    }
                }
            }
        }

        Text("\uD83E\uDD86 Select a Service", fontSize = 18.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { serviceExpanded = true }
                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                .padding(16.dp)
        ) {
            Text(text = selectedService)
            DropdownMenu(expanded = serviceExpanded, onDismissRequest = { serviceExpanded = false }) {
                services.forEach { service ->
                    DropdownMenuItem(onClick = {
                        selectedService = service
                        serviceExpanded = false
                    }) {
                        Text(service)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("\uD83E\uDDD3 Your Info", fontSize = 18.sp)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && phone.isNotBlank()) {
                    showPaymentDialog = true
                } else {
                    Toast.makeText(context, "Please enter name and phone.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Booking")
        }

        if (showPaymentDialog) {
            AlertDialog(
                onDismissRequest = { showPaymentDialog = false },
                title = { Text("Payment Information") },
                text = {
                    Column {
                        OutlinedTextField(value = cardNumber, onValueChange = { cardNumber = it }, label = { Text("Card Number") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = cardExpiry, onValueChange = { cardExpiry = it }, label = { Text("Expiry Date (MM/YY)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = cardCVC, onValueChange = { cardCVC = it }, label = { Text("CVC") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val booking = Booking(
                            name = name,
                            phone = phone,
                            date = selectedDate.format(formatter),
                            time = selectedTime.format(timeFormatter),
                            service = selectedService,
                            cardNumber = cardNumber,
                            cardExpiry = cardExpiry,
                            cardCVC = cardCVC
                        )
                        FirestoreService.saveBooking(
                            booking,
                            onSuccess = {
                                uid?.let {
                                    db.collection("users").document(it).get().addOnSuccessListener { doc ->
                                        val oldPoints = doc.getLong("points") ?: 0L
                                        db.collection("users").document(it).update("points", oldPoints + 10)
                                    }
                                }
                                showPaymentDialog = false
                                showSuccessDialog = true
                            },
                            onFailure = {
                                Toast.makeText(context, "Booking failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }) {
                        Text("Submit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPaymentDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Booking Confirmed") },
                text = { Text("Your booking has been saved and points added.") },
                confirmButton = {
                    TextButton(onClick = { showSuccessDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateTimeSlots(): List<LocalTime> {
    val slots = mutableListOf<LocalTime>()
    var time = LocalTime.of(9, 0)
    val end = LocalTime.of(22, 0)
    while (time <= end) {
        slots.add(time)
        time = time.plusMinutes(15)
    }
    return slots
}
