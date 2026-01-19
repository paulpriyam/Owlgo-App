package com.example.owlgo.firebase

import com.example.owlgo.model.ProblemEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val fs: FirebaseFirestore
) {
    fun col(): CollectionReference =
        fs.collection("users").document(requireNotNull(auth.currentUser?.uid))
            .collection("problems")

    fun listenAll(cb: (List<ProblemEntity>) -> Unit): ListenerRegistration =
        col().addSnapshotListener { s, _ -> cb(s?.documents?.mapNotNull { it.toProblem() } ?: emptyList()) }

    suspend fun fetchDue(todayIso: String): List<ProblemEntity> =
        col().whereLessThanOrEqualTo("nextRevisionDate", todayIso)
            .orderBy("nextRevisionDate").get().await().documents.mapNotNull { it.toProblem() }
}

fun DocumentSnapshot.toProblem(): ProblemEntity? = ProblemEntity(
    slug = getString("slug") ?: id,
    title = getString("title") ?: id,
    difficulty = getString("difficulty") ?: "Unknown",
    topics = (get("topics") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
    firstSolvedDate = getString("firstSolvedDate")?.let(Instant::parse),
    lastSolvedDate = getString("lastSolvedDate")?.let(Instant::parse),
    nextRevisionDate = getString("nextRevisionDate")?.let(Instant::parse),
    solveCount = (getLong("solveCount") ?: 0L).toInt(),
    syncedAt = getString("syncedAt")?.let(Instant::parse)
)