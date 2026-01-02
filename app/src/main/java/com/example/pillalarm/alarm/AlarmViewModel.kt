package com.example.pillalarm.alarm

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlarmViewModel(private val repo: AlarmRepository, private val appContext: Context) : ViewModel() {

    private val _alarms = MutableStateFlow<List<AlarmEntity>>(emptyList())
    val alarms: StateFlow<List<AlarmEntity>> = _alarms

    fun load(medicineId: String) {
        viewModelScope.launch {
            _alarms.value = repo.getAlarmsForMedicine(medicineId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun addAlarm(medicineId: String,
                 medicineName: String,
                 medicineImageUrl: String,
                 hour: Int,
                 minute: Int,
                 pm: Boolean,
                 days: List<String>) {
        viewModelScope.launch {
            val entity = AlarmEntity(
                medicineId = medicineId,
                medicineName = medicineName,
                medicineImageUrl = medicineImageUrl,
                hour = hour,
                minute = minute,
                pm = pm,
                days = days
            )
            repo.addAlarm(entity)
            // After insert, load again (to get generated id).
            _alarms.value = repo.getAlarmsForMedicine(medicineId)

            // This schedule all alarms for this medicine id.
            repo.getAlarmsForMedicine(medicineId).forEach { AlarmScheduler.schedule(appContext, it) }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repo.deleteAlarm(alarm)
            // To cancel scheduled alarm
            AlarmScheduler.cancel(appContext, alarm)
            // To reload list
            _alarms.value = repo.getAlarmsForMedicine(alarm.medicineId)
        }
    }
}

// AlarmViewModel Factory for providing dependencies
class AlarmViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = AlarmRepository(context)
        @Suppress("UNCHECKED_CAST")
        return AlarmViewModel(repo, context.applicationContext) as T
    }
}
