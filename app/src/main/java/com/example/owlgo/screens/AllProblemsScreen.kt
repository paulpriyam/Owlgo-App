package com.example.owlgo.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.owlgo.viwmodel.ProblemsViewModel
import com.example.owlgo.viwmodel.SessionViewModel
import com.example.owlgo.viwmodel.AuthState
import com.example.owlgo.model.ProblemEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AllProblemsScreen(
    viewModel: ProblemsViewModel
) {
    val sessionVm: SessionViewModel = hiltViewModel()
    val auth by sessionVm.state.collectAsState()
    if (auth is AuthState.SignedOut) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sign in to see All", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        val items by viewModel.allProblems.collectAsState()
        val solved = items.filter { it.lastSolvedDate != null }
        LazyColumn(Modifier.fillMaxSize()) {
            items(solved) { p -> ProblemRow(p) }
        }
    }
}

@Composable
private fun ProblemRow(p: ProblemEntity) {
    val last = formatDate(p.lastSolvedDate)
    val next = formatDate(p.nextRevisionDate)
    ListItem(
        headlineContent = { Text(p.title) },
        supportingContent = {
            Text("${p.topics.joinToString()} • Last: $last • Next: $next", style = MaterialTheme.typography.bodySmall)
        },
        trailingContent = { Text(p.difficulty) },
        modifier = Modifier.clickable { }
    )
}

private fun formatDate(i: Instant?): String =
    i?.atZone(ZoneId.systemDefault())?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "—"
