package com.pilltack.app.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.pilltack.app.data.Repo

@Composable
fun AppRoot(repo: Repo) {
    val nav = rememberNavController()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route ?: ""
    val tabs = listOf(
        BarTab("home", "home", "Today", Icons.Filled.Today),
        BarTab("meds", "medicines", "Medicines", Icons.Filled.Medication),
        BarTab("add", "add?med=-1", "Add", Icons.Filled.AddCircleOutline),
        BarTab("cal", "calendar", "Calendar", Icons.Filled.CalendarMonth),
        BarTab("hist", "history?med=-1", "History", Icons.Filled.History)
    )
    val showBar = route == "home" || route == "calendar" || route.startsWith("history") || route == "medicines"
    val onBrand = route == "" || route == "splash" || route == "onboarding"
    SystemBars(if (onBrand) PT.Brand else PT.Bg, if (onBrand) PT.Brand else PT.Bg, lightBg = !onBrand && !PT.dark)
    val selected = tabs.indexOfFirst { it.key != "add" && route.startsWith(it.route.substringBefore('?')) }.coerceAtLeast(0)

    Scaffold(
        containerColor = PT.Bg, contentColor = PT.Ink,
        bottomBar = {
            AnimatedVisibility(showBar,
                enter = slideInVertically(tween(300)) { it } + fadeIn(tween(300)),
                exit = slideOutVertically(tween(200)) { it } + fadeOut(tween(150))) {
                PillBar(tabs, selected) { i ->
                    val t = tabs[i]
                    if (t.key == "add") nav.navigate(t.route)
                    else nav.navigate(t.route) { popUpTo("home"); launchSingleTop = true }
                }
            }
        }
    ) { pad ->
        NavHost(nav, "splash", Modifier.padding(pad),
            enterTransition = { fadeIn(tween(260)) + slideInHorizontally(tween(260)) { it / 12 } },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(260)) + slideInHorizontally(tween(260)) { -it / 12 } },
            popExitTransition = { fadeOut(tween(160)) + slideOutHorizontally(tween(260)) { it / 12 } }) {
            composable("splash") { SplashScreen(nav, repo) }
            composable("onboarding") { OnboardingScreen(nav, repo) }
            composable("home") { HomeScreen(nav, repo) }
            composable("add?med={med}", listOf(navArgument("med") { type = NavType.LongType; defaultValue = -1L })) {
                AddScreen(nav, repo, it.arguments?.getLong("med") ?: -1L)
            }
            composable("calendar") { CalendarScreen(nav, repo) }
            composable("history?med={med}", listOf(navArgument("med") { type = NavType.LongType; defaultValue = -1L })) {
                HistoryScreen(nav, repo, it.arguments?.getLong("med") ?: -1L)
            }
            composable("medicines") { MedicinesScreen(nav, repo) }
            composable("refill") { RefillScreen(nav, repo) }
            composable("menu") { MenuScreen(nav) }
            composable("settings") { SettingsScreen(nav, repo) }
            composable("success") { SuccessScreen(nav) }
        }
    }
}
