package cat.nilcm01.portam.profile

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
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
import cat.nilcm01.portam.ui.values.PaddingSmall
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
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private object ReceiptsSteps {
    const val Loading = 0
    const val Success = 1
    const val Error = 2
}

data class Receipt(
    val id: Int,
    val timestamp: String,  // ISO 8601 format
    val amount: Float
)

data class ReceiptApiResult(
    val success: Boolean,
    val user_id: String,
    val receipts: List<Receipt>
)

suspend fun getReceiptsFromApi(): ReceiptApiResult {
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
                            "${StorageManager.getUserData()["userId"]}/receipts/"
                ) {
                    contentType(io.ktor.http.ContentType.Application.Json)
                }

            val responseBody = response.bodyAsText()
            val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

            client.close()

            // Return ApiResult
            val receipts = jsonResponse["receipts"]?.jsonArray
            ReceiptApiResult(
                success = jsonResponse["success"]?.jsonPrimitive?.boolean ?: false,
                user_id = jsonResponse["user_id"]?.jsonPrimitive?.content ?: "",
                receipts = receipts?.map { receiptJsonElement ->
                    val validationObject = receiptJsonElement.jsonObject
                    Receipt(
                        id = validationObject["id"]?.jsonPrimitive?.int ?: 0,
                        timestamp = validationObject["timestamp"]?.jsonPrimitive?.content ?: "",
                        amount = validationObject["amount"]?.jsonPrimitive?.float ?: 0.0f
                    )
                } ?: emptyList()
            )
        } catch (e: Exception) {
            ReceiptApiResult(
                success = false,
                user_id = StorageManager.getUserData().get("userId") as String,
                receipts = emptyList()
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ReceiptsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { }
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(ReceiptsSteps.Loading) }
    var receipts by remember { mutableStateOf(listOf<Receipt>()) }

    // Intercept system back / gesture and call the provided onBack lambda
    BackHandler(enabled = true) {
        onBack()
    }

    // Load receipts on first composition
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val result = getReceiptsFromApi()
            if (result.success) {
                receipts = result.receipts

                // Order validations by most recent first
                receipts = receipts.sortedByDescending { it.timestamp }

                step = ReceiptsSteps.Success
            } else {
                step = ReceiptsSteps.Error
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
                text = "Factures",
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

        // Scrollable content area for receipts
        Column(
            modifier = Modifier
                .weight(1f) // constrain to remaining space so verticalScroll works
                .verticalScroll(rememberScrollState())
                .padding(PaddingLarge),
            verticalArrangement = if (step != ReceiptsSteps.Success) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Loading state
            if (step == ReceiptsSteps.Loading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Carregant les factures",
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
            else if (step == ReceiptsSteps.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "S'ha produït un error en carregar les factures.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = FontStyle.Italic
                )
            }

            // Success state
            else if (step == ReceiptsSteps.Success) {
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
                    text = "Ordre: més recent primer",
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(PaddingMedium))

                // If no receipts available, show message
                if (receipts.isEmpty()) {
                    Spacer(modifier = Modifier.height(PaddingMedium))
                    Text(
                        text = "No hi ha factures disponibles.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontStyle = FontStyle.Italic
                    )
                }

                // Dynamically list all receipts
                receipts.forEach { receipt ->
                    // Date format origin: 2025-06-01T10:00:00Z
                    // Date format target: 01/06/2025 - 10:00
                    val dateParts = receipt.timestamp.split("T")
                    val date = dateParts[0].split("-").reversed().joinToString("/")
                    val time = dateParts[1].removeSuffix("Z").substring(0,5)
                    val formattedTimestamp = "$date - $time"

                    // Amount format: 49.99 -> 49,99 €
                    val formattedAmount = String
                        .format("%.2f", receipt.amount)
                        .replace('.', ',')

                    Box {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(CornerRadiusMedium)
                                )
                                .padding(PaddingMedium),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Factura nº: ${receipt.id}",
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Left
                            )
                            Text(
                                text = "Data: $formattedTimestamp",
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Left
                            )
                            Text(
                                text = "Import: $formattedAmount €",
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Left
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(PaddingMedium))
                }
            }
        }
    }
}