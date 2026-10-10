package com.wassanborn72.concertalog.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationLogDao {
    @Query("SELECT * FROM medication_logs ORDER BY date DESC")
    fun observeAll(): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): MedicationLog?

    @Upsert
    suspend fun upsert(log: MedicationLog)

    @Upsert
    suspend fun upsertAll(logs: List<MedicationLog>)

    @Query("DELETE FROM medication_logs")
    suspend fun deleteAll()
}
