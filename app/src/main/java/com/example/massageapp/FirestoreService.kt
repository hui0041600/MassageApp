package com.example.massageapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private val bookingsRef = db.collection("bookings")

    // save and check time
    fun saveBooking(booking: Booking, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }
        // check the time
        bookingsRef
            .whereEqualTo("date", booking.date)
            .whereEqualTo("time", booking.time)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    onFailure(Exception("This time slot is already booked. Please choose another."))
                } else {
                    val bookingMap = hashMapOf(
                        "name" to booking.name,
                        "phone" to booking.phone,
                        "date" to booking.date,
                        "time" to booking.time,
                        "service" to booking.service,
                        "cardNumber" to booking.cardNumber,
                        "cardExpiry" to booking.cardExpiry,
                        "cardCVC" to booking.cardCVC,
                        "uid" to uid
                    )

                    bookingsRef.add(bookingMap)
                        .addOnSuccessListener {
                            saveCardInfo(uid, booking.cardNumber, booking.cardExpiry, booking.cardCVC)
                            addPoints(uid, 10)
                            onSuccess()
                        }
                        .addOnFailureListener { e -> onFailure(e) }
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // cancel take point back
    fun cancelBooking(booking: Booking) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        bookingsRef
            .whereEqualTo("name", booking.name)
            .whereEqualTo("phone", booking.phone)
            .whereEqualTo("date", booking.date)
            .whereEqualTo("time", booking.time)
            .whereEqualTo("service", booking.service)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    bookingsRef.document(document.id).delete()
                }

                // cancel then give the point back
                val userRef = db.collection("users").document(uid)
                userRef.get().addOnSuccessListener { doc ->
                    val currentPoints = doc.getLong("points")?.toInt() ?: 0
                    val newPoints = (currentPoints - 10).coerceAtLeast(0)
                    userRef.update("points", newPoints)
                }
            }
    }

    //  listen the appointment base UID
    fun listenToBookingsForUser(uid: String, onDataChanged: (List<Booking>) -> Unit): ListenerRegistration {
        return bookingsRef
            .whereEqualTo("uid", uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val bookings = snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
                    onDataChanged(bookings)
                }
            }
    }

    //  listen by admin
    fun listenToAllBookings(onDataChanged: (List<Booking>) -> Unit): ListenerRegistration {
        return bookingsRef
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val bookings = snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
                    onDataChanged(bookings)
                }
            }
    }

    fun listenToBookings(onDataChanged: (List<Booking>) -> Unit): ListenerRegistration {
        return listenToAllBookings(onDataChanged)
    }

    // Save Card info
    fun saveCardInfo(uid: String, cardNumber: String, cardExpiry: String, cardCVC: String) {
        val cardMap = hashMapOf(
            "cardNumber" to cardNumber,
            "cardExpiry" to cardExpiry,
            "cardCVC" to cardCVC
        )
        db.collection("users").document(uid)
            .collection("paymentInfo")
            .document("defaultCard")
            .set(cardMap)
    }

    // Give points
    fun addPoints(uid: String, pointsToAdd: Int) {
        val userRef = db.collection("users").document(uid)
        userRef.get().addOnSuccessListener { doc ->
            val currentPoints = doc.getLong("points")?.toInt() ?: 0
            userRef.update("points", currentPoints + pointsToAdd)
        }
    }
}