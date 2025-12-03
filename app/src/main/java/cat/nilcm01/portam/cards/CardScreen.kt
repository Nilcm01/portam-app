package cat.nilcm01.portam.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.R
import cat.nilcm01.portam.ui.values.CornerRadiusMedium
import cat.nilcm01.portam.ui.values.CornerRadiusSmall
import cat.nilcm01.portam.ui.values.IconSizeLarge
import cat.nilcm01.portam.ui.values.IconSizeMedium
import cat.nilcm01.portam.ui.values.PaddingLarge
import cat.nilcm01.portam.ui.values.PaddingMedium
import cat.nilcm01.portam.ui.values.PaddingSmall
import cat.nilcm01.portam.ui.values.PaddingXXLarge

@Composable
fun CardScreen(
    modifier: Modifier = Modifier,
    onNavigateToSuports: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = PaddingLarge, vertical = PaddingMedium),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (
            modifier = Modifier
                .padding(horizontal = PaddingXXLarge)
                .aspectRatio(0.6f)
                .background(
                    MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(CornerRadiusSmall)
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_contactless),
                contentDescription = "Contactless",
                modifier = Modifier
                    .size(IconSizeLarge)
                    .rotate(270.0f),
                tint = MaterialTheme.colorScheme.onSecondary
            )

            Text(
                "Nom\nCognom Cognom",
                modifier = Modifier
                    .padding(
                        horizontal = PaddingMedium,
                        vertical = PaddingSmall
                    )
                    .fillMaxWidth(),
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Text(
                "Títol actiu:\nT-10 - 2 Zones\nZona d'origen: Zona 3\nQueden: 7 viatges\nVàlid fins al: 31/12/2024",
                modifier = Modifier
                    .padding(
                        horizontal = PaddingMedium,
                        vertical = PaddingSmall
                    )
                    .fillMaxWidth(),
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Column (
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            onNavigateToSuports()
                        },
                        role = Role.Button
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_card_stack),
                        contentDescription = "Gestiona els suports",
                        modifier = Modifier.size(IconSizeMedium),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Gestiona els suports",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            onNavigateToHistory()
                        },
                        role = Role.Button
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_history),
                        contentDescription = "Historial de validacions",
                        modifier = Modifier.size(IconSizeMedium),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Historial de validacions",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}