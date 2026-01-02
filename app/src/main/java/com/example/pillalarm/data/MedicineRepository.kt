package com.example.pillalarm.data

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pillalarm.auth.ImageFiles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

// Here we will uploads a medicine image to Firebase Storage, then saves a record in Firestore.
object MedicineRepository {

    @RequiresApi(Build.VERSION_CODES.S) // If imageFiles.reduceImageSize relies on API 31+ features
    fun upload(
        context: Context,
        imageUri: Uri,
        name: String,
        onSuccess: (medicineId: String) -> Unit
    ) {
        // Root storage reference
        val storage = FirebaseStorage.getInstance().reference

        // Using current time as a time-based filename; consider a UUID for stronger uniqueness
        val ref = storage.child("medicines/${System.currentTimeMillis()}.jpg")

        // Reduce/convert image to byte[] for upload; bail out if conversion failed
        val data = ImageFiles.reduceImageSize(context, imageUri) ?: return

        // Upload the bytes to Firebase Storage and get the download URL
        ref.putBytes(data).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { url ->
                saveRecord(name, url.toString(), onSuccess)
            }
        }
    }
    private fun saveRecord( // Saves a record in Firestore with medicine name and image URL
        name: String,
        url: String,
        onSuccess: (medicineId: String) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val record = mapOf(
            "name" to name,
            "imageUrl" to url,
            "timestamp" to System.currentTimeMillis(),
            "userId" to uid
        )

        db.collection("medicines").add(record)
            .addOnSuccessListener { doc ->
                onSuccess(doc.id) // return medicineId
            }
    }

    fun delete(id: String) { // Deletes a medicine record from Firestore
        FirebaseFirestore.getInstance()
            .collection("medicines")
            .document(id)
            .delete()
    }
}
