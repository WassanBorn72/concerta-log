package com.wassanborn72.concertalog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_logs")
data class MedicationLog(
    @PrimaryKey val date: String,
    val recordTime: String = "",
    val dose: String = "",
    val doseOther: String = "",
    val medTime: String = "",
    val sleep: Double? = null,
    val sleepQuality: Int? = null,
    val otherMeds: Int? = null,
    val startEase: Int? = null,
    val startEffort: Int? = null,
    val progress: Int? = null,
    val motivation: Int? = null,
    val calm: Int? = null,
    val fog: Int? = null,
    val focus: Int? = null,
    val anxiety: Int? = null,
    val irritability: Int? = null,
    val appetite: Int? = null,
    val physical: Int? = null,
    val wearoff: Int? = null,
    val latency: String = "",
    val food: Int? = null,
    val overall: Int? = null,
    val workOutput: Int? = null,
    val work: String = "",
    val notes: String = "",
    val doctorNote: String = ""
)
