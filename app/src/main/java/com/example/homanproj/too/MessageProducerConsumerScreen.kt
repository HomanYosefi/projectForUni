package com.example.homanproj.too


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun MessageProducerConsumerScreen(
    viewModel: MessageProducerConsumerViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val producerStatus by viewModel.producerStatus.collectAsStateWithLifecycle()
    val consumerStatus by viewModel.consumerStatus.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 16.dp),
    ) {
        // Status Cards
        StatusSection(
            producerStatus = producerStatus,
            consumerStatus = consumerStatus,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Control Buttons
        ControlSection(
            producerStatus = producerStatus,
            consumerStatus = consumerStatus,
            onStartProducer = viewModel::startProducer,
            onStopProducer = viewModel::stopProducer,
            onStartConsumer = viewModel::startConsumer,
            onStopConsumer = viewModel::stopConsumer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Messages List
        MessagesList(
            messages = messages,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatusSection(
    producerStatus: MessageProducerConsumerViewModel.ProducerStatus,
    consumerStatus: MessageProducerConsumerViewModel.ConsumerStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatusCard(
            title = "Producer",
            status = when (producerStatus) {
                is MessageProducerConsumerViewModel.ProducerStatus.Idle -> "Idle"
                is MessageProducerConsumerViewModel.ProducerStatus.Producing -> "Producing"
                is MessageProducerConsumerViewModel.ProducerStatus.Error -> "Error: ${producerStatus.message}"
            },
            isActive = producerStatus is MessageProducerConsumerViewModel.ProducerStatus.Producing,
            modifier = Modifier.weight(1f)
        )

        StatusCard(
            title = "Consumer",
            status = when (consumerStatus) {
                is MessageProducerConsumerViewModel.ConsumerStatus.Idle -> "Idle"
                is MessageProducerConsumerViewModel.ConsumerStatus.Consuming -> "Consuming"
                is MessageProducerConsumerViewModel.ConsumerStatus.Error -> "Error: ${consumerStatus.message}"
            },
            isActive = consumerStatus is MessageProducerConsumerViewModel.ConsumerStatus.Consuming,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatusCard(
    title: String,
    status: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ControlSection(
    producerStatus: MessageProducerConsumerViewModel.ProducerStatus,
    consumerStatus: MessageProducerConsumerViewModel.ConsumerStatus,
    onStartProducer: () -> Unit,
    onStopProducer: () -> Unit,
    onStartConsumer: () -> Unit,
    onStopConsumer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = if (producerStatus is MessageProducerConsumerViewModel.ProducerStatus.Producing)
                onStopProducer else onStartProducer,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (producerStatus is MessageProducerConsumerViewModel.ProducerStatus.Producing)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                if (producerStatus is MessageProducerConsumerViewModel.ProducerStatus.Producing)
                    "Stop Producer"
                else
                    "Start Producer"
            )
        }

        Button(
            onClick = if (consumerStatus is MessageProducerConsumerViewModel.ConsumerStatus.Consuming)
                onStopConsumer else onStartConsumer,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (consumerStatus is MessageProducerConsumerViewModel.ConsumerStatus.Consuming)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                if (consumerStatus is MessageProducerConsumerViewModel.ConsumerStatus.Consuming)
                    "Stop Consumer"
                else
                    "Start Consumer"
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessagesList(
    messages: List<MessageProducerConsumerViewModel.Message>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = messages,
            key = { it.id }
        ) { message ->
            MessageItem(
                message = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .animateItemPlacement()
            )
        }
    }
}

@Composable
fun MessageItem(
    message: MessageProducerConsumerViewModel.Message,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
                    text = message.content,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${message.status::class.simpleName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (message.status) {
                        is MessageProducerConsumerViewModel.MessageStatus.Created -> MaterialTheme.colorScheme.primary
                        is MessageProducerConsumerViewModel.MessageStatus.Sent -> MaterialTheme.colorScheme.secondary
                        is MessageProducerConsumerViewModel.MessageStatus.Received -> MaterialTheme.colorScheme.tertiary
                        is MessageProducerConsumerViewModel.MessageStatus.Processed -> MaterialTheme.colorScheme.primary
                    }
                )
            }

            Text(
                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .format(Date(message.timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}