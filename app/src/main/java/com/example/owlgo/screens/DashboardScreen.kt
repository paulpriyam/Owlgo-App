package com.example.owlgo.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.owlgo.viwmodel.DashboardViewModel
import com.example.owlgo.viwmodel.SessionViewModel
import com.example.owlgo.viwmodel.AuthState
import com.fleeys.heatmap.model.Heat
import com.fleeys.heatmap.HeatMap
import com.fleeys.heatmap.style.HeatColor
import com.fleeys.heatmap.style.HeatMapStyle
import com.fleeys.heatmap.style.HeatStyle
import coil.compose.AsyncImage
import com.example.owlgo.model.ProblemEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.owlgo.R
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.time.ZoneId

@Composable
fun DashboardScreen(
    viewModel: com.example.owlgo.viwmodel.ProblemsViewModel
) {
    val sessionVm: SessionViewModel = hiltViewModel()
    val dashVm: DashboardViewModel = hiltViewModel()
    val auth by sessionVm.state.collectAsState()
    val heat by dashVm.heat.collectAsState()
    val all by viewModel.allProblems.collectAsState()
    val ctx = LocalContext.current
    var dialogOpen by remember { mutableStateOf(false) }
    var dialogSolved by remember { mutableStateOf<List<ProblemEntity>>(emptyList()) }
    var dialogDue by remember { mutableStateOf<List<ProblemEntity>>(emptyList()) }
    val isSignedIn = auth is AuthState.SignedIn
    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            viewModel.startSync()
        } else {
            viewModel.stopSync()
        }
    }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val token = account.idToken
                Log.d(
                    "Dashboard",
                    "GoogleSignIn result: email=${account.email} tokenLen=${token?.length ?: 0}"
                )
                if (token != null) {
                    sessionVm.onGoogleIdToken(token)
                } else {
                    Log.w("Dashboard", "GoogleSignIn token null")
                    sessionVm.signOut()
                }
            } catch (e: ApiException) {
                Log.e(
                    "Dashboard",
                    "GoogleSignIn ApiException code=${e.statusCode} message=${e.message}"
                )
                sessionVm.signOut()
            }
        }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (auth) {
            is AuthState.SignedOut -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            sessionVm.signIn()
                            val apiStatus = GoogleApiAvailability.getInstance()
                                .isGooglePlayServicesAvailable(ctx)
                            Log.d("Dashboard", "PlayServices status=$apiStatus")
                            if (apiStatus != ConnectionResult.SUCCESS) {
                                Log.e("Dashboard", "Google Play services not available: $apiStatus")
                            }
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .requestIdToken(ctx.getString(R.string.default_web_client_id))
                                    .build()
                            val clientId = ctx.getString(R.string.default_web_client_id)
                            Log.d("Dashboard", "ClientId len=${clientId.length}")
                            val client = GoogleSignIn.getClient(ctx, gso)
                            Log.d("Dashboard", "Launching Google sign-in intent")
                            launcher.launch(client.signInIntent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4285F4),
                            contentColor = Color.White
                        )
                    ) { Text("Sign in with Google") }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Sign in to see your progress",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            is AuthState.SigningIn -> {
                Text("Signing in...", style = MaterialTheme.typography.bodyMedium)
            }

            is AuthState.SignedIn -> {
                val u = (auth as AuthState.SignedIn).user
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = u.photoUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .height(40.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterVertically)
                    )
                    Column {
                        Text(u.displayName ?: "", style = MaterialTheme.typography.titleMedium)
                        if (u.email != null) Text(
                            u.email,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                androidx.compose.material3.Card {
                    Text(
                        "Last 180 days",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                val style = HeatMapStyle().copy(
                    heatStyle = HeatStyle().copy(
                        heatColor = HeatColor().copy(
                            activeLowestColor = Color(0xFFFFC107),
                            activeHighestColor = Color(0xFF4CAF50),
                            inactiveColor = Color.LightGray
                        )
                    )
                )
                HeatMap(data = heat, style = style, onHeatClick = { h: Heat<Unit> ->
                    val clicked = h.date
                    dialogSolved = all.filter { p ->
                        p.lastSolvedDate?.atZone(ZoneId.systemDefault())?.let {
                            val d = it.toLocalDate()
                            d.year == clicked.year && d.monthValue == clicked.monthNumber && d.dayOfMonth == clicked.dayOfMonth
                        } ?: false
                    }
                    dialogDue = all.filter { p ->
                        p.nextRevisionDate?.atZone(ZoneId.systemDefault())?.let {
                            val d = it.toLocalDate()
                            d.year == clicked.year && d.monthValue == clicked.monthNumber && d.dayOfMonth == clicked.dayOfMonth
                        } ?: false
                    }
                    dialogOpen = true
                })
                if (dialogOpen) {
                    AlertDialog(
                        onDismissRequest = { dialogOpen = false },
                        confirmButton = {
                            TextButton(onClick = { dialogOpen = false }) { Text("Close") }
                        },
                        title = { Text("Details") },
                        text = {
                            Column {
                                Text("Solved", style = MaterialTheme.typography.titleSmall)
                                if (dialogSolved.isEmpty()) {
                                    Text(
                                        "None",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    dialogSolved.forEach { p -> DayProblemRow(p) }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text("Due", style = MaterialTheme.typography.titleSmall)
                                if (dialogDue.isEmpty()) {
                                    Text(
                                        "None",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    dialogDue.forEach { p -> DayProblemRow(p) }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayProblemRow(p: ProblemEntity) {
    androidx.compose.material3.ListItem(
        headlineContent = { Text(p.title) },
        supportingContent = { Text(p.topics.joinToString(), style = MaterialTheme.typography.bodySmall) },
        trailingContent = { Text(p.difficulty) }
    )
}
