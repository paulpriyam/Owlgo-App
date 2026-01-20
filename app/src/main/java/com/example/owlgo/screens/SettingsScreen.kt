package com.example.owlgo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.owlgo.viwmodel.AuthState
import com.example.owlgo.viwmodel.SessionViewModel

@Composable
fun SettingsScreen() {
    val sessionVm: SessionViewModel = hiltViewModel()
    val auth by sessionVm.state.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (auth is AuthState.SignedIn) {
            val u = (auth as AuthState.SignedIn).user
            ListItem(
                leadingContent = {
                    AsyncImage(
                        model = u.photoUrl,
                        contentDescription = "Profile"
                    )
                },
                headlineContent = { Text(u.displayName ?: "") },
                supportingContent = { if (u.email != null) Text(u.email) }
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { sessionVm.signOut() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Sign out", color = MaterialTheme.colorScheme.onError)
            }
        } else {
            Text("Not signed in", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
