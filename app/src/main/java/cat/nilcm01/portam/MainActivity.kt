package cat.nilcm01.portam

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cat.nilcm01.portam.cards.CardScreen
import cat.nilcm01.portam.cards.SuportsScreen
import cat.nilcm01.portam.home.HomeScreen
import cat.nilcm01.portam.profile.ProfileScreen
import cat.nilcm01.portam.titles.AddTitleScreen
import cat.nilcm01.portam.titles.TitlesScreen
import cat.nilcm01.portam.ui.theme.PortamTheme
import cat.nilcm01.portam.utils.NfcHandler
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import cat.nilcm01.portam.cards.AddSuportScreen
import cat.nilcm01.portam.login.LoginScreen
import cat.nilcm01.portam.utils.StorageManager


class MainActivity : ComponentActivity() {
    // Mutable state to hold the latest NFC tag UID
    private val nfcTagUid = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        StorageManager.initialize(this)

        // Handle initial intent if launched via NFC
        handleNfcIntent(intent)

        setContent {
            PortamTheme {
                MainScreen(nfcTagUid = nfcTagUid.value)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent?.action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {

            val uid = NfcHandler.extractTagUid(intent)
            if (uid != null) {
                nfcTagUid.value = uid
            }
        }
    }

    fun getNfcHandler(): NfcHandler {
        return NfcHandler(this)
    }

    fun consumeNfcTagUid(): String? {
        val uid = nfcTagUid.value
        nfcTagUid.value = null
        return uid
    }
}

enum class BottomDestination(
    val route: String,
    val label: String,
    val icon: Int
) {
    HOME("home", "Inici", R.drawable.icon_home_filled),
    CARD_GRAPH("card_graph", "Targeta", R.drawable.icon_contactless_filled),
    TITLES_GRAPH("titles_graph", "Títols", R.drawable.icon_transport_ticket_filled),
    PROFILE("profile", "Perfil", R.drawable.icon_account_circle),
}

private object Routes {
    // LOGIN
    const val Login = "login"

    // HOME
    const val Home = "home"

    // CARDS - SUPORTS
    const val CardGraph = "card_graph"
    const val CardMain = "card/main"
    const val CardSuports = "card/suports"
    const val CardAddSuport = "card/add_suport"

    // TITLES
    const val TitlesGraph = "titles_graph"
    const val TitlesMain = "titles/main"
    const val TitlesAdd = "titles/add"

    // PROFILE - ACCOUNT ADMIN
    const val Profile = "profile"
}

@Composable
fun MainScreen(nfcTagUid: String? = null) {
    val navController = rememberNavController()

    // Saber quina ruta estem per marcar la bottom bar
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = backStackEntry?.destination

    // Check if we're on the Login screen
    val isLoginScreen = currentDest?.route == Routes.Login

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!isLoginScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    val items = listOf(
                        BottomDestination.HOME,
                        BottomDestination.CARD_GRAPH,
                        BottomDestination.TITLES_GRAPH,
                        BottomDestination.PROFILE
                    )

                    items.forEach { destination ->
                        val selected = currentDest?.hierarchy?.any { it.route == destination.route } == true ||
                                // Per al cas del gràfic anidat de Card, marca com seleccionat si estem dins de qualsevol "card/*"
                                (destination == BottomDestination.CARD_GRAPH &&
                                        currentDest?.hierarchy?.any { it.route?.startsWith("card") == true } == true) ||
                                // Per al cas del gràfic anidat de Titles, marca com seleccionat si estem dins de qualsevol "titles/*"
                                (destination == BottomDestination.TITLES_GRAPH &&
                                        currentDest?.hierarchy?.any { it.route?.startsWith("titles") == true } == true)

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
                                    painter = painterResource(destination.icon),
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
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Login,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(1)) },
            exitTransition = { fadeOut(tween(1)) }
        ) {
            // LOGIN (top-level)
            composable(Routes.Login) {
                LoginScreen(
                    modifier = Modifier.fillMaxSize(),
                    onLoginSuccess = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                )
            }

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
                        onBack = { navController.popBackStack() },
                        nfcTagUid = nfcTagUid
                    )
                }
            }

            // TITLES GRAPH (top-level → amb rutes internes)
            navigation(
                startDestination = Routes.TitlesMain,
                route = Routes.TitlesGraph
            ) {
                composable(
                    Routes.TitlesMain
                ) {
                    TitlesScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavigateToAddTitle = {
                            navController.navigate(Routes.TitlesAdd)
                        }
                    )
                }
                composable(
                    Routes.TitlesAdd
                ) {
                    AddTitleScreen(
                        modifier = Modifier.fillMaxSize(),
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // PROFILE (top-level simple)
            composable(Routes.Profile) {
                ProfileScreen(Modifier.fillMaxSize())
            }
        }
    }
}