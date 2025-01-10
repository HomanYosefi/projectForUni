package com.example.homanproj.five


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun BarberShopScreen(
    viewModel: BarberShopViewModel = hiltViewModel()
) {
    val barber by viewModel.barber.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val waitingRoom by viewModel.waitingRoom.collectAsStateWithLifecycle()
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

        // Barber Status
        BarberCard(
            barber = barber,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Waiting Room
            WaitingRoomCard(
                customers = waitingRoom,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            // Completed Customers
            CompletedCustomersCard(
                customers = customers.filter { it.status == BarberShopViewModel.Status.DONE },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
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
fun BarberCard(
    barber: BarberShopViewModel.Barber,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (barber.status) {
                BarberShopViewModel.Status.SLEEPING -> MaterialTheme.colorScheme.secondaryContainer
                BarberShopViewModel.Status.CUTTING -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Barber Status: ${barber.status}",
                style = MaterialTheme.typography.titleMedium
            )
            if (barber.currentCustomer != null) {
                Text(
                    text = "Current Customer: ${barber.currentCustomer.id}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "Total Haircuts: ${barber.totalHaircuts}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun WaitingRoomCard(
    customers: List<BarberShopViewModel.Customer>,
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
                text = "Waiting Room (${customers.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(customers) { customer ->
                    CustomerItem(customer)
                }
            }
        }
    }
}

@Composable
fun CompletedCustomersCard(
    customers: List<BarberShopViewModel.Customer>,
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
                text = "Completed Haircuts (${customers.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(customers) { customer ->
                    CustomerItem(customer)
                }
            }
        }
    }
}

@Composable
fun CustomerItem(
    customer: BarberShopViewModel.Customer
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (customer.status) {
                BarberShopViewModel.Status.WAITING -> MaterialTheme.colorScheme.secondaryContainer
                BarberShopViewModel.Status.DONE -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Text(
            text = "Customer ${customer.id}",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LogsList(
    logs: List<BarberShopViewModel.LogEntry>,
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
fun LogEntry(log: BarberShopViewModel.LogEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = log.message,
            style = MaterialTheme.typography.bodyMedium,
            color = when (log.type) {
                BarberShopViewModel.LogType.SUCCESS -> MaterialTheme.colorScheme.primary
                BarberShopViewModel.LogType.ERROR -> MaterialTheme.colorScheme.error
                BarberShopViewModel.LogType.INFO -> MaterialTheme.colorScheme.onSurface
            }
        )
        Text(
            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(Date(log.timestamp)),
            style = MaterialTheme.typography.bodySmall
        )
    }
}