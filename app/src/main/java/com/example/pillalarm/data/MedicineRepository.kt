package com.example.pillalarm.data

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pillalarm.auth.ImageFiles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object MedicineRepository {

    @RequiresApi(Build.VERSION_CODES.S)
    fun upload(
        context: Context,
        imageUri: Uri,
        name: String,
        onSuccess: (medicineId: String) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance().reference
        val ref = storage.child("medicines/${System.currentTimeMillis()}.jpg")

        val data = ImageFiles.reduceImageSize(context, imageUri) ?: return

        ref.putBytes(data).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { url ->
                saveRecord(name, url.toString(), onSuccess)
            }
        }
    }

    private fun saveRecord(
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

    fun delete(id: String) {
        FirebaseFirestore.getInstance()
            .collection("medicines")
            .document(id)
            .delete()
    }
}
