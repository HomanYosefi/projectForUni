package com.example.homanproj.three


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DiningPhilosophersScreen(
    viewModel: DiningPhilosophersViewModel = hiltViewModel()
) {
    val philosophers by viewModel.philosophers.collectAsStateWithLifecycle()
    val forks by viewModel.forks.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 16.dp),
    ) {
        // Control Panel
        ControlPanel(
            onStart = viewModel::startDining,
            onStop = viewModel::stopDining,
            modifier = Modifier.fillMaxWidth()
        )

        // Table Visualization
        TableVisualization(
            philosophers = philosophers,
            forks = forks,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        // Philosophers Status
        PhilosophersStatus(
            philosophers = philosophers,
            modifier = Modifier.fillMaxWidth()
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
        modifier = modifier.padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onStart) {
            Text("Start Dining")
        }
        Button(
            onClick = onStop,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Stop Dining")
        }
    }
}

@Composable
fun TableVisualization(
    philosophers: List<Philosopher>,
    forks: List<Fork>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw table
            val center = Offset(size.width / 2, size.height / 2)
            val radius = minOf(size.width, size.height) * 0.4f

            drawCircle(
                color = Color.Gray,
                radius = radius,
                center = center
            )

            // Draw philosophers and forks
            val angleStep = 360f / philosophers.size
            philosophers.forEachIndexed { index, philosopher ->
                val angle = index * angleStep
                val philosopherPos = getPositionOnCircle(center, radius, angle)
                val forkPos = getPositionOnCircle(center, radius * 0.8f, angle + angleStep / 2)

                // Draw philosopher
                drawCircle(
                    color = when (philosopher.state) {
                        PhilosopherState.THINKING -> Color.Blue
                        PhilosopherState.HUNGRY -> Color.Yellow
                        PhilosopherState.EATING -> Color.Green
                    },
                    radius = 30f,
                    center = philosopherPos
                )

                // Draw fork
                drawLine(
                    color = Color.Black,
                    start = forkPos,
                    end = Offset(
                        forkPos.x + 20f * cos((angle + angleStep / 2) * PI.toFloat() / 180f),
                        forkPos.y + 20f * sin((angle + angleStep / 2) * PI.toFloat() / 180f)
                    ),
                    strokeWidth = 5f
                )
            }
        }
    }
}

@Composable
fun PhilosophersStatus(
    philosophers: List<Philosopher>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(philosophers) { philosopher ->
            PhilosopherStatusItem(philosopher)
        }
    }
}

@Composable
fun PhilosopherStatusItem(
    philosopher: Philosopher
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
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
                    text = philosopher.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "State: ${philosopher.state}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (philosopher.state) {
                        PhilosopherState.THINKING -> MaterialTheme.colorScheme.primary
                        PhilosopherState.HUNGRY -> MaterialTheme.colorScheme.error
                        PhilosopherState.EATING -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }
            Text(
                text = "Meals: ${philosopher.mealsEaten}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun getPositionOnCircle(center: Offset, radius: Float, angleDegrees: Float): Offset {
    val angleRadians = angleDegrees * PI.toFloat() / 180f
    return Offset(
        x = center.x + radius * cos(angleRadians),
        y = center.y + radius * sin(angleRadians)
    )
}