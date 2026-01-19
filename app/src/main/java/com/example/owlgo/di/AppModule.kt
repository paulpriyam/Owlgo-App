package com.example.owlgo.di

import android.content.Context
import androidx.room.Room
import com.example.owlgo.data.AppDb
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun auth(): FirebaseAuth = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun fireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun db(@ApplicationContext c: Context): AppDb = Room.databaseBuilder(c, AppDb::class.java, "owlgo.db").build()

    @Provides
    fun dao(db: AppDb) = db.problemDao()

}