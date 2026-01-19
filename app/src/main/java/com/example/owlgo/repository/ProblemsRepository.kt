package com.example.owlgo.repository

import com.example.owlgo.dao.ProblemDao
import com.example.owlgo.firebase.FirestoreDataSource
import com.example.owlgo.model.ProblemEntity
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.Period
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProblemsRepository @Inject constructor(
    private val dao: ProblemDao,
    private val remote: FirestoreDataSource
) {
    private var reg: ListenerRegistration? = null

    fun startSync() {
        reg?.remove()
        reg = remote.listenAll { items ->
            CoroutineScope(Dispatchers.IO).launch { dao.upsertAll(items) }
        }
    }

    fun dueTodayFlow(today: Instant): Flow<List<ProblemEntity>> = dao.dueToday(today)
    fun allFlow(): Flow<List<ProblemEntity>> = dao.all()

    suspend fun markSolvedAgain(slug: String) {
        val now = Instant.now()
        remote.col().document(slug).update(
            mapOf("lastSolvedDate" to now.toString(), "nextRevisionDate" to now.plus(Period.ofDays(15)).toString())
        ).await()
    }
}