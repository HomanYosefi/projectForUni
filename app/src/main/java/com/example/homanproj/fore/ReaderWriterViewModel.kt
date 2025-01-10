package com.example.homanproj.fore


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
class ReaderWriterViewModel @Inject constructor() : ViewModel() {

    private val _readers = MutableStateFlow<List<Reader>>(emptyList())
    val readers: StateFlow<List<Reader>> = _readers.asStateFlow()

    private val _writers = MutableStateFlow<List<Writer>>(emptyList())
    val writers: StateFlow<List<Writer>> = _writers.asStateFlow()

    private val _sharedResource = MutableStateFlow<String>("")
    val sharedResource: StateFlow<String> = _sharedResource.asStateFlow()

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    private val readerWriterLock = ReaderWriterLock()
    private val readerJobs = mutableMapOf<Int, Job>()
    private val writerJobs = mutableMapOf<Int, Job>()

    data class Reader(
        val id: Int,
        var status: Status = Status.IDLE,
        var readCount: Int = 0
    )

    data class Writer(
        val id: Int,
        var status: Status = Status.IDLE,
        var writeCount: Int = 0
    )

    data class LogEntry(
        val timestamp: Long = System.currentTimeMillis(),
        val action: String,
        val actor: String,
        val resource: String
    )

    enum class Status {
        IDLE, WAITING, ACTIVE
    }

    init {
        initializeActors()
    }

    private fun initializeActors() {
        _readers.value = List(3) { Reader(it) }
        _writers.value = List(2) { Writer(it) }
    }

    fun startSimulation() {
        _readers.value.forEach { reader ->
            startReader(reader.id)
        }
        _writers.value.forEach { writer ->
            startWriter(writer.id)
        }
    }

    fun stopSimulation() {
        readerJobs.values.forEach { it.cancel() }
        writerJobs.values.forEach { it.cancel() }
        readerJobs.clear()
        writerJobs.clear()
        resetStatus()
    }

    private fun resetStatus() {
        _readers.update { readers ->
            readers.map { it.copy(status = Status.IDLE) }
        }
        _writers.update { writers ->
            writers.map { it.copy(status = Status.IDLE) }
        }
    }

    private fun startReader(readerId: Int) {
        if (readerJobs[readerId]?.isActive == true) return

        readerJobs[readerId] = viewModelScope.launch {
            while (isActive) {
                updateReaderStatus(readerId, Status.WAITING)
                readerWriterLock.acquireRead()

                try {
                    updateReaderStatus(readerId, Status.ACTIVE)
                    // Simulate reading
                    val currentResource = _sharedResource.value
                    addLog("Reading", "Reader $readerId", currentResource)
                    incrementReaderCount(readerId)
                    delay(Random.nextLong(1000, 2000))
                } finally {
                    updateReaderStatus(readerId, Status.IDLE)
                    readerWriterLock.releaseRead()
                }

                delay(Random.nextLong(500, 1500))
            }
        }
    }

    private fun startWriter(writerId: Int) {
        if (writerJobs[writerId]?.isActive == true) return

        writerJobs[writerId] = viewModelScope.launch {
            while (isActive) {
                updateWriterStatus(writerId, Status.WAITING)
                readerWriterLock.acquireWrite()

                try {
                    updateWriterStatus(writerId, Status.ACTIVE)
                    // Simulate writing
                    val newValue = "Data written by Writer $writerId at ${System.currentTimeMillis()}"
                    _sharedResource.value = newValue
                    addLog("Writing", "Writer $writerId", newValue)
                    incrementWriterCount(writerId)
                    delay(Random.nextLong(2000, 3000))
                } finally {
                    updateWriterStatus(writerId, Status.IDLE)
                    readerWriterLock.releaseWrite()
                }

                delay(Random.nextLong(1000, 2000))
            }
        }
    }

    private fun updateReaderStatus(readerId: Int, status: Status) {
        _readers.update { readers ->
            readers.map {
                if (it.id == readerId) it.copy(status = status)
                else it
            }
        }
    }

    private fun updateWriterStatus(writerId: Int, status: Status) {
        _writers.update { writers ->
            writers.map {
                if (it.id == writerId) it.copy(status = status)
                else it
            }
        }
    }

    private fun incrementReaderCount(readerId: Int) {
        _readers.update { readers ->
            readers.map {
                if (it.id == readerId) it.copy(readCount = it.readCount + 1)
                else it
            }
        }
    }

    private fun incrementWriterCount(writerId: Int) {
        _writers.update { writers ->
            writers.map {
                if (it.id == writerId) it.copy(writeCount = it.writeCount + 1)
                else it
            }
        }
    }

    private fun addLog(action: String, actor: String, resource: String) {
        _logs.update { currentLogs ->
            (currentLogs + LogEntry(action = action, actor = actor, resource = resource))
                .takeLast(100) // Keep only last 100 logs
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSimulation()
    }
}

class ReaderWriterLock {
    private val mutex = Mutex()
    private var activeReaders = 0
    private var activeWriter = false
    private val readerQueue = Channel<Unit>(Channel.UNLIMITED)
    private val writerQueue = Channel<Unit>(Channel.UNLIMITED)

    suspend fun acquireRead() {
        mutex.withLock {
            if (!activeWriter && writerQueue.isEmpty) {
                activeReaders++
                return
            }
            readerQueue.send(Unit)
        }
        readerQueue.receive()
        mutex.withLock {
            activeReaders++
        }
    }

    suspend fun releaseRead() {
        mutex.withLock {
            activeReaders--
            if (activeReaders == 0 && !writerQueue.isEmpty) {
                writerQueue.receive()
                activeWriter = true
            }
        }
    }

    suspend fun acquireWrite() {
        mutex.withLock {
            if (activeReaders == 0 && !activeWriter) {
                activeWriter = true
                return
            }
            writerQueue.send(Unit)
        }
        writerQueue.receive()
        activeWriter = true
    }

    suspend fun releaseWrite() {
        mutex.withLock {
            activeWriter = false
            if (!readerQueue.isEmpty) {
                while (!readerQueue.isEmpty) {
                    readerQueue.receive()
                    activeReaders++
                }
            } else if (!writerQueue.isEmpty) {
                writerQueue.receive()
                activeWriter = true
            }
        }
    }
}
