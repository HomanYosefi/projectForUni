package com.example.homanproj.fore


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReaderWriterScreen(
    viewModel: ReaderWriterViewModel = hiltViewModel()
) {
    val readers by viewModel.readers.collectAsStateWithLifecycle()
    val writers by viewModel.writers.collectAsStateWithLifecycle()
    val sharedResource by viewModel.sharedResource.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 16.dp),
    ) {
        // Control Panel
        ControlPanel(
            onStart = viewModel::startSimulation,
            onStop = viewModel::stopSimulation,
            modifier = Modifier.fillMaxWidth()
        )

        // Shared Resource Display
        SharedResourceCard(
            resource = sharedResource,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Actors Status
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Readers Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Readers",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ReadersList(readers = readers)
            }

            // Writers Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Writers",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                WritersList(writers = writers)
            }
        }

        // Logs
        LogsList(
            logs = logs,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun ControlPanel(
    onStart: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onStart) {
            Text("Start Simulation")
        }
        Button(
            onClick = onStop,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Stop Simulation")
        }
    }
}

@Composable
fun SharedResourceCard(
    resource: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Shared Resource",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resource.ifEmpty { "No data written yet" },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ReadersList(readers: List<ReaderWriterViewModel.Reader>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(readers) { reader ->
            ReaderCard(reader)
        }
    }
}

@Composable
fun WritersList(writers: List<ReaderWriterViewModel.Writer>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(writers) { writer ->
            WriterCard(writer)
        }
    }
}

@Composable
fun ReaderCard(reader: ReaderWriterViewModel.Reader) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (reader.status) {
                ReaderWriterViewModel.Status.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                ReaderWriterViewModel.Status.WAITING -> MaterialTheme.colorScheme.secondaryContainer
                ReaderWriterViewModel.Status.IDLE -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Reader ${reader.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Status: ${reader.status}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Read Count: ${reader.readCount}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun WriterCard(writer: ReaderWriterViewModel.Writer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (writer.status) {
                ReaderWriterViewModel.Status.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                ReaderWriterViewModel.Status.WAITING -> MaterialTheme.colorScheme.secondaryContainer
                ReaderWriterViewModel.Status.IDLE -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Writer ${writer.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Status: ${writer.status}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Write Count: ${writer.writeCount}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun LogsList(
    logs: List<ReaderWriterViewModel.LogEntry>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Activity Logs",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                reverseLayout = true
            ) {
                items(logs) { log ->
                    LogEntry(log)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun LogEntry(log: ReaderWriterViewModel.LogEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${log.action} by ${log.actor}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .format(Date(log.timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}