package com.example.massageapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val services = listOf(
        Service("Trigger Point Therapy", R.drawable.trigger_point, "Relieves tight muscle areas causing pain elsewhere. Uses cycles of isolated pressure and release."),
        Service("Swedish Massage", R.drawable.swedish_massage, "Light to medium pressure. Promotes circulation, helps with stress and chronic pain."),
        Service("Deep Tissue Massage", R.drawable.deep_tissue, "Releases muscle tension and loosens scar tissue. Great for chronic or overuse injuries."),
        Service("Sports Massage", R.drawable.sports_massage, "Improves flexibility and endurance. Speeds up post-workout recovery."),
        Service("Reflexology", R.drawable.reflexology, "Restorative. Relieves stress, anxiety, and foot/ankle pain.")
    )

    var selectedService by remember { mutableStateOf<Service?>(null) }
    val context = LocalContext.current

    Scaffold{ paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Welcome to Massage Spa", fontSize = 26.sp, modifier = Modifier.padding(8.dp))

            services.forEach { service ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { selectedService = service },
                    elevation = 4.dp
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(service.imageRes),
                            contentDescription = service.name,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(service.name, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("\uD83D\uDCCD Shop Info", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text("Hours: 9:00 AM – 4:00 PM")
            Text("Address: 123 Main Street, City, State")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=One+Pace+Plaza")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open in Google Maps")
            }
        }
    }

    selectedService?.let { service ->
        AlertDialog(
            onDismissRequest = { selectedService = null },
            title = { Text(service.name) },
            text = { Text(service.description) },
            confirmButton = {
                TextButton(onClick = { selectedService = null }) {
                    Text("Close")
                }
            }
        )
    }
}

data class Service(
    val name: String,
    val imageRes: Int,
    val description: String
)
