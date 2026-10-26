package com.example.padelconnect.data.repository

import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.model.Participant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
interface MatchCreator {
    suspend fun createMatch(match: Match)
}
@Singleton
class MatchRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : MatchCreator{
    private val matchesCollection = firestore.collection("matches")

    // Real-time listener: emits updated match lists to all devices instantly
    val allMatches: Flow<List<Match>> = callbackFlow {
        val listener = matchesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val matches = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Match::class.java)?.copy(id = doc.id)
                }
                trySend(matches)
            }
        }
        awaitClose { listener.remove() }
    }

    // Creates a new match document on Firebase
    override suspend fun createMatch(match: Match) {
        matchesCollection.document(match.id).set(match).await()
    }

    // Joins a match transactionally to prevent race conditions
    suspend fun joinMatch(matchId: String, userId: String, userName: String): Boolean {
        return try {
            val docRef = matchesCollection.document(matchId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val match = snapshot.toObject(Match::class.java) ?: throw Exception("Match not found")

                // Validation checks
                if (match.isUserParticipant(userId) || match.isFull) {
                    return@runTransaction false
                }

                val updatedParticipants = match.participants + Participant(id = userId, name = userName)
                transaction.update(docRef, "participants", updatedParticipants)
                true
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteMatch(matchId: String): Boolean {
        return try {
            matchesCollection.document(matchId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun editMatch(match: Match): Boolean {
        return try {
            matchesCollection.document(match.id).set(match).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getMatch(matchId: String): Match? {
        return try {
            val snapshot = matchesCollection.document(matchId).get().await()
            snapshot.toObject(Match::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}