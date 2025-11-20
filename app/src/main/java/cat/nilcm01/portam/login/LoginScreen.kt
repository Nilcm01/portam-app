package cat.nilcm01.portam.login

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.nilcm01.portam.ui.theme.success
import cat.nilcm01.portam.ui.theme.transparent
import cat.nilcm01.portam.ui.values.CornerRadiusMedium
import cat.nilcm01.portam.ui.values.CornerRadiusSmall
import cat.nilcm01.portam.ui.values.PaddingLarge
import cat.nilcm01.portam.ui.values.PaddingMedium
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private object Steps {
    const val Loading = 0
    const val Start = 1
    const val Processing = 2
    const val Success = 3
    const val Error = -1
}

class ApiResult(
    val code: Int,
    val success: Boolean,
    val message: Map<String,String>,
    val user: Int?,
    val token: String?,
    val expires: String?
)

var loginApiResult: ApiResult? = null
var registerApiResult: ApiResult? = null

data class UserLocal(
    var id: Int,
    var token: String,
    var name: String,
    var surname: String,
    var email: String,
)

data class User(
    var id: Int,
    var name: String,
    var surname: String,
    var gov_id: String,
    var email: String,
    var phone: String,
    var birthdate: String,
    var password: String
)

data class UserLogin(
    var email: String,
    var password: String
)

// Call to the API to login
fun loginApiCall(user: UserLogin): ApiResult {
    // Simulate API call delay
    Thread.sleep(3000)
    // TODO: Implement API call to login
    return ApiResult(
        200,
        true,
        mapOf(
            "ca" to "Inici de sessió correcte",
            "es" to "Inicio de sesión correcto",
            "en" to "Login successful"
        ),
        1234567890,
        "abcdef1234567890",
        "2025-12-31H23:59:59"
    )
}

// Call to the API to register
fun registerApiCall(): ApiResult {
    // Simulate API call delay
    Thread.sleep(3000)
    // TODO: Implement API call to register
    return ApiResult(
        402,
        false,
        mapOf(
            "ca" to "Aquest correu electrònic ja està registrat",
            "es" to "Este correo electrónico ya está registrado",
            "en" to "This email is already registered"
        ),
        null,
        null,
        null
    )
}


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess : () -> Unit = {}
) {
    var showRegister by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf(Steps.Start) }

    // Intercept system back / gesture and call the provided onBack lambda
    BackHandler(enabled = true) {
        if (showRegister) {
            showRegister = false
        }
    }

    // State variables
    var userLogin by remember { mutableStateOf(UserLogin("", "")) }

    // Email validation function
    fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()) return true // Don't show error for empty field
        val emailRegex = "^[a-zA-Z0-9][a-zA-Z0-9._-]*@[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    val isEmailValid = isValidEmail(userLogin.email)
    var loginEmpty by remember { mutableStateOf(false) }

    // View
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingLarge)
            .verticalScroll(
                rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp, 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Porta'm",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // LOADING
        // Show loading indicator
        if (step == Steps.Loading) {
            // Full height column with loading indicator
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PaddingLarge)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // LOGIN / REGISTER FORM
        else {
            // Showing the login form
            if (!showRegister) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Main login form content
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Subtitle
                        Text(
                            text = "Inicia la sessió al teu compte",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Email input field
                        OutlinedTextField(
                            value = userLogin.email,
                            onValueChange = { userLogin = userLogin.copy(email = it) },
                            label = { Text("Correu electrònic") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = !isEmailValid && userLogin.email.isNotEmpty(),
                            supportingText = {
                                if (!isEmailValid && userLogin.email.isNotEmpty()) {
                                    Text(
                                        text = "Format de correu incorrecte.\nExemple: nom.cognom@domini.com",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.transparent,
                                    RoundedCornerShape(CornerRadiusSmall)
                                )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = userLogin.password,
                            onValueChange = { userLogin = userLogin.copy(password = it) },
                            label = { Text("Clau de pas") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.transparent,
                                    RoundedCornerShape(CornerRadiusSmall)
                                )
                        )
                        // Login button
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (step != Steps.Processing) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    RoundedCornerShape(CornerRadiusMedium)
                                )
                                .clickable(
                                    enabled = step != Steps.Processing,
                                    onClick = {
                                        if (userLogin.email.isNotEmpty() &&
                                            isEmailValid &&
                                            userLogin.password.isNotEmpty()) {
                                            step = Steps.Processing
                                            loginEmpty = false
                                        } else {
                                            loginEmpty = true
                                        }
                                    }
                                )
                                .padding(PaddingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Inicia la sessió",
                                color = if (step != Steps.Processing) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        //// MESSAGES

                        // Only show if Error
                        if (step == Steps.Error) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "S'ha produït un error en iniciar la sessió." +
                                        "\n\n" +
                                        "Error ${loginApiResult?.code}: ${loginApiResult?.message?.get("ca")}",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error,
                                fontStyle = FontStyle.Italic
                            )
                        }

                        // Only show if login is empty fields
                        if (loginEmpty) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "Cal completar tots els camps",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error,
                                fontStyle = FontStyle.Italic
                            )
                        }

                        //// END MESSAGES

                        // Register button
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "No tens compte?",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (step != Steps.Processing) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    RoundedCornerShape(CornerRadiusMedium)
                                )
                                .clickable(
                                    enabled = step != Steps.Processing,
                                    onClick = {
                                        showRegister = true
                                        step = Steps.Start
                                    }
                                )
                                .padding(PaddingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Registra't",
                                color = if (step != Steps.Processing) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Processing overlay - shown on top when processing
                    if (step == Steps.Processing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .matchParentSize()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Iniciant la sessió...",
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
                        }

                        // API function call
                        LaunchedEffect(Unit) {
                            withContext(Dispatchers.IO) {
                                loginApiResult = loginApiCall(userLogin)
                            }
                            if (loginApiResult?.success == true) {
                                step = Steps.Success
                                onLoginSuccess()
                            } else {
                                step = Steps.Error
                            }
                        }
                    }
                }
            }

            // Showing the registration form
            else {
                // TODO: Implement registration form
            }
        }
    }
}