package com.example.homanproj.too


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MessageProducerConsumerViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _producerStatus = MutableStateFlow<ProducerStatus>(ProducerStatus.Idle)
    val producerStatus: StateFlow<ProducerStatus> = _producerStatus.asStateFlow()

    private val _consumerStatus = MutableStateFlow<ConsumerStatus>(ConsumerStatus.Idle)
    val consumerStatus: StateFlow<ConsumerStatus> = _consumerStatus.asStateFlow()

    private val messageQueue = MutableSharedFlow<Message>(replay = 0)
    private var producerJob: Job? = null
    private var consumerJob: Job? = null

    data class Message(
        val id: Int,
        val content: String,
        val timestamp: Long = System.currentTimeMillis(),
        val status: MessageStatus = MessageStatus.Created
    )

    sealed class MessageStatus {
        data object Created : MessageStatus()
        data object Sent : MessageStatus()
        data object Received : MessageStatus()
        data object Processed : MessageStatus()
    }

    sealed class ProducerStatus {
        data object Idle : ProducerStatus()
        data object Producing : ProducerStatus()
        data class Error(val message: String) : ProducerStatus()
    }

    sealed class ConsumerStatus {
        data object Idle : ConsumerStatus()
        data object Consuming : ConsumerStatus()
        data class Error(val message: String) : ConsumerStatus()
    }

    fun startProducer() {
        if (producerJob?.isActive == true) return

        producerJob = viewModelScope.launch {
            _producerStatus.value = ProducerStatus.Producing
            var messageId = 0

            try {
                while (isActive) {
                    val message = Message(
                        id = messageId++,
                        content = "Message #$messageId",
                        status = MessageStatus.Created
                    )

                    messageQueue.emit(message.copy(status = MessageStatus.Sent))
                    _messages.update { currentList ->
                        currentList + message
                    }

                    delay(1000) // Simulate processing time
                }
            } catch (e: Exception) {
                _producerStatus.value = ProducerStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun startConsumer() {
        if (consumerJob?.isActive == true) return

        consumerJob = viewModelScope.launch {
            _consumerStatus.value = ConsumerStatus.Consuming

            try {
                messageQueue.collect { message ->
                    delay(1500) // Simulate processing time

                    _messages.update { currentList ->
                        currentList.map {
                            if (it.id == message.id) {
                                it.copy(status = MessageStatus.Processed)
                            } else {
                                it
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _consumerStatus.value = ConsumerStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun stopProducer() {
        producerJob?.cancel()
        _producerStatus.value = ProducerStatus.Idle
    }

    fun stopConsumer() {
        consumerJob?.cancel()
        _consumerStatus.value = ConsumerStatus.Idle
    }

    override fun onCleared() {
        super.onCleared()
        stopProducer()
        stopConsumer()
    }
}