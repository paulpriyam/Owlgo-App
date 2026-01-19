package com.example.owlgo.viwmodel

import androidx.lifecycle.ViewModel
import com.example.owlgo.repository.ProblemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProblemsViewModel @Inject constructor(
    private val repository: ProblemsRepository
): ViewModel() {
}