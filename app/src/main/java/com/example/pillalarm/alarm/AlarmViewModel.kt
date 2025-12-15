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
                 hour: Int,
                 minute: Int,
                 pm: Boolean,
                 days: List<String>) {
        viewModelScope.launch {
            val entity = AlarmEntity(
                medicineId = medicineId,
                hour = hour,
                minute = minute,
                pm = pm,
                days = days
            )
            repo.addAlarm(entity)
            // After insert, load again (to get generated id); simple approach: reload all
            _alarms.value = repo.getAlarmsForMedicine(medicineId)

            // schedule newly inserted alarm(s). Need id — Room returns rowId, but entity id isn't updated;
            // so reload to find new alarms and schedule them. Simpler: schedule all alarms for this medicine.
            repo.getAlarmsForMedicine(medicineId).forEach { AlarmScheduler.schedule(appContext, it) }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repo.deleteAlarm(alarm)
            // cancel scheduled alarm
            AlarmScheduler.cancel(appContext, alarm)
            // reload list
            _alarms.value = repo.getAlarmsForMedicine(alarm.medicineId)
        }
    }
}

// AlarmViewModelFactory for providing dependencies
class AlarmViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = AlarmRepository(context)
        @Suppress("UNCHECKED_CAST")
        return AlarmViewModel(repo, context.applicationContext) as T
    }
}
