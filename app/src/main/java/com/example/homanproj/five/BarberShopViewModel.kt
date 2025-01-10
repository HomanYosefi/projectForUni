package com.example.homanproj.five


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
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

@HiltViewModel
class BarberShopViewModel @Inject constructor() : ViewModel() {

    private val _barber = MutableStateFlow(Barber())
    val barber: StateFlow<Barber> = _barber.asStateFlow()

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _waitingRoom = MutableStateFlow<List<Customer>>(emptyList())
    val waitingRoom: StateFlow<List<Customer>> = _waitingRoom.asStateFlow()

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    private val mutex = Mutex()
    private val customerQueue = Channel<Customer>(Channel.UNLIMITED)
    private var barberJob: Job? = null
    private var customerGeneratorJob: Job? = null
    private val maxWaitingRoomSize = 5

    data class Barber(
        val status: Status = Status.SLEEPING,
        val currentCustomer: Customer? = null,
        val totalHaircuts: Int = 0
    )

    data class Customer(
        val id: Int,
        var status: Status = Status.WAITING
    )

    data class LogEntry(
        val timestamp: Long = System.currentTimeMillis(),
        val message: String,
        val type: LogType
    )

    enum class Status {
        SLEEPING, WAITING, CUTTING, DONE
    }

    enum class LogType {
        INFO, SUCCESS, ERROR
    }

    fun startSimulation() {
        startBarber()
        startCustomerGenerator()
    }

    fun stopSimulation() {
        barberJob?.cancel()
        customerGeneratorJob?.cancel()
        resetState()
    }

    private fun resetState() {
        _barber.value = Barber()
        _customers.value = emptyList()
        _waitingRoom.value = emptyList()
        viewModelScope.launch {
            customerQueue.receive() // Clear queue
        }
    }

    private fun startBarber() {
        barberJob = viewModelScope.launch {
            while (isActive) {
                // Barber checks for customers
                if (customerQueue.isEmpty) {
                    updateBarberStatus(Status.SLEEPING)
                    addLog("Barber is sleeping", LogType.INFO)
                }

                // Wait for customer
                val customer = customerQueue.receive()
                updateBarberStatus(Status.CUTTING, customer)
                addLog("Barber is cutting hair for Customer ${customer.id}", LogType.SUCCESS)

                // Simulate haircut
                delay(Random.nextLong(2000, 4000))

                // Finish haircut
                completeHaircut(customer)
            }
        }
    }

    private fun startCustomerGenerator() {
        var customerId = 0
        customerGeneratorJob = viewModelScope.launch {
            while (isActive) {
                delay(Random.nextLong(1000, 3000))
                val customer = Customer(++customerId)
                addCustomer(customer)
            }
        }
    }

    private suspend fun addCustomer(customer: Customer) {
        mutex.withLock {
            if (_waitingRoom.value.size < maxWaitingRoomSize) {
                _waitingRoom.update { it + customer }
                _customers.update { it + customer }
                customerQueue.send(customer)
                addLog("Customer ${customer.id} entered waiting room", LogType.INFO)
            } else {
                addLog("Customer ${customer.id} left - waiting room full", LogType.ERROR)
            }
        }
    }

    private fun updateBarberStatus(status: Status, customer: Customer? = null) {
        _barber.update {
            it.copy(
                status = status,
                currentCustomer = customer,
                totalHaircuts = if (status == Status.CUTTING) it.totalHaircuts + 1 else it.totalHaircuts
            )
        }
    }

    private fun completeHaircut(customer: Customer) {
        _waitingRoom.update { it - customer }
        _customers.update { customers ->
            customers.map {
                if (it.id == customer.id) it.copy(status = Status.DONE)
                else it
            }
        }
        updateBarberStatus(Status.SLEEPING)
        addLog("Completed haircut for Customer ${customer.id}", LogType.SUCCESS)
    }

    private fun addLog(message: String, type: LogType) {
        _logs.update { currentLogs ->
            (currentLogs + LogEntry(message = message, type = type))
                .takeLast(100)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSimulation()
    }
}
