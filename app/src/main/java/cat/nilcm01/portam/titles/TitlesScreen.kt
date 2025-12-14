package cat.nilcm01.portam.titles

import android.widget.Toast
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.ui.theme.ColorGreen
import cat.nilcm01.portam.ui.values.BarTopHeight
import cat.nilcm01.portam.ui.values.BarTopInnerPadding
import cat.nilcm01.portam.ui.values.CornerRadiusMedium
import cat.nilcm01.portam.ui.values.IconSizeMedium
import cat.nilcm01.portam.ui.values.IconSizeMediumSmall
import cat.nilcm01.portam.ui.values.PaddingLarge
import cat.nilcm01.portam.ui.values.PaddingMedium
import cat.nilcm01.portam.ui.values.PaddingSmall
import cat.nilcm01.portam.utils.StorageManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.emptyList

private object MainSteps {
    const val Loading = 1
    const val Success = 2
    const val Error = -1
}

data class UserTitle(
    val id: String,
    val title: String,
    val user: String,
    val name: String,
    val description: String,
    val uses_left: Int?, // null means unlimited uses
    val first_use: String,
    val expiration: String,
    val re_entry: Int,
    val link: Int,
    val num_zones: Int,
    val zone_origin: Int?,
    val active: Boolean
)

data class MainApiResult(
    val success: Boolean,
    val user_id: String,
    val user_titles: List<UserTitle>
)

suspend fun getUserTitlesApi(): MainApiResult {
    return withContext(Dispatchers.IO) {
        try {
            val client = HttpClient(Android) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }

            // Make GET request
            val response: HttpResponse =
                client.get(
                    "https://portam-server.vercel.app/api/titles/user/" +
                            "${StorageManager.getUserData()["userId"]}"
                ) {
                    contentType(io.ktor.http.ContentType.Application.Json)
                }

            val responseBody = response.bodyAsText()
            val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

            client.close()

            // Return MainApiResult
            val success = jsonResponse["success"]?.jsonPrimitive?.boolean ?: false
            val titles = jsonResponse["titles"]?.jsonArray
            val userTitles = titles?.map { titleElement ->
                val titleObj = titleElement.jsonObject
                UserTitle(
                    id = titleObj["id"]?.jsonPrimitive?.content ?: "",
                    title = titleObj["title"]?.jsonPrimitive?.content ?: "",
                    user = titleObj["user"]?.jsonPrimitive?.content ?: "",
                    name = titleObj["title_name"]?.jsonPrimitive?.content ?: "",
                    description = titleObj["title_description"]?.jsonPrimitive?.content ?: "",
                    uses_left = titleObj["uses_left"]?.jsonPrimitive?.intOrNull,
                    first_use = titleObj["first_use"]?.jsonPrimitive?.content ?: "",
                    expiration = titleObj["expiration"]?.jsonPrimitive?.content ?: "",
                    re_entry = titleObj["re_entry"]?.jsonPrimitive?.int ?: 0,
                    link = titleObj["link"]?.jsonPrimitive?.int ?: 0,
                    num_zones = titleObj["num_zones"]?.jsonPrimitive?.int ?: 0,
                    zone_origin = titleObj["zone_origin"]?.jsonPrimitive?.intOrNull,
                    active = titleObj["active"]?.jsonPrimitive?.boolean ?: false
                )
            } ?: emptyList()
            MainApiResult(
                success = success,
                user_id = StorageManager.getUserData()["userId"].toString(),
                user_titles = userTitles
            )
        } catch (e: Exception) {
            MainApiResult(
                success = false,
                user_id = StorageManager.getUserData()["userId"].toString(),
                user_titles = emptyList()
            )
        }
    }
}

suspend fun activateTitleApi(id: String, currentTitles: List<UserTitle>): List<UserTitle> {
    return withContext(Dispatchers.IO) {
        try {
            val client = HttpClient(Android) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }

            // Make GET request
            val response: HttpResponse =
                client.post(
                    "https://portam-server.vercel.app/api/titles/user/" +
                            "${StorageManager.getUserData()["userId"]}/" +
                            "$id/activate"
                ) {
                    contentType(io.ktor.http.ContentType.Application.Json)
                }

            val responseBody = response.bodyAsText()
            val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

            client.close()

            // Return MainApiResult
            val success = jsonResponse["success"]?.jsonPrimitive?.boolean ?: false

            if (success) {
                // Set all titles to inactive except the one with the given id
                currentTitles.map { title ->
                    if (title.id == id) {
                        title.copy(active = true)
                    } else {
                        title.copy(active = false)
                    }
                }
            } else {
                currentTitles
            }
        } catch (e: Exception) {
            currentTitles
        }
    }
}


@Composable
fun TitlesScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddTitle: () -> Unit = {}
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(MainSteps.Loading) }
    var userTitlesList by remember { mutableStateOf<List<UserTitle>>(emptyList()) }
    var activatingTitle by remember { mutableStateOf<String?>(null) }

    // Separate titles into active and expired based on userTitlesList
    val currentTime = remember { java.time.Instant.now().toString() }
    val activeUserTitles = remember(userTitlesList) {
        userTitlesList.filter {
            // Active if: (uses_left > 0 OR uses_left is null for unlimited) AND not expired
            (it.uses_left == null || it.uses_left > 0) && it.expiration >= currentTime
        }.sortedWith(
            compareByDescending<UserTitle> { it.active }
                .thenBy { it.expiration }
        )
    }
    val expiredUserTitles = remember(userTitlesList) {
        userTitlesList.filter {
            // Expired if: uses_left == 0 (exhausted) OR expiration date passed
            (it.uses_left != null && it.uses_left == 0) || it.expiration < currentTime
        }.sortedByDescending { it.expiration }
    }

    // Load userTitles on first composition
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val result = getUserTitlesApi()
            if (result.success) {
                userTitlesList = result.user_titles
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
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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

                // Active user titles header
                Spacer(modifier = Modifier.height(PaddingMedium))
                Text(
                    modifier = Modifier
                        .fillMaxWidth() // Comment this line to center the text
                        .padding(
                            bottom = PaddingMedium,
                            top = PaddingMedium,
                            start = 0.dp,
                            end = 0.dp
                        ),
                    text = "Els teus títols:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                // Dynamically list user titles
                activeUserTitles.forEach { title ->
                    val isActivating = activatingTitle == title.id
                    // Date format: DD-MM-YYYY
                    val expDateOriginal = title.expiration.take(10)
                    val expirationDate = "${expDateOriginal.substring(8, 10)}-${
                        expDateOriginal.substring(
                            5,
                            7
                        )
                    }-${expDateOriginal.substring(0, 4)}"

                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (title.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(CornerRadiusMedium)
                                )
                                .alpha(if (isActivating) 0.2f else 1f)
                                .padding(PaddingSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = PaddingMedium)
                            ) {
                                Text(
                                    text = title.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = title.description,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "Zones: ${title.num_zones} - Origen: ${title.zone_origin ?: "Pendent"}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "Caduca el: $expirationDate",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "Viatges restants: ${title.uses_left ?: "Il·limitats"}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                            }

                            // Show activate button
                            IconButton(
                                onClick = {
                                    if (!isActivating && !title.active) {
                                        activatingTitle = title.id
                                    }
                                },
                                enabled = !isActivating
                            ) {
                                if (!title.active) {
                                    Icon(
                                        painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_transport_ticket),
                                        contentDescription = "Activa el títol",
                                        modifier = Modifier.size(IconSizeMedium),
                                        tint = ColorGreen
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_contactless),
                                        contentDescription = "Títol actiu",
                                        modifier = Modifier.size(IconSizeMedium),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        // Activation overlay
                        if (isActivating) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .matchParentSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Activant títol...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }

                            // Handle activation
                            LaunchedEffect(Unit) {
                                withContext(Dispatchers.IO) {
                                    val updatedTitles = activateTitleApi(title.id, userTitlesList)
                                    activatingTitle = null

                                    // If both previous and updated titles have the same active title, no change
                                    val previousActiveTitle = userTitlesList.find { it.active }
                                    val updatedActiveTitle = updatedTitles.find { it.active }
                                    if (previousActiveTitle?.id == updatedActiveTitle?.id) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context,
                                                "No s'ha pogut activar el títol",
                                                Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    } else {
                                        userTitlesList = updatedTitles
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context,
                                                "S'ha activat el títol",
                                                Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add spacing between title cards
                    Spacer(modifier = Modifier.height(PaddingMedium))
                }

                // If empty, show message
                if (activeUserTitles.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(
                                bottom = PaddingMedium,
                                top = PaddingMedium,
                                start = 0.dp,
                                end = 0.dp
                            ),
                        text = "Cap títol disponible",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }


                // Expirated or used up user titles header
                Spacer(modifier = Modifier.height(PaddingMedium))
                Text(
                    modifier = Modifier
                        .fillMaxWidth() // Comment this line to center the text
                        .padding(
                            bottom = PaddingMedium,
                            top = PaddingMedium,
                            start = 0.dp,
                            end = 0.dp
                        ),
                    text = "Títols exahurits o caducats:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )


                // Dynamically list expired user titles
                expiredUserTitles.forEach { title ->
                    // Date format: DD-MM-YYYY
                    val expDateOriginal = title.expiration.take(10)
                    val expirationDate = "${expDateOriginal.substring(8, 10)}-${
                        expDateOriginal.substring(
                            5,
                            7
                        )
                    }-${expDateOriginal.substring(0, 4)}"

                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (title.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(CornerRadiusMedium)
                                )
                                .alpha(1f)
                                .padding(PaddingSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = PaddingMedium)
                            ) {
                                Text(
                                    text = title.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = title.description,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "Zones: ${title.num_zones} - Origen: ${title.zone_origin ?: "Pendent"}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "Caducat el: $expirationDate",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "Viatges que li restaven: ${title.uses_left ?: "Ilimitats"}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                            }

                            // Show activate button
                            //Spacer(modifier = Modifier.width(IconSizeMediumSmall))
                        }
                    }

                    // Add spacing between title cards
                    Spacer(modifier = Modifier.height(PaddingMedium))
                }

                // If empty, show message
                if (expiredUserTitles.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(
                                bottom = PaddingMedium,
                                top = PaddingMedium,
                                start = 0.dp,
                                end = 0.dp
                            ),
                        text = "Cap títol exahurit o caducat",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}