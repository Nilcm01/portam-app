package cat.nilcm01.portam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cat.nilcm01.portam.cards.CardScreen
import cat.nilcm01.portam.cards.SuportsScreen
import cat.nilcm01.portam.home.HomeScreen
import cat.nilcm01.portam.profile.ProfileScreen
import cat.nilcm01.portam.titles.TitlesScreen
import cat.nilcm01.portam.ui.theme.PortamTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import cat.nilcm01.portam.cards.AddSuportScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PortamTheme {
                MainScreen()
            }
        }
    }
}

enum class BottomDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    HOME("home", "Inici", Icons.Filled.Home),
    CARD_GRAPH("card_graph", "Targeta", Icons.Filled.Email),
    TITLES("titles", "Títols", Icons.Filled.Menu),
    PROFILE("profile", "Perfil", Icons.Filled.AccountCircle),
}

private object Routes {
    // HOME
    const val Home = "home"

    // CARDS - SUPORTS
    const val CardGraph = "card_graph"
    const val CardMain = "card/main"
    const val CardSuports = "card/suports"
    const val CardAddSuport = "card/add_suport"

    // TITLES
    const val Titles = "titles"

    // PROFILE - ACCOUNT ADMIN
    const val Profile = "profile"
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Saber quina ruta estem per marcar la bottom bar
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = backStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                val items = listOf(
                    BottomDestination.HOME,
                    BottomDestination.CARD_GRAPH,
                    BottomDestination.TITLES,
                    BottomDestination.PROFILE
                )

                items.forEach { destination ->
                    val selected = currentDest?.hierarchy?.any { it.route == destination.route } == true ||
                            // Per al cas del gràfic anidat de Card, marca com seleccionat si estem dins de qualsevol "card/*"
                            (destination == BottomDestination.CARD_GRAPH &&
                                    currentDest?.hierarchy?.any { it.route?.startsWith("card") == true } == true)

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(text = destination.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            indicatorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.0f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(1)) },
            exitTransition = { fadeOut(tween(1)) }
        ) {
            // HOME (top-level)
            composable(Routes.Home) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    onNavigateToCards = {
                        // Si vols obrir directament la pestanya Card
                        navController.navigate(Routes.CardMain) {
                            // Ens assegurem d’estar dins el gràfic de card
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // CARD GRAPH (top-level → amb rutes internes)
            navigation(
                startDestination = Routes.CardMain,
                route = Routes.CardGraph
            ) {
                composable(
                    Routes.CardMain
                ) {
                    CardScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavigateToSuports = {
                            navController.navigate(Routes.CardSuports)
                        }
                    )
                }
                composable(
                    Routes.CardSuports
                ) {
                    SuportsScreen(
                        modifier = Modifier.fillMaxSize(),
                        onBack = { navController.popBackStack() },
                        onNavigateToAddSuport = {
                            navController.navigate(Routes.CardAddSuport)
                        }
                    )
                }
                composable(
                    Routes.CardAddSuport
                ) {
                    AddSuportScreen(
                        modifier = Modifier.fillMaxSize(),
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // TITLES (top-level simple)
            composable(Routes.Titles) {
                TitlesScreen(Modifier.fillMaxSize())
            }

            // PROFILE (top-level simple)
            composable(Routes.Profile) {
                ProfileScreen(Modifier.fillMaxSize())
            }
        }
    }
}