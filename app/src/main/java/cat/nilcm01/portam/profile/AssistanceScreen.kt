package cat.nilcm01.portam.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.ui.theme.ColorWhiteAprox
import cat.nilcm01.portam.ui.values.BarTopHeight
import cat.nilcm01.portam.ui.values.BarTopInnerPadding
import cat.nilcm01.portam.ui.values.CornerRadiusMedium
import cat.nilcm01.portam.ui.values.IconSizeMediumSmall
import cat.nilcm01.portam.ui.values.PaddingLarge
import cat.nilcm01.portam.ui.values.PaddingMedium
import cat.nilcm01.portam.ui.values.PaddingSmall

@Composable
fun AssistanceScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { }
) {
    // Intercept system back / gesture and call the provided onBack lambda
    BackHandler(enabled = true) {
        onBack()
    }

    // View
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
                text = "Assistència i contacte",
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

        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f) // constrain to remaining space so verticalScroll works
                .verticalScroll(rememberScrollState())
                .padding(PaddingLarge),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header text
            Text(
                modifier = Modifier
                    .fillMaxWidth() // Comment this line to center the text
                    .padding(
                        bottom = PaddingSmall,
                        top = PaddingSmall,
                        start = 0.dp,
                        end = 0.dp
                    ),
                text = "Necessiteu ajuda, suport o teniu cap comentari?" +
                        "\n\n" +
                        "Contacteu-nos a través de les següents vies:",
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(PaddingMedium))
            // Email
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .padding(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_mail),
                    contentDescription = "Correu electrònic",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingMedium, 0.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        "Correu electrònic:",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left,
                    )
                    Text(
                        "suport@portam.cat",
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Left,
                    )
                }

            }
            Spacer(modifier = Modifier.height(PaddingMedium))
            // Phone
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .padding(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_telephone),
                    contentDescription = "Telèfon",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingMedium, 0.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        "Telèfon:",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        "600 000 000\n(Dilluns a divendres de 8 a 20h)",
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Left
                    )
                }
            }
            Spacer(modifier = Modifier.height(PaddingMedium))
            // Office
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .padding(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_building),
                    contentDescription = "Oficina",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingMedium, 0.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        "Oficina:",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        "Carrer de les Sitges, Edifici Q\n08193 Bellaterra, Barcelona",
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Left
                    )
                }
            }
        }
    }
}