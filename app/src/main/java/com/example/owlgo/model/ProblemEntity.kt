package com.example.owlgo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "problems")
data class ProblemEntity(
    @PrimaryKey val slug: String,
    val title: String,
    val difficulty: String,
    val topics: List<String>,
    val firstSolvedDate: Instant?,
    val lastSolvedDate: Instant?,
    val nextRevisionDate: Instant?,
    val solveCount: Int,
    val syncedAt: Instant?
)
