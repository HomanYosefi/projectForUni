package com.example.homanproj


import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.homanproj.DevelopersSection
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        if (!backPressedOnce) {
            backPressedOnce = true
            Toast.makeText(
                context,
                "برای خروج دوباره دکمه برگشت را لمس کنید",
                Toast.LENGTH_SHORT
            ).show()

            coroutineScope.launch {
                kotlinx.coroutines.delay(2000)
                backPressedOnce = false
            }
        } else {
            // اینجا می‌توانید اپلیکیشن را ببندید
        }
    }

    val items = listOf(
        Item(
            name = "همگام‌سازی با مانیتور",
            description = "پیاده‌سازی الگوی تولیدکننده-مصرف‌کننده با استفاده از مانیتور",
            color = MaterialTheme.colorScheme.primaryContainer,
            navigationRoute = "ProducerConsumerScreen"
        ),
        Item(
            name = "خوانندگان-نویسندگان",
            description = "مدیریت دسترسی همزمان به منابع مشترک",
            color = MaterialTheme.colorScheme.secondaryContainer,
            navigationRoute = "ReaderWriterScreen"
        ),
        Item(
            name = "فیلسوف‌های غذاخوری",
            description = "حل مسئله کلاسیک با استفاده از سمافور",
            color = MaterialTheme.colorScheme.surfaceVariant,
            navigationRoute = "DiningPhilosophersScreen"
        ),
        Item(
            name = "تبادل پیام",
            description = "ارتباط بین پروسه‌ها با مکانیزم تبادل پیام",
            color = MaterialTheme.colorScheme.secondaryContainer,
            navigationRoute = "MessageProducerConsumerScreen"
        ),
        Item(
            name = "بافر دایره‌ای",
            description = "پیاده‌سازی بافر نامحدود با ساختار دایره‌ای",
            color = MaterialTheme.colorScheme.tertiaryContainer,
            navigationRoute = "BarberShopScreen"
        ),
        Item(
            name = "راهنمای جامع",
            description = "آموزش و توضیحات کامل مسائل همگام‌سازی",
            color = MaterialTheme.colorScheme.primaryContainer,
            navigationRoute = "GuideScreen"
        ),
        Item(
            name = "توسعه دهندگان",
            description = "لیست ماها",
            color = MaterialTheme.colorScheme.primaryContainer,
            navigationRoute = "DevelopersSection"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "مسائل همگام‌سازی",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                ElevatedCard(
                    onClick = { navController.navigate(item.navigationRoute) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = item.color
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// دیتا کلاس بهبود یافته
data class Item(
    val name: String,
    val description: String,
    val color: Color,
    val navigationRoute: String
)