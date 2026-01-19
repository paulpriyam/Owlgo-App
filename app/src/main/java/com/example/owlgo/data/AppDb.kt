package com.example.owlgo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.owlgo.dao.ProblemDao
import com.example.owlgo.model.Converters
import com.example.owlgo.model.ProblemEntity

@Database(entities = [ProblemEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb: RoomDatabase() {
    abstract fun problemDao(): ProblemDao
}