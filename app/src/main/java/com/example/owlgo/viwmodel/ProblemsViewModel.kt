package com.example.owlgo.viwmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.owlgo.repository.ProblemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ProblemsViewModel @Inject constructor(
    private val repository: ProblemsRepository
): ViewModel() {

    private val today = MutableStateFlow(Instant.now())
    private fun startOfDay(i: Instant): Instant =
        i.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
    private fun endOfDay(i: Instant): Instant =
        startOfDay(i).plus(1, ChronoUnit.DAYS).minusMillis(1)

    val problemsToBeSolvedToday = today.flatMapLatest { repository.dueTodayFlow(endOfDay(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val problemsSolvedToday = today.flatMapLatest { repository.solvedTodayFlow(startOfDay(it)) }
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000), emptyList())

    val allProblems = repository.allFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startSync() = repository.startSync()
    fun stopSync() = repository.stopSync()
}
