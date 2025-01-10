package com.example.homanproj

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProducerConsumerViewModel @Inject constructor() : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _bufferSize = MutableStateFlow(0)
    val bufferSize: StateFlow<Int> = _bufferSize.asStateFlow()

    private val _producerSpeed = MutableStateFlow(1000L)
    val producerSpeed: StateFlow<Long> = _producerSpeed.asStateFlow()

    private val _consumerSpeed = MutableStateFlow(2000L)
    val consumerSpeed: StateFlow<Long> = _consumerSpeed.asStateFlow()

    private val _isProducing = MutableStateFlow(false)
    val isProducing: StateFlow<Boolean> = _isProducing.asStateFlow()

    private val _isConsuming = MutableStateFlow(false)
    val isConsuming: StateFlow<Boolean> = _isConsuming.asStateFlow()

    private val _stats = MutableStateFlow(Stats())
    val stats: StateFlow<Stats> = _stats.asStateFlow()

    private var producerJob: Job? = null
    private var consumerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val buffer = Channel<Item>(Channel.BUFFERED)
    private val bufferList = mutableListOf<Item>()

    data class Item(
        val id: Int,
        val timestamp: Long,
        val type: String
    )

    data class Stats(
        val totalProduced: Int = 0,
        val totalConsumed: Int = 0,
        val averageWaitTime: Long = 0
    )

    fun setProducerSpeed(speed: Long) {
        _producerSpeed.value = speed
    }

    fun setConsumerSpeed(speed: Long) {
        _consumerSpeed.value = speed
    }

    fun startProducing() {
        if (producerJob?.isActive == true) return
        _isProducing.value = true

        producerJob = scope.launch {
            var id = 0
            while (isActive) {
                val item = Item(
                    id = id++,
                    timestamp = System.currentTimeMillis(),
                    type = "Product"
                )
                buffer.send(item)
                bufferList.add(item)
                _bufferSize.value = bufferList.size
                _stats.update { current ->
                    current.copy(totalProduced = current.totalProduced + 1)
                }
                delay(_producerSpeed.value)
            }
        }
    }

    fun stopProducing() {
        producerJob?.cancel()
        _isProducing.value = false
    }

    fun startConsuming() {
        if (consumerJob?.isActive == true) return
        _isConsuming.value = true

        consumerJob = scope.launch {
            while (isActive) {
                val item = buffer.receive()
                val waitTime = System.currentTimeMillis() - item.timestamp
                bufferList.remove(item)
                _items.update { currentList ->
                    currentList + item
                }
                _bufferSize.value = bufferList.size
                _stats.update { current ->
                    current.copy(
                        totalConsumed = current.totalConsumed + 1,
                        averageWaitTime = (current.averageWaitTime + waitTime) / 2
                    )
                }
                delay(_consumerSpeed.value)
            }
        }
    }

    fun stopConsuming() {
        consumerJob?.cancel()
        _isConsuming.value = false
    }

    override fun onCleared() {
        super.onCleared()
        scope.launch {
            buffer.close()
        }
        producerJob?.cancel()
        consumerJob?.cancel()
    }
}