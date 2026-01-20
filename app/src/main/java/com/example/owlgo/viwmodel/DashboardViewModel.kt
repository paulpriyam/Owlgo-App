package com.example.owlgo.viwmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.owlgo.repository.ProblemsRepository
import com.fleeys.heatmap.model.Heat

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import java.time.ZoneId
import java.time.LocalDate as JLocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repository: ProblemsRepository
) : ViewModel() {

    private val today: LocalDate = JLocalDate.now().let { LocalDate(it.year, it.monthValue, it.dayOfMonth) }
    private val start: LocalDate = today.plus(DatePeriod(days = -180))

    val heat = repository.allFlow().map { problems ->
        val counts = problems.mapNotNull { p ->
            val d = p.lastSolvedDate?.atZone(ZoneId.systemDefault())?.toLocalDate()
            d?.let { LocalDate(it.year, it.monthValue, it.dayOfMonth) }
        }.groupingBy { it }.eachCount()

        generateSequence(start) { d ->
            if (d < today) d.plus(DatePeriod(days = 1)) else null
        }.map { d ->
            Heat<Unit>(date = d, value = (counts[d] ?: 0).toDouble())
        }.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
