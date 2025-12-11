package cat.nilcm01.portam.titles

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import cat.nilcm01.portam.ui.values.BarTopHeight
import cat.nilcm01.portam.ui.values.BarTopInnerPadding
import cat.nilcm01.portam.ui.values.CornerRadiusMedium
import cat.nilcm01.portam.ui.values.IconSizeMediumSmall
import cat.nilcm01.portam.ui.values.PaddingLarge
import cat.nilcm01.portam.ui.values.PaddingMedium
import cat.nilcm01.portam.ui.values.PaddingSmall
import cat.nilcm01.portam.ui.values.PaddingXLarge
import cat.nilcm01.portam.ui.values.PaddingXXLarge
import cat.nilcm01.portam.utils.StorageManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
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
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private object AddSteps {
    const val Loading = 0
    const val List = 1
    const val ListError = -1
    const val Detail = 2
    const val Payment = 3
    const val Success = 10
    const val Error = 11
}

data class Title(
    val id: String,
    val name: String,
    val description: String,
    val uses: Int?,             // Number of uses (NULL = unlimited uses)
    val expiration: Int?,       // Days until expiration (NULL = no expiration)
    val price: Float,
    val num_zones: Int,
    val link: Int?,             // Minutes of free transfer (NULL = no free transfer)
    val re_entry: Int?          // Minutes of cooldown for re-entry (NULL = no re-entry allowed)
)

data class TitlesForUserApiResult(
    val success: Boolean,
    val user: String,
    val titles: List<Title>?
)

suspend fun getTitlesForUser(): TitlesForUserApiResult {
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
                            "${StorageManager.getUserData()["userId"]}/available"
                ) {
                    contentType(io.ktor.http.ContentType.Application.Json)
                }

            val responseBody = response.bodyAsText()
            val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

            client.close()

            // Return TitlesForUserApiResult
            val success = jsonResponse["success"]?.jsonPrimitive?.boolean ?: false
            val titlesJsonArray = jsonResponse["titles"]?.jsonArray
            val titles = titlesJsonArray?.map { titleJsonElement ->
                val titleObject = titleJsonElement.jsonObject
                Title(
                    id = titleObject["id"]?.jsonPrimitive?.content ?: "",
                    name = titleObject["name"]?.jsonPrimitive?.content ?: "",
                    description = titleObject["description"]?.jsonPrimitive?.content ?: "",
                    uses = titleObject["uses"]?.jsonPrimitive?.intOrNull,
                    expiration = titleObject["expiration"]?.jsonPrimitive?.intOrNull,
                    price = titleObject["price"]?.jsonPrimitive?.floatOrNull ?: 0.0f,
                    num_zones = titleObject["num_zones"]?.jsonPrimitive?.intOrNull ?: 0,
                    link = titleObject["link"]?.jsonPrimitive?.intOrNull,
                    re_entry = titleObject["re_entry"]?.jsonPrimitive?.intOrNull
                )
            }

            TitlesForUserApiResult(
                success = success,
                user = StorageManager.getUserData()["userId"] as String,
                titles = titles
            )
        } catch (e: Exception) {
            TitlesForUserApiResult(
                success = false,
                user = StorageManager.getUserData()["userId"] as String,
                titles = null
            )
        }
    }
}

suspend fun addTitleToUser(titleId: String): Boolean {
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

            // Make POST request
            val response: HttpResponse =
                client.post(
                    "https://portam-server.vercel.app/api/titles/user/" +
                            "${StorageManager.getUserData()["userId"]}"
                ) {
                    contentType(io.ktor.http.ContentType.Application.FormUrlEncoded)
                    setBody(
                        "title=$titleId"
                    )
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

suspend fun createReceipt(amount: Float): Boolean {
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

            // Make POST request
            val response: HttpResponse =
                client.post(
                    "https://portam-server.vercel.app/api/users/" +
                            "${StorageManager.getUserData()["userId"]}/receipts"
                ) {
                    contentType(io.ktor.http.ContentType.Application.FormUrlEncoded)
                    setBody(
                        "amount=$amount"
                    )
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
fun AddTitleScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(AddSteps.Loading) }
    var titles by remember { mutableStateOf<List<Title>>(emptyList()) }
    var selectedTitle by remember { mutableStateOf<Title?>(null) }
    val currentTime = remember { java.time.Instant.now().toString() }

    // If step is Loading, Payment, Success, Error or ListError, set as true. Otherwise, false.
    val centeredContent = when (step) {
        AddSteps.Loading, AddSteps.Payment, AddSteps.Success, AddSteps.Error, AddSteps.ListError -> true
        else -> false
    }

    // Clear the current selected title when returning to list
    fun clearSelectedTitle() {
        selectedTitle = null
    }

    fun handleBack() {
        when (step) {
            AddSteps.Loading, AddSteps.Payment -> {
                // Prevent back action while loading or payment
                return
            }
            AddSteps.Detail -> {
                // Go back to list
                clearSelectedTitle()
                step = AddSteps.List
            }
            AddSteps.List, AddSteps.ListError, AddSteps.Success, AddSteps.Error -> {
                // Exit screen
                onBack()
            }
        }
    }

    // Intercept system back / gesture and call the provided onBack lambda
    BackHandler(enabled = true) {
        handleBack()
    }

    // Load titles on first composition
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val result = getTitlesForUser()
            if (result.success) {
                titles = result.titles ?: emptyList()
                step = AddSteps.List
            } else {
                step = AddSteps.ListError
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
        // Header
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
                            handleBack()
                        }
                    )
                    .padding(horizontal = 0.dp, vertical = 0.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Torna enrere",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Nou títol",
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
            verticalArrangement = if (centeredContent) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Loading list
            if (step == AddSteps.Loading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Carregant els títols disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            // List error
            else if (step == AddSteps.ListError) {
                Text(
                    text = "S'ha produït un error en carregar els títols disponibles.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = FontStyle.Italic
                )
            }

            // Titles list
            else if (step == AddSteps.List) {
                // Header
                Text(
                    modifier = Modifier
                        .fillMaxWidth() // Comment this line to center the text
                        .padding(
                            bottom = PaddingMedium,
                            top = 0.dp,
                            start = 0.dp,
                            end = 0.dp
                        ),
                    text = "Títols disponibles:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(PaddingMedium))

                // Dynamically list all titles
                titles.forEach { title ->
                    // Price format: 00,00
                    var price = "%.2f".format(title.price)
                    price = price.replace('.', ',')
                    Box (
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    selectedTitle = title
                                    step = AddSteps.Detail
                                }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(CornerRadiusMedium)
                                )
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
                                Spacer(modifier = Modifier.height(PaddingSmall))
                                Text(
                                    text = "Zones: ${title.num_zones}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "Viatges: ${title.uses ?: "il·limitats"}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text =
                                        if (title.expiration != null)
                                             "Caducitat: ${title.expiration} dies"
                                        else "Sense caducitat",
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "${price} €",
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(PaddingMedium))
                }
            }

            // Title detail
            else if (step == AddSteps.Detail && selectedTitle != null) {
                // Price format: 00,00
                var price = "%.2f".format(selectedTitle!!.price)
                price = price.replace('.', ',')
                // Header
                Text(
                    modifier = Modifier
                        .fillMaxWidth() // Comment this line to center the text
                        .padding(
                            bottom = PaddingMedium,
                            top = 0.dp,
                            start = 0.dp,
                            end = 0.dp
                        ),
                    text = "Títol seleccionat:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(PaddingMedium))

                // Show the selected title details
                if (selectedTitle != null) {
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(CornerRadiusMedium)
                                )
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
                                    text = selectedTitle!!.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = selectedTitle!!.description,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Left
                                )
                                Spacer(modifier = Modifier.height(PaddingSmall))
                                Text(
                                    text = "Zones: ${selectedTitle!!.num_zones}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "Viatges: ${selectedTitle!!.uses ?: "il·limitats"}",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text =
                                        if (selectedTitle!!.expiration != null)
                                            "Caducitat: ${selectedTitle!!.expiration} dies"
                                        else "Sense caducitat",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "${price} €",
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(PaddingXLarge))

                    // Confirm button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(CornerRadiusMedium)
                            )
                            .clickable(
                                onClick = {
                                    step = AddSteps.Payment
                                }
                            )
                            .padding(PaddingMedium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_card_band),
                            contentDescription = "Compra aquest títol",
                            modifier = Modifier.size(IconSizeMediumSmall),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            "Compra aquest títol",
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // If selectedTitle is null (should not happen), show error
                else {
                    Text(
                        text = "S'ha produït un error en carregar els detalls del títol.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            // Payment processing
            else if (step == AddSteps.Payment) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Processant el pagament",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                // Handle payment in background
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        val titleId = selectedTitle!!.id
                        val titlePrice = selectedTitle!!.price
                        val result = addTitleToUser(titleId)
                        clearSelectedTitle()
                        step = if (result) {
                            val receiptResult = createReceipt(titlePrice)
                            AddSteps.Success
                        } else {
                            AddSteps.Error
                        }
                    }
                }

            }

            // Success
            else if (step == AddSteps.Success) {
                Text(
                    text = "El títol s'ha afegit correctament al teu compte.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(PaddingXXLarge))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(CornerRadiusMedium)
                        )
                        .clickable(
                            onClick = {
                                handleBack()
                            }
                        )
                        .padding(PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_transport_ticket_filled),
                        contentDescription = "Torna als teus títols",
                        modifier = Modifier.size(IconSizeMediumSmall),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "Torna als teus títols",
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Error
            else if (step == AddSteps.Error) {
                Text(
                    text = "S'ha produït un error en afegir el títol al teu compte.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.height(PaddingXXLarge))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(CornerRadiusMedium)
                        )
                        .clickable(
                            onClick = {
                                handleBack()
                            }
                        )
                        .padding(PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = cat.nilcm01.portam.R.drawable.icon_transport_ticket_filled),
                        contentDescription = "Torna als teus títols",
                        modifier = Modifier.size(IconSizeMediumSmall),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "Torna als teus títols",
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}