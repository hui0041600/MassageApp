package com.example.massageapp

data class Booking(
    val name: String = "",
    val phone: String = "",
    val date: String = "",
    val time: String = "",
    val service: String = "",
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val cardCVC: String = ""
)