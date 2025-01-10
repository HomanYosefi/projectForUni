package com.example.homanproj


// Compose UI
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

// Compose Material 3
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

// Compose Runtime
import androidx.compose.runtime.Composable

// Compose UI Utils
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun GuideScreen() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "راهنمای مسائل همگام‌سازی",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 1. Producer-Consumer with Monitor
            item {
                ProblemCard(
                    number = "1️⃣",
                    title = "همگام‌سازی با مانیتور",
                    description = "پیاده‌سازی الگوی تولیدکننده-مصرف‌کننده با استفاده از مانیتور",
                    points = listOf(
                        "استفاده از مانیتور برای مدیریت بافر مشترک",
                        "کنترل همزمانی با متغیرهای شرطی",
                        "نمایش وضعیت بافر و عملیات‌ها",
                        "جلوگیری از race condition",
                        "مدیریت حالت‌های پر و خالی بودن بافر"
                    )
                )
            }

            // 2. Readers-Writers
            item {
                ProblemCard(
                    number = "2️⃣",
                    title = "خوانندگان-نویسندگان",
                    description = "مدیریت دسترسی همزمان به منابع مشترک",
                    points = listOf(
                        "اولویت‌بندی خوانندگان و نویسندگان",
                        "امکان خواندن همزمان",
                        "انحصار در نوشتن",
                        "جلوگیری از گرسنگی",
                        "نمایش آمار دسترسی‌ها"
                    )
                )
            }

            // 3. Dining Philosophers
            item {
                ProblemCard(
                    number = "3️⃣",
                    title = "فیلسوف‌های غذاخوری",
                    description = "حل مسئله کلاسیک با استفاده از سمافور",
                    points = listOf(
                        "پنج فیلسوف و پنج چنگال",
                        "جلوگیری از deadlock",
                        "استفاده از سمافور",
                        "نمایش وضعیت فیلسوف‌ها",
                        "مدیریت منابع مشترک"
                    )
                )
            }

            // 4. Message Passing
            item {
                ProblemCard(
                    number = "4️⃣",
                    title = "تبادل پیام",
                    description = "ارتباط بین پروسه‌ها با مکانیزم تبادل پیام",
                    points = listOf(
                        "استفاده از کانال‌ها برای ارتباط",
                        "مدیریت همزمانی با تبادل پیام",
                        "عدم نیاز به متغیر مشترک",
                        "ارتباط ایمن بین پروسه‌ها",
                        "نمایش جریان پیام‌ها"
                    )
                )
            }

            // 5. Circular Buffer (Sleeping Barber)
            item {
                ProblemCard(
                    number = "5️⃣",
                    title = "بافر دایره‌ای",
                    description = "پیاده‌سازی بافر نامحدود با ساختار دایره‌ای",
                    points = listOf(
                        "مدیریت صف دایره‌ای",
                        "کنترل ظرفیت بافر",
                        "همگام‌سازی تولیدکننده و مصرف‌کننده",
                        "جلوگیری از شرایط رقابت",
                        "نمایش وضعیت بافر"
                    )
                )
            }
        }
    }
}
@Composable
fun ProblemCard(
    number: String,
    title: String,
    description: String,
    points: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            points.forEach { point ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("•")
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}