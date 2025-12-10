package cat.nilcm01.portam.cards

import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.MainActivity
import cat.nilcm01.portam.ui.theme.success
import cat.nilcm01.portam.ui.theme.transparent
import cat.nilcm01.portam.ui.values.*
import cat.nilcm01.portam.utils.StorageManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private object StepsAddSuport {
    const val Start = 1
    const val Processing = 2
    const val Success = 3
    const val Error = -1
}

class SuportApiResult(
    i_code: Int,
    i_success: Boolean,
    i_message: String,
    i_uid: String,
    i_suport: String,
    i_activation: String
) {
    val code: Int = i_code
    val success: Boolean = i_success
    val message: String = i_message
    val uid: String = i_uid
    val suport: String = i_suport
    val activation: String = i_activation
}

var suportApiResultGlobal: SuportApiResult? = null

// Call to the API to add suport
suspend fun addSuportApiCall(uid: String): SuportApiResult {
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
                    "https://portam-server.vercel.app/api/users/" +
                            "${StorageManager.getUserData()["userId"]}/suports"
                ) {
                    contentType(io.ktor.http.ContentType.Application.FormUrlEncoded)
                    setBody(
                        "uid=${uid}"
                    )
                }

            val responseBody = response.bodyAsText()
            val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

            client.close()

            // Return ApiResult
            val suport = jsonResponse["suport"]?.jsonObject
            SuportApiResult(
                i_code = response.status.value,
                i_success = jsonResponse["success"]?.jsonPrimitive?.boolean ?: false,
                i_message = jsonResponse["error"]?.jsonPrimitive?.content
                    ?: "Suport afegit correctament",
                i_uid = uid,
                i_suport = "",
                i_activation = suport?.get("activation")?.jsonPrimitive?.content ?: ""
            )
        } catch (e: Exception) {
            SuportApiResult(
                i_code = -1,
                i_success = false,
                i_message = "Error en afegir el suport: ${e.localizedMessage}",
                i_uid = "",
                i_suport = "",
                i_activation = ""
            )
        }
    }
}

@Composable
fun AddSuportScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    nfcTagUid: String? = null
) {
    var step by remember { mutableStateOf(StepsAddSuport.Start) }

    // Intercept system back / gesture and call the provided onBack lambda
    BackHandler(enabled = true) {
        if (step != StepsAddSuport.Processing) {
            onBack()
        }
    }

    // State to hold the UID
    var uid by remember { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as? MainActivity

    // Update uid when a new NFC tag is detected
    LaunchedEffect(nfcTagUid) {
        if (nfcTagUid != null) {
            uid = nfcTagUid
            // Show toast notification
            Toast.makeText(context, "Suport llegit", Toast.LENGTH_SHORT).show()
            // Consume the NFC tag UID so it's not used again
            activity?.consumeNfcTagUid()
        }
    }

    // Enable/Disable NFC foreground dispatch
    DisposableEffect(Unit) {
        val nfcHandler = activity?.getNfcHandler()
        nfcHandler?.enableForegroundDispatch()

        onDispose {
            nfcHandler?.disableForegroundDispatch()
        }
    }

    // Body
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
                text = "Afegeix un suport",
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

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(PaddingLarge),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Escriu el codi del suport o apropa'l a la part posterior del teu dispositiu per llegir-lo.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = uid,
                onValueChange = { uid = it },
                label = { Text("Codi del suport") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.transparent,
                        RoundedCornerShape(CornerRadiusSmall)
                    )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                    .clickable(
                        onClick = {
                            // If UID is empty, only contains spaces, do nothing
                            if (uid.trim().isEmpty() || step != StepsAddSuport.Start) {
                                return@clickable
                            }
                            // Move to processing step
                            step = StepsAddSuport.Processing
                        }
                    )
                    .padding(PaddingMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_add_circle),
                    contentDescription = "Afegeix aquest suport",
                    modifier = Modifier.size(IconSizeMediumSmall),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    "Afegeix aquest suport",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Only show if Step == Steps.Processing
            if (step == StepsAddSuport.Processing) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Afegint el suport, si us plau espera...",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                )
                // Loading indicator
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )

                // API function call
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        suportApiResultGlobal = addSuportApiCall(uid)
                    }
                    step = if (suportApiResultGlobal?.success == true) {
                        StepsAddSuport.Success
                    } else {
                        StepsAddSuport.Error
                    }
                }

            }

            // Only show if Step == Steps.Success
            else if (step == StepsAddSuport.Success) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Suport afegit correctament!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.success
                )
            }

            // Only show if Step == Steps.Error
            else if (step == StepsAddSuport.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "S'ha produït un error en afegir el suport." +
                            "\n\n" +
                            "Error ${suportApiResultGlobal?.code}: ${suportApiResultGlobal?.message}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}