package com.example.owlgo.viwmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface AuthState {
    data object SignedOut : AuthState
    data object SigningIn : AuthState
    data class SignedIn(val user: UserInfo) : AuthState
}

data class UserInfo(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(value = AuthState.SignedOut)
    val state: StateFlow<AuthState> = _state

    init {
        Log.d("SessionVM", "Init: attaching FirebaseAuth listener")
        auth.addAuthStateListener {
            val u = it.currentUser
            Log.d("SessionVM", "AuthStateListener: user=${u?.uid ?: "null"} name=${u?.displayName ?: "null"}")
            _state.value = if (u == null) {
                AuthState.SignedOut
            } else {
                AuthState.SignedIn(
                    UserInfo(
                        uid = u.uid,
                        displayName = u.displayName,
                        email = u.email,
                        photoUrl = u.photoUrl?.toString()
                    )
                )
            }
        }
    }

    fun signIn() {
        Log.d("SessionVM", "signIn: set SigningIn")
        _state.value = AuthState.SigningIn
    }

    fun onGoogleIdToken(idToken: String) {
        Log.d("SessionVM", "onGoogleIdToken: token len=${idToken.length}")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            val u = auth.currentUser
            Log.d("SessionVM", "onComplete: success=${task.isSuccessful} user=${u?.uid ?: "null"}")
            _state.value = if (task.isSuccessful && u != null) {
                AuthState.SignedIn(
                    UserInfo(
                        uid = u.uid,
                        displayName = u.displayName,
                        email = u.email,
                        photoUrl = u.photoUrl?.toString()
                    )
                )
            } else {
                Log.w("SessionVM", "signInWithCredential failed or user null; setting SignedOut")
                AuthState.SignedOut
            }
        }
            .addOnFailureListener {
                Log.e("SessionVM", "signInWithCredential failure", it)
            }
    }

    fun signOut() {
        Log.d("SessionVM", "signOut: FirebaseAuth.signOut")
        auth.signOut()
        _state.value = AuthState.SignedOut
    }
}
