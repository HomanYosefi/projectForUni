package com.example.homanproj


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homanproj.five.BarberShopScreen
import com.example.homanproj.fore.ReaderWriterScreen
import com.example.homanproj.three.DiningPhilosophersScreen
import com.example.homanproj.too.MessageProducerConsumerScreen


@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("MainScreen") {
            MainScreen(navController = navController)
        }

        composable("ProducerConsumerScreen") {
            ProducerConsumerScreen()
        }

        composable("MessageProducerConsumerScreen") {
            MessageProducerConsumerScreen()
        }

        composable("DiningPhilosophersScreen") {
            DiningPhilosophersScreen()
        }

        composable("ReaderWriterScreen") {
            ReaderWriterScreen()
        }

        composable("BarberShopScreen") {
            BarberShopScreen()
        }

        composable("GuideScreen") {
            GuideScreen()
        }

        composable("DevelopersSection") {
            DevelopersSection()
        }

    }
}