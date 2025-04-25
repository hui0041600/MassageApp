package com.example.massageapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MapsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Find Us on the Map",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

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