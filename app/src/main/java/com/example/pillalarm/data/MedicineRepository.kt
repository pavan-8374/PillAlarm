package com.example.pillalarm.data

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pillalarm.alarm.AlarmScheduler
import com.example.pillalarm.auth.ImageFiles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object MedicineRepository {

    @RequiresApi(Build.VERSION_CODES.S)
    fun upload(context: Context,
               imageUri: Uri,
               name: String,
               alarmTime: Long) {
        val storage = FirebaseStorage.getInstance().reference
        val ref = storage.child("medicines/${System.currentTimeMillis()}.jpg")

        val data = ImageFiles.reduceImageSize(context, imageUri) ?: return

        ref.putBytes(data).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { url ->
                saveRecord(name, url.toString(), alarmTime) {
                    AlarmScheduler.schedule(context, name, alarmTime)
                }
            }
        }
    }

    private fun saveRecord(name: String,
                           url: String,
                           alarm: Long,
                           onSuccess: () -> Unit
    ){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val record = mapOf(
            "name" to name,
            "imageUrl" to url,
            "alarmTime" to alarm,
            "timestamp" to System.currentTimeMillis(),
            "userId" to uid
        )

        db.collection("medicines").add(record)
            .addOnSuccessListener { onSuccess() }
    }
    fun updateAlarm(id: String, newAlarmTime: Long) {
        FirebaseFirestore.getInstance()
            .collection("medicines")
            .document(id)
            .update("alarmTime", newAlarmTime)
    }

    fun delete(id: String) {
        FirebaseFirestore.getInstance()
            .collection("medicines")
            .document(id)
            .delete()
    }
}
