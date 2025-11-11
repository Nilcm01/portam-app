package cat.nilcm01.portam.cards

import android.R
import android.widget.ScrollView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.ui.theme.ColorRed
import cat.nilcm01.portam.ui.values.*

@Composable
fun SuportsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    // Intercept system back / gesture and call the provided onBack lambda
    BackHandler(enabled = true) {
        onBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            Icon(
                modifier = Modifier
                    .size(IconSizeMediumSmall)
                    .clickable(
                        onClick = {
                            onBack()
                        }
                    )
                    .padding(horizontal = 0.dp, vertical = 0.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Torna enrere",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Suports",
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

        // Scrollable content area for suports
        Column(
            modifier = Modifier
                .weight(1f) // constrain to remaining space so verticalScroll works
                .verticalScroll(rememberScrollState())
                .padding(PaddingLarge),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Example suport items
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .padding(PaddingMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_mobile_cast),
                    contentDescription = "Targeta virtual",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        "Targeta virtual",
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        "UID: 0493f7b78f6181",
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        "Actiu des de: 01/01/2024",
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Left
                    )
                }
                Spacer(modifier = Modifier.width(IconSizeMediumSmall))
            }
            Spacer(modifier = Modifier.height(PaddingMedium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .padding(PaddingMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_card_band),
                    contentDescription = "Targeta física",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        "Targeta física",
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        "UID: 0d94e613ff67b2",
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        "Actiu des de: 11/12/2024",
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Left
                    )
                }
                IconButton(
                    onClick = { /* TODO: Handle delete action */ }
                ) {
                    Icon(
                        painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_trash_cross),
                        contentDescription = "Elimina targeta",
                        modifier = Modifier.size(IconSizeMediumSmall),
                        tint = ColorRed
                    )
                }
            }
            Spacer(modifier = Modifier.height(PaddingMedium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            // TODO: Handle add new suport action
                        }
                    )
                    .padding(PaddingMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Afegeix un nou suport",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}