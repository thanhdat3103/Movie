package com.example.gamefirstscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val moviesPerPage = 4
    private var currentPage = 0
    var movieListResponse: List<Movies> by mutableStateOf(listOf())
        private set

    fun getAllMovies() {
        viewModelScope.launch {
            try {
                movieListResponse = ApiService.getInstance().getAllMovies()
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }
    }
}
