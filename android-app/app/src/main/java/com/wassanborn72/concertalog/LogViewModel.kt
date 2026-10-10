package com.wassanborn72.concertalog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wassanborn72.concertalog.data.AppDatabase
import com.wassanborn72.concertalog.data.MedicationLog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LogViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.get(application).medicationLogDao()

    val logs = dao.observeAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun save(log: MedicationLog) {
        viewModelScope.launch { dao.upsert(log) }
    }

    fun import(logs: List<MedicationLog>) {
        viewModelScope.launch { dao.upsertAll(logs) }
    }

    fun deleteAll() {
        viewModelScope.launch { dao.deleteAll() }
    }
}
