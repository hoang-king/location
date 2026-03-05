package com.example.location.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.location.data.remote.firebase.FirebaseAuthService
import com.example.location.presentation.screen.auth.LoginScreen
import com.example.location.presentation.screen.map.MapScreen
import com.example.location.presentation.screen.room.RoomScreen
import com.example.location.utils.Constants

@Composable
fun LocationNavGraph(
    authService: FirebaseAuthService,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (authService.isLoggedIn) Constants.ROUTE_ROOMS else Constants.ROUTE_LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Constants.ROUTE_LOGIN) {
            LoginScreen(
                authService = authService,
                onLoginSuccess = {
                    navController.navigate(Constants.ROUTE_ROOMS) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Constants.ROUTE_ROOMS) {
            RoomScreen(
                onNavigateToMap = { roomId ->
                    navController.navigate("map/$roomId")
                },
                onNavigateToLogin = {
                    navController.navigate(Constants.ROUTE_LOGIN) {
                        popUpTo(Constants.ROUTE_ROOMS) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Constants.ROUTE_MAP,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) {
            MapScreen(onBack = { navController.popBackStack() })
        }
    }
}
