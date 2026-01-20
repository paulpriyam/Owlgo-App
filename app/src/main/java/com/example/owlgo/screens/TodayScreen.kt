package com.example.owlgo.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.owlgo.model.ProblemEntity
import com.example.owlgo.viwmodel.ProblemsViewModel
import com.example.owlgo.viwmodel.SessionViewModel
import com.example.owlgo.viwmodel.AuthState

@Composable
fun TodayScreen (
    viewModel: ProblemsViewModel
){
    val sessionVm: SessionViewModel = hiltViewModel()
    val auth by sessionVm.state.collectAsState()
    val due by viewModel.problemsToBeSolvedToday.collectAsState()
    val solved by viewModel.problemsSolvedToday.collectAsState()
    val ctx = LocalContext.current

    if (auth is AuthState.SignedOut) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sign in to see Today", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            item { SectionHeader("Solved Today") }
            item { Divider() }
            if (solved.isEmpty()) {
                item { Placeholder("No problems solved today") }
            } else {
                items(solved) { p -> ProblemRow(p) { open(ctx, it) } }
            }
            item { SectionHeader("Due Today") }
            item { Divider() }
            if (due.isEmpty()) {
                item { Placeholder("No problems due today") }
            } else {
                items(due) { p -> ProblemRow(p) { open(ctx, it) } }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
}

@Composable
private fun Placeholder(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProblemRow(p: ProblemEntity, onOpen: (ProblemEntity) -> Unit) {
    ListItem(
        headlineContent = { Text(p.title) },
        supportingContent = { Text(p.topics.joinToString()) },
        trailingContent = { Text(p.difficulty) },
        modifier = Modifier.clickable { onOpen(p) }
    )
}

private fun open(ctx: android.content.Context, p: ProblemEntity) {
    val uri = Uri.parse("https://leetcode.com/problems/${p.slug}/")
    ctx.startActivity(Intent(Intent.ACTION_VIEW, uri))

}
