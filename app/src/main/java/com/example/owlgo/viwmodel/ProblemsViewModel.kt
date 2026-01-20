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
import javax.inject.Inject

@HiltViewModel
class ProblemsViewModel @Inject constructor(
    private val repository: ProblemsRepository
): ViewModel() {

    private val today = MutableStateFlow(Instant.now())
    val problemsToBeSolvedToday = today.flatMapLatest { repository.dueTodayFlow(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val problemsSolvedToday = repository.solvedTodayFlow(Instant.now())
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000), emptyList())

    val allProblems = repository.allFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
