package cat.nilcm01.portam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import cat.nilcm01.portam.cards.CardScreen
import cat.nilcm01.portam.cards.SuportsScreen
import cat.nilcm01.portam.home.HomeScreen
import cat.nilcm01.portam.profile.ProfileScreen
import cat.nilcm01.portam.titles.TitlesScreen
import cat.nilcm01.portam.ui.theme.PortamTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var keepOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepOnScreen }

        lifecycleScope.launch {
            // TODO: initialize data
            delay(1000L) // Simulate loading

            keepOnScreen = false
        }
        enableEdgeToEdge()
        setContent {
            PortamTheme {
                MainScreen()
            }
        }
    }
}

// Destinacions de la bottom bar
enum class BottomDestination(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    HOME("Inici", Icons.Filled.Home),
    CARD("Targeta", Icons.Filled.Email),
    TITLES("Títols", Icons.Filled.Menu),
    PROFILE("Perfil", Icons.Filled.AccountCircle)
}

@Composable
fun MainScreen() {
    var selectedDestination by rememberSaveable { mutableStateOf(BottomDestination.HOME) }
    var showSuports by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                BottomDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = selectedDestination == destination,
                        onClick = {
                            selectedDestination = destination
                            showSuports = false // reset nested screen when switching tabs
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
                            indicatorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedDestination) {
            BottomDestination.HOME -> HomeScreen(
                Modifier.padding(innerPadding),
                onNavigateToCards = { selectedDestination = BottomDestination.CARD }
            )

            BottomDestination.CARD -> {
                if (showSuports) {
                    SuportsScreen(
                        Modifier.padding(innerPadding),
                        onBack = { showSuports = false }
                    )
                } else {
                    CardScreen(
                        Modifier.padding(innerPadding),
                        onNavigateToSuports = { showSuports = true }
                    )
                }
            }

            BottomDestination.TITLES -> TitlesScreen(
                Modifier.padding(innerPadding)
            )

            BottomDestination.PROFILE -> ProfileScreen(
                Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PortamTheme {
        MainScreen()
    }
}