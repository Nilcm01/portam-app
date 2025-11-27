package cat.nilcm01.portam.titles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.ui.values.BarTopHeight
import cat.nilcm01.portam.ui.values.BarTopInnerPadding
import cat.nilcm01.portam.ui.values.CornerRadiusMedium
import cat.nilcm01.portam.ui.values.IconSizeMediumSmall
import cat.nilcm01.portam.ui.values.PaddingLarge
import cat.nilcm01.portam.ui.values.PaddingMedium
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private object MainSteps {
    const val Loading = 1
    const val Success = 2
    const val Error = -1
}

data class UserTitle(
    val id: Int,
    val title: Int,
    val user: String,
    val name: String,
    val description: String,
    val uses: Int,
    val uses_left: Int,
    val first_use: String,
    val expiration: String,
    val re_entry: Int,
    val link: Int,
    val num_zones: Int,
    val active: Boolean
)

data class MainApiResult(
    val success: Boolean,
    val user_id: Int,
    val user_titles: List<UserTitle>
)

var userTitles: List<UserTitle>? = null

fun getUserTitlesApi(): MainApiResult {
    // Simulate API call delay
    Thread.sleep(1000)
    // TODO: Implement API call to fetch user titles
    return MainApiResult(
        success = true,
        user_id = 123,
        user_titles = listOf(
            UserTitle(
                id = 200474,
                title = 10,
                user = "1234567890ab",
                name = "T-Mes - 3 zones",
                description = "10 viatges unipersonals en 3 zones durant 30 dies.",
                uses = 10,
                uses_left = 5,
                first_use = "2025-11-01T10:03:42Z",
                expiration = "2025-12-01T10:03:42Z",
                re_entry = 15,
                link = 120,
                num_zones = 3,
                active = false
            ),
            UserTitle(
                id = 200475,
                title = 11,
                user = "1234567890ab",
                name = "T-10 - 2 zones",
                description = "10 viatges multipersonals en 2 zones.",
                uses = 10,
                uses_left = 2,
                first_use = "2025-11-12T10:03:42Z",
                expiration = "2025-12-12T10:03:42Z",
                re_entry = 0,
                link = 90,
                num_zones = 2,
                active = true
            ),
            UserTitle(
                id = 200476,
                title = 11,
                user = "1234567890ab",
                name = "T-10 - 1 zona",
                description = "10 viatges multipersonals en 1 zona.",
                uses = 10,
                uses_left = 0,
                first_use = "2025-11-12T10:03:42Z",
                expiration = "2025-12-12T10:03:42Z",
                re_entry = 0,
                link = 90,
                num_zones = 1,
                active = false
            ),
            UserTitle(
                id = 200478,
                title = 11,
                user = "1234567890ab",
                name = "T-10 - 1 zona",
                description = "10 viatges multipersonals en 1 zona.",
                uses = 10,
                uses_left = 2,
                first_use = "2025-11-12T10:03:42Z",
                expiration = "2025-11-12T10:03:42Z",
                re_entry = 0,
                link = 90,
                num_zones = 1,
                active = false
            )
        )
    )
}

fun activateTitleApi(id: Int): Boolean {
    // Simulate API call delay
    Thread.sleep(1000)
    // TODO: Implement API call to activate title
    // For now, set all titles to inactive except the one with the given id
    userTitles = userTitles?.map { title ->
        if (title.id == id) {
            title.copy(active = true)
        } else {
            title.copy(active = false)
        }
    }
    return true
}

@Composable
fun TitlesScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddTitle: () -> Unit = {}
) {
    var step by remember { mutableStateOf(MainSteps.Loading) }
    var userTitlesList by remember { mutableStateOf<List<UserTitle>>(emptyList()) }
    var activatingTitle by remember { mutableStateOf<Int?>(null) }


    // Load userTitles on first composition
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val result = getUserTitlesApi()
            if (result.success) {
                userTitlesList = result.user_titles
                userTitles = result.user_titles
                step = MainSteps.Success
            } else {
                step = MainSteps.Error
            }
        }
    }


    // View

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
            Spacer(
                modifier = Modifier
                    .width(IconSizeMediumSmall)
                    .padding(horizontal = BarTopInnerPadding, vertical = 0.dp)
            )
            Text(
                text = "Títols",
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

        // Scrollable content area for titles
        Column(
            modifier = Modifier
                .weight(1f) // constrain to remaining space so verticalScroll works
                .verticalScroll(rememberScrollState())
                .padding(PaddingLarge),
            verticalArrangement = if (step != MainSteps.Success) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Loading state
            if (step == MainSteps.Loading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Carregant els teus títols",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator( color = MaterialTheme.colorScheme.primary )
                }
            }

            // Error state
            else if (step == MainSteps.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "S'ha produït un error en carregar els teus títols.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = FontStyle.Italic
                )
            }

            // Success state
            else if (step == MainSteps.Success) {

                // Sort userTitles: active first, then from closest to furthest expiration date,
                // then already expired ones, by newer to older expiration date (use current date)
                val sortedUserTitles = userTitlesList.sortedWith(
                    compareByDescending<UserTitle> { it.active }
                        .thenBy { if (it.expiration < java.time.Instant.now().toString()) 1 else 0 }
                        .thenBy { it.expiration }
                )


                // Add new title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(CornerRadiusMedium)
                        )
                        .clickable(
                            onClick = {
                                onNavigateToAddTitle()
                            }
                        )
                        .padding(PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_add_circle),
                        contentDescription = "Afegeix un nou títol",
                        modifier = Modifier.size(IconSizeMediumSmall),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "Afegeix un nou títol",
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                //Spacer(modifier = Modifier.height(PaddingMedium))

                // Dynamically list user titles
            }
        }
    }
}