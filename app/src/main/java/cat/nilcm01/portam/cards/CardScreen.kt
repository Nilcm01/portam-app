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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
import cat.nilcm01.portam.utils.StorageManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private object CardSteps {
    const val Loading = 0
    const val Success = 1
    const val Error = -1
}

data class ActiveUserTitle(
    val success: Boolean,
    val id: String?,
    val user: String?,
    val name: String?,
    val zone_origin: Int?,
    val uses_left: Int?,
    val expiration: String?,
    val error: String? = null
)

var activeUserTitle: ActiveUserTitle? = null

suspend fun getActiveUserTitleApiCall(): ActiveUserTitle {
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
                            "${StorageManager.getUserData()["userId"]}/active"
                ) {
                    contentType(io.ktor.http.ContentType.Application.Json)
                }

            val responseBody = response.bodyAsText()
            val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

            client.close()

            // Return ActiveUserTitle
            val success = jsonResponse["success"]?.jsonPrimitive?.boolean ?: false
            val title = jsonResponse["title"]?.jsonObject
            ActiveUserTitle(
                success = success,
                id = title?.get("id")?.jsonPrimitive?.content,
                user = title?.get("user")?.jsonPrimitive?.content,
                name = title?.get("title_name")?.jsonPrimitive?.content,
                zone_origin = title?.get("zone_origin")?.jsonPrimitive?.intOrNull,
                uses_left = title?.get("uses_left")?.jsonPrimitive?.intOrNull,
                expiration = title?.get("expiration")?.jsonPrimitive?.content
            )
        } catch (e: Exception) {
            ActiveUserTitle(
                success = false,
                id = null,
                user = null,
                name = null,
                zone_origin = null,
                uses_left = null,
                expiration = null,
                error = e.message
            )
        }
    }
}


@Composable
fun CardScreen(
    modifier: Modifier = Modifier,
    onNavigateToSuports: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var step by remember { mutableStateOf(CardSteps.Loading) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = PaddingLarge, vertical = PaddingMedium),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
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

            val user = StorageManager.getUserData()

            Text(
                "" + user["name"] + "\n" + user["surname"],
                modifier = Modifier
                    .padding(
                        horizontal = PaddingMedium,
                        vertical = PaddingSmall
                    )
                    .fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Column(
                modifier = Modifier
                    .padding(
                        horizontal = PaddingMedium,
                        vertical = PaddingSmall
                    )
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = if (step == CardSteps.Loading) Alignment.CenterHorizontally else Alignment.Start
            ) {
                Text(
                    "Títol actiu:",
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxWidth(),
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                if (step == CardSteps.Loading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = PaddingLarge)
                            .size(24.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                        strokeWidth = 2.dp
                    )

                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            val active = getActiveUserTitleApiCall()
                            activeUserTitle = active
                            step = if (active.success && active.name != null) {
                                CardSteps.Success
                            } else {
                                CardSteps.Error
                            }
                        }
                    }
                }

                else if (step == CardSteps.Error) {
                    Text(
                        "Error en carregar el títol actiu.",
                        modifier = Modifier
                            .padding(top = PaddingSmall)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                    Text(
                        "" + activeUserTitle?.error,
                        modifier = Modifier
                            .padding(top = PaddingSmall)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }

                else if (step == CardSteps.Success && activeUserTitle != null) {
                    Text(
                        "" + activeUserTitle?.name,
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    val zone = if (activeUserTitle?.zone_origin == null)
                        "pendent"
                    else
                        activeUserTitle?.zone_origin
                    Text(
                        "Zona d'origen: $zone",
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        "" +
                                if (activeUserTitle?.uses_left == null) {
                                    "Viatges il·limitats"
                                } else {
                                    "Queden: " + activeUserTitle?.uses_left + " viatges"
                                },
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    // Expiration date format, from YYYY-MM-DDThh:mm:ss to DD/MM/YYYY
                    val expirationDate = activeUserTitle?.expiration?.split("T")?.get(0)
                    val formattedDate = expirationDate?.split("-")?.let {
                        if (it.size == 3) {
                            "${it[2]}/${it[1]}/${it[0]}"
                        } else {
                            expirationDate
                        }
                    }
                    Text(
                        "Vàlid fins el: $formattedDate",
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        Column(
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