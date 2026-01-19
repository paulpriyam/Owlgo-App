package com.example.owlgo.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.owlgo.model.ProblemEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface ProblemDao {
    @Query("SELECT * FROM problems WHERE nextRevisionDate <= :today ORDER BY nextRevisionDate")
    fun dueToday(today: Instant): Flow<List<ProblemEntity>>

    @Query("SELECT * FROM problems ORDER BY lastSolvedDate DESC")
    fun all(): Flow<List<ProblemEntity>>

    @Upsert
    suspend fun upsertAll(items: List<ProblemEntity>)

}