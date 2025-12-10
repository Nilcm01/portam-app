package cat.nilcm01.portam.cards

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.ui.theme.ColorRed
import cat.nilcm01.portam.ui.values.*
import cat.nilcm01.portam.utils.StorageManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private object Steps {
    const val Loading = 1
    const val Success = 2
    const val Error = -1
}

data class Suport(
    val uid: String,
    val user: String,
    val activation: String,
    val info: String
)

data class ApiResult(
    val success: Boolean,
    val user_id: String,
    val suports: List<Suport>
)

var suports: List<Suport>? = null

suspend fun getSuportsApi(): ApiResult {
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
                    "https://portam-server.vercel.app/api/users/" +
                            "${StorageManager.getUserData()["userId"]}/suports"
                ) {
                    contentType(io.ktor.http.ContentType.Application.Json)
                }

            val responseBody = response.bodyAsText()
            val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

            client.close()

            // Return ApiResult
            val suports = jsonResponse["suports"]?.jsonArray
            ApiResult(
                success = jsonResponse["success"]?.jsonPrimitive?.boolean ?: false,
                user_id = jsonResponse["user_id"]?.jsonPrimitive?.content ?: "",
                suports = suports?.map { suportJsonElement ->
                    val suportObject = suportJsonElement.jsonObject
                    val info =
                        if (suportObject["info"]?.jsonPrimitive?.content == "" ||
                            suportObject["info"]?.jsonPrimitive?.content == null ||
                            suportObject["info"]?.jsonPrimitive?.content.equals("null")) {
                            "Targeta"
                        } else {
                            suportObject["info"]?.jsonPrimitive?.content ?: "Targeta"
                        }
                    Suport(
                        user = suportObject["user"]?.jsonPrimitive?.content ?: "",
                        activation = suportObject["activation"]?.jsonPrimitive?.content ?: "",
                        uid = suportObject["uid"]?.jsonPrimitive?.content ?: "",
                        info = info,
                    )
                } ?: emptyList()
            )
        } catch (e: Exception) {
            ApiResult(
                success = false,
                user_id = StorageManager.getUserData().get("userId") as String,
                suports = emptyList()
            )
        }
    }
}

suspend fun deleteSuportApi(uid: String): Boolean {
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
                client.delete(
                    "https://portam-server.vercel.app/api/users/" +
                            "${StorageManager.getUserData()["userId"]}/suports/" +
                            uid
                ) {
                    contentType(io.ktor.http.ContentType.Application.Json)
                }

            val responseBody = response.bodyAsText()
            val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

            client.close()

            // Return boolean
            return@withContext jsonResponse["success"]?.jsonPrimitive?.boolean ?: false
        } catch (e: Exception) {
            return@withContext false
        }
    }
}

@Composable
fun SuportsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNavigateToAddSuport: () -> Unit = {}
) {
    var step by remember { mutableStateOf(Steps.Loading) }
    var suportsList by remember { mutableStateOf<List<Suport>>(emptyList()) }
    var deletingUid by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val deviceId = StorageManager.getDeviceId()

    // Intercept system back / gesture and call the provided onBack lambda
    BackHandler(enabled = true) {
        onBack()
    }

    // Load suports on first composition
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val result = getSuportsApi()
            if (result.success) {
                suportsList = result.suports
                suports = result.suports
                step = Steps.Success
            } else {
                step = Steps.Error
            }
        }
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
            verticalArrangement = if (step == Steps.Loading) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Loading state
            if (step == Steps.Loading) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Carregant els suports",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // Error state
            else if (step == Steps.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "S'ha produït un error en carregar els suports.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = FontStyle.Italic
                )
            }
            // Success state
            else if (step == Steps.Success) {
                // Sort suports: current device's virtual card first, then others
                val sortedSuports = suportsList.sortedWith(compareBy {
                    when {
                        it.info.startsWith("spvirtual::") && it.info.substringAfter("spvirtual::") == deviceId -> 0
                        else -> 1
                    }
                })

                // Add suport button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(CornerRadiusMedium)
                        )
                        .clickable(
                            onClick = {
                                onNavigateToAddSuport()
                            }
                        )
                        .padding(PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_add_circle),
                        contentDescription = "Afegeix un nou suport",
                        modifier = Modifier.size(IconSizeMediumSmall),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "Afegeix un nou suport",
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(PaddingMedium))

                // Dynamically generate suport items
                sortedSuports.forEachIndexed { index, suport ->
                    val isDeleting = deletingUid == suport.uid

                    // Check if it's a virtual card and parse device info
                    val isVirtualCard = suport.info.startsWith("spvirtual::")
                    val virtualDeviceId = if (isVirtualCard) suport.info.substringAfter("spvirtual::") else null
                    val isCurrentDevice = isVirtualCard && virtualDeviceId == deviceId

                    // Determine display name
                    val displayName = when {
                        isCurrentDevice -> "Targeta virtual:\naquest dispositiu"
                        isVirtualCard -> "Targeta virtual:\n$virtualDeviceId"
                        else -> suport.info.replace(": ", ":\n")
                    }

                    // Can delete if it's not a virtual card OR if it's a virtual card from another device
                    val canDelete = !isVirtualCard || !isCurrentDevice

                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(CornerRadiusMedium)
                                )
                                .alpha(if (isDeleting) 0.2f else 1f)
                                .padding(PaddingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Determine icon based on card type
                            val iconRes = if (isVirtualCard) {
                                cat.nilcm01.portam.R.drawable.icon_mobile_cast
                            } else {
                                cat.nilcm01.portam.R.drawable.icon_card_band
                            }

                            // From YYYY-MM-DDThh:mm:ss to DD-MM-YYYY
                            val activationDate = "" +
                                    suport.activation.subSequence(8, 10) + "-" +
                                    suport.activation.subSequence(5, 7) + "-" +
                                    suport.activation.subSequence(0, 4)

                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = displayName,
                                modifier = Modifier.size(IconSizeMediumSmall),
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier.weight(1f).padding(horizontal = PaddingMedium)
                            ) {
                                Text(
                                    displayName,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    "UID: ${suport.uid}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    "Actiu des de: $activationDate",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                            }

                            // Show delete button only for cards that can be deleted
                            if (canDelete) {
                                IconButton(
                                    onClick = {
                                        if (!isDeleting) {
                                            deletingUid = suport.uid
                                        }
                                    },
                                    enabled = !isDeleting
                                ) {
                                    Icon(
                                        painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_trash_cross),
                                        contentDescription = "Elimina targeta",
                                        modifier = Modifier.size(IconSizeMediumSmall),
                                        tint = ColorRed
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(IconSizeMediumSmall))
                            }
                        }

                        // Deletion overlay
                        if (isDeleting) {
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
                                        text = "Eliminant suport",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }

                            // Handle deletion
                            LaunchedEffect(Unit) {
                                val success = deleteSuportApi(suport.uid)
                                if (success) {
                                    // Remove from list
                                    suportsList = suportsList.filter { it.uid != suport.uid }
                                    deletingUid = null
                                    // Show toast on Main thread
                                    Toast.makeText(context, "S'ha eliminat el suport", Toast.LENGTH_SHORT).show()
                                } else {
                                    deletingUid = null
                                    // Show error toast on Main thread
                                    Toast.makeText(context, "No s'ha pogut eliminar el suport", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }

                    if (index < sortedSuports.size - 1) {
                        Spacer(modifier = Modifier.height(PaddingMedium))
                    }
                }
            }
        }
    }
}