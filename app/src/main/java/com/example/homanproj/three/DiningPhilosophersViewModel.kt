package com.example.homanproj.three


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.random.Random


enum class PhilosopherState {
    THINKING,
    HUNGRY,
    EATING
}

data class Fork(val id: Int)

// Philosopher.kt
data class Philosopher(
    val id: Int,
    val name: String,
    var state: PhilosopherState = PhilosopherState.THINKING,
    var mealsEaten: Int = 0
)

@HiltViewModel
class DiningPhilosophersViewModel @Inject constructor() : ViewModel() {

    private val _philosophers = MutableStateFlow<List<Philosopher>>(emptyList())
    val philosophers: StateFlow<List<Philosopher>> = _philosophers.asStateFlow()

    private val _forks = MutableStateFlow<List<Fork>>(emptyList())
    val forks: StateFlow<List<Fork>> = _forks.asStateFlow()

    private val mutex = Mutex()
    private val philosopherJobs = mutableMapOf<Int, Job>()
    private val forkStates = mutableMapOf<Int, Boolean>() // true = available

    init {
        initializePhilosophers()
        initializeForks()
    }

    private fun initializePhilosophers() {
        val philosopherNames = listOf("Plato", "Aristotle", "Socrates", "Kant", "Nietzsche")
        _philosophers.value = philosopherNames.mapIndexed { index, name ->
            Philosopher(id = index, name = name)
        }
    }

    private fun initializeForks() {
        _forks.value = List(5) { Fork(it) }
        forkStates.clear()
        repeat(5) { forkStates[it] = true }
    }

    fun startDining() {
        _philosophers.value.forEach { philosopher ->
            startPhilosopher(philosopher.id)  // حالا ID فیلسوف رو پاس میدیم
        }
    }

    fun startPhilosopher(philosopherId: Int) {  // پارامتر رو به ID تغییر دادیم
        if (philosopherJobs[philosopherId]?.isActive == true) return

        philosopherJobs[philosopherId] = viewModelScope.launch {
            while (isActive) {
                think(philosopherId)
                if (tryToEat(philosopherId)) {
                    eat(philosopherId)
                    releaseForks(philosopherId)
                }
            }
        }
    }

    fun stopDining() {
        philosopherJobs.values.forEach { it.cancel() }
        philosopherJobs.clear()
        // Reset states
        _philosophers.update { philosophers ->
            philosophers.map { it.copy(state = PhilosopherState.THINKING) }
        }
        initializeForks()
    }




    private suspend fun think(philosopherId: Int) {
        updatePhilosopherState(philosopherId, PhilosopherState.THINKING)
        delay(Random.nextLong(1000, 3000))
    }

    private suspend fun tryToEat(philosopherId: Int): Boolean {
        updatePhilosopherState(philosopherId, PhilosopherState.HUNGRY)

        val leftFork = philosopherId
        val rightFork = (philosopherId + 1) % 5

        mutex.withLock {
            if (forkStates[leftFork] == true && forkStates[rightFork] == true) {
                forkStates[leftFork] = false
                forkStates[rightFork] = false
                return true
            }
        }
        return false
    }

    private suspend fun eat(philosopherId: Int) {
        updatePhilosopherState(philosopherId, PhilosopherState.EATING)
        _philosophers.update { philosophers ->
            philosophers.map {
                if (it.id == philosopherId) {
                    it.copy(mealsEaten = it.mealsEaten + 1)
                } else it
            }
        }
        delay(Random.nextLong(2000, 4000))
    }

    private suspend fun releaseForks(philosopherId: Int) {
        val leftFork = philosopherId
        val rightFork = (philosopherId + 1) % 5

        mutex.withLock {
            forkStates[leftFork] = true
            forkStates[rightFork] = true
        }
    }

    private fun updatePhilosopherState(philosopherId: Int, state: PhilosopherState) {
        _philosophers.update { philosophers ->
            philosophers.map {
                if (it.id == philosopherId) it.copy(state = state)
                else it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopDining()
    }
}
