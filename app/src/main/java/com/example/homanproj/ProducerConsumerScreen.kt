package com.example.homanproj


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.animation.animateContentSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProducerConsumerScreen(
    viewModel: ProducerConsumerViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val bufferSize by viewModel.bufferSize.collectAsStateWithLifecycle()
    val producerSpeed by viewModel.producerSpeed.collectAsStateWithLifecycle()
    val consumerSpeed by viewModel.consumerSpeed.collectAsStateWithLifecycle()
    val isProducing by viewModel.isProducing.collectAsStateWithLifecycle()
    val isConsuming by viewModel.isConsuming.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 16.dp),
    ) {
        StatsCard(
            stats = stats,
            bufferSize = bufferSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Speed Controls
        SpeedControls(
            producerSpeed = producerSpeed,
            consumerSpeed = consumerSpeed,
            onProducerSpeedChange = { viewModel.setProducerSpeed(it) },
            onConsumerSpeedChange = { viewModel.setConsumerSpeed(it) }
        )

        // Control Buttons
        ControlButtons(
            isProducing = isProducing,
            isConsuming = isConsuming,
            onStartProducing = { viewModel.startProducing() },
            onStopProducing = { viewModel.stopProducing() },
            onStartConsuming = { viewModel.startConsuming() },
            onStopConsuming = { viewModel.stopConsuming() }
        )

        // Buffer Visualization
        BufferVisualization(
            bufferSize = bufferSize,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 16.dp)
        )

        // Items List
        AnimatedItemsList(items = items)
    }
}

@Composable
fun StatsCard(
    stats: ProducerConsumerViewModel.Stats,
    bufferSize: Int,
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
                text = "Statistics",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Produced: ${stats.totalProduced}")
            Text("Total Consumed: ${stats.totalConsumed}")
            Text("Average Wait Time: ${stats.averageWaitTime}ms")
            Text("Current Buffer Size: $bufferSize")
        }
    }
}

@Composable
fun SpeedControls(
    producerSpeed: Long,
    consumerSpeed: Long,
    onProducerSpeedChange: (Long) -> Unit,
    onConsumerSpeedChange: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Producer Speed (ms): $producerSpeed")
        Slider(
            value = producerSpeed.toFloat(),
            onValueChange = { onProducerSpeedChange(it.toLong()) },
            valueRange = 500f..3000f
        )

        Text("Consumer Speed (ms): $consumerSpeed")
        Slider(
            value = consumerSpeed.toFloat(),
            onValueChange = { onConsumerSpeedChange(it.toLong()) },
            valueRange = 500f..3000f
        )
    }
}

@Composable
fun ControlButtons(
    isProducing: Boolean,
    isConsuming: Boolean,
    onStartProducing: () -> Unit,
    onStopProducing: () -> Unit,
    onStartConsuming: () -> Unit,
    onStopConsuming: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = if (isProducing) onStopProducing else onStartProducing,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isProducing)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isProducing) "Stop Producer" else "Start Producer")
        }

        Button(
            onClick = if (isConsuming) onStopConsuming else onStartConsuming,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConsuming)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isConsuming) "Stop Consumer" else "Start Consumer")
        }
    }
}

@Composable
fun BufferVisualization(
    bufferSize: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            val transition = updateTransition(targetState = bufferSize, label = "buffer")
            val width by transition.animateFloat(
                label = "width",
                transitionSpec = { spring(stiffness = Spring.StiffnessLow) }
            ) { size ->
                (size.toFloat() / 20f).coerceIn(0f, 1f)
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(width)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            Text(
                text = "Buffer: $bufferSize",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedItemsList(items: List<ProducerConsumerViewModel.Item>) {
    LazyColumn {
        items(items = items, key = { it.id }) { item ->
            AnimatedVisibility(
                visible = true,
                enter = expandVertically() + fadeIn(),
                modifier = Modifier.animateItemPlacement()
            ) {
                ItemCard(item)
            }
        }
    }
}


@Composable
fun ItemCard(item: ProducerConsumerViewModel.Item) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Item #${item.id}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created: ${formatTimestamp(item.timestamp)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Type: ${item.type}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Utility function to format timestamp
private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(timestamp))
}