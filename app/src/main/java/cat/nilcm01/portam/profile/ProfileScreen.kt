package cat.nilcm01.portam.profile

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.ui.theme.ColorRed
import cat.nilcm01.portam.ui.theme.ColorWhiteAprox
import cat.nilcm01.portam.ui.values.BarTopHeight
import cat.nilcm01.portam.ui.values.BarTopInnerPadding
import cat.nilcm01.portam.ui.values.CornerRadiusMedium
import cat.nilcm01.portam.ui.values.IconSizeMediumSmall
import cat.nilcm01.portam.ui.values.PaddingLarge
import cat.nilcm01.portam.ui.values.PaddingMedium
import cat.nilcm01.portam.utils.StorageManager

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToReceipts: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToAssistance: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    // User name
    val userName by remember { mutableStateOf("Nom d'usuari") }
    val userSurnames by remember { mutableStateOf("Cognoms de l'usuari") }
    val userId by remember { mutableStateOf(26542838445) }


    // VIEW

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //// HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(BarTopHeight)
                .background(
                    color = MaterialTheme.colorScheme.primary
                )
                .padding(horizontal = 0.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(
                modifier = Modifier
                    .width(IconSizeMediumSmall)
                    .padding(horizontal = BarTopInnerPadding, vertical = 0.dp)
            )
            Text(
                text = "Perfil d'usuari",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier
                    .width(IconSizeMediumSmall)
                    .padding(horizontal = BarTopInnerPadding, vertical = 0.dp)
            )
        }

        //// Scrollable area for the content
        Column(
            modifier = Modifier
                .weight(1f) // constrain to remaining space so verticalScroll works
                .verticalScroll(rememberScrollState())
                .padding(PaddingLarge),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // User data display
            Text(
                text = "$userName\n$userSurnames\nID: $userId",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingLarge),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(PaddingLarge))

            // Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            onNavigateToSettings()
                        }
                    )
                    .padding(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_account_circle),
                    contentDescription = "Dades personals",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    "Dades personals",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(PaddingMedium))

            // Receipts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            onNavigateToReceipts()
                        }
                    )
                    .padding(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_account_circle),
                    contentDescription = "Factures",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    "Factures",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(PaddingMedium))

            // History
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            onNavigateToHistory()
                        }
                    )
                    .padding(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_history),
                    contentDescription = "Historial de validacions",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    "Historial de validacions",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(PaddingMedium))

            // Assistance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            onNavigateToAssistance()
                        }
                    )
                    .padding(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_user_boxed_mail),
                    contentDescription = "Assistència i contacte",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    "Assistència i contacte",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(PaddingMedium))

            // Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        ColorRed,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            onLogout()
                        }
                    )
                    .padding(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_account_circle_crossed),
                    contentDescription = "Tanca la sessió",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = ColorWhiteAprox
                )
                Text(
                    "Tanca la sessió",
                    color = ColorWhiteAprox,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}