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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
fun registerApiCall(user: User): ApiResult {
    // Simulate API call delay
    Thread.sleep(3000)
    // TODO: Implement API call to register
    return ApiResult(
        200,
        true,
        mapOf(
            "ca" to "Registre correcte",
            "es" to "Registro correcto",
            "en" to "Registration successful"
        ),
        1234567890,
        "abcdef1234567890",
        "2025-12-31H23:59:59"
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess : () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
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

    // Registration state variables
    var userRegister by remember { mutableStateOf(User(0, "", "", "", "", "", "", "")) }
    var passwordConfirm by remember { mutableStateOf("") }
    var registerEmpty by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Email validation function
    fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()) return true // Don't show error for empty field
        val emailRegex = "^[a-zA-Z0-9][a-zA-Z0-9._-]*@[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    // Phone validation function
    fun isValidPhone(phone: String): Boolean {
        if (phone.isEmpty()) return true // Don't show error for empty field
        val phoneRegex = "^[0-9]{9,15}$".toRegex()
        return phoneRegex.matches(phone)
    }

    // Date validation function (YYYY-MM-DD)
    fun isValidDate(date: String): Boolean {
        if (date.isEmpty()) return true // Don't show error for empty field
        val dateRegex = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$".toRegex()
        return dateRegex.matches(date)
    }

    val isEmailValid = isValidEmail(if (showRegister) userRegister.email else userLogin.email)
    val isPhoneValid = isValidPhone(userRegister.phone)
    val isDateValid = isValidDate(userRegister.birthdate)
    val passwordsMatch = userRegister.password == passwordConfirm || passwordConfirm.isEmpty()

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
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
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
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
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
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Main registration form content
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Subtitle
                        Text(
                            text = "Crea el teu compte",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Name field
                        OutlinedTextField(
                            value = userRegister.name,
                            onValueChange = { userRegister = userRegister.copy(name = it) },
                            label = { Text("Nom") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            supportingText = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.transparent,
                                    RoundedCornerShape(CornerRadiusSmall)
                                )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Surname field
                        OutlinedTextField(
                            value = userRegister.surname,
                            onValueChange = { userRegister = userRegister.copy(surname = it) },
                            label = { Text("Cognoms") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            supportingText = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.transparent,
                                    RoundedCornerShape(CornerRadiusSmall)
                                )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Government ID field
                        OutlinedTextField(
                            value = userRegister.gov_id,
                            onValueChange = { userRegister = userRegister.copy(gov_id = it) },
                            label = { Text("DNI/NIE") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            supportingText = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.transparent,
                                    RoundedCornerShape(CornerRadiusSmall)
                                )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Email field
                        OutlinedTextField(
                            value = userRegister.email,
                            onValueChange = { userRegister = userRegister.copy(email = it) },
                            label = { Text("Correu electrònic") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = !isEmailValid && userRegister.email.isNotEmpty(),
                            supportingText = {
                                if (!isEmailValid && userRegister.email.isNotEmpty()) {
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

                        // Phone field
                        OutlinedTextField(
                            value = userRegister.phone,
                            onValueChange = { userRegister = userRegister.copy(phone = it) },
                            label = { Text("Telèfon") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = !isPhoneValid && userRegister.phone.isNotEmpty(),
                            supportingText = {
                                if (!isPhoneValid && userRegister.phone.isNotEmpty()) {
                                    Text(
                                        text = "El telèfon ha de tenir entre 9 i 15 dígits",
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

                        // Birthdate field with date picker
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = step != Steps.Processing) {
                                    showDatePicker = true
                                }
                        ) {
                            OutlinedTextField(
                                value = userRegister.birthdate,
                                onValueChange = { },
                                label = { Text("Data de naixement") },
                                singleLine = true,
                                enabled = false,
                                readOnly = true,
                                isError = !isDateValid && userRegister.birthdate.isNotEmpty(),
                                supportingText = {
                                    if (!isDateValid && userRegister.birthdate.isNotEmpty()) {
                                        Text(
                                            text = "Format de data incorrecte.\nExemple: 1990-12-31",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.transparent,
                                        RoundedCornerShape(CornerRadiusSmall)
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Password field
                        OutlinedTextField(
                            value = userRegister.password,
                            onValueChange = { userRegister = userRegister.copy(password = it) },
                            label = { Text("Clau de pas") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            supportingText = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.transparent,
                                    RoundedCornerShape(CornerRadiusSmall)
                                )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Password confirmation field
                        OutlinedTextField(
                            value = passwordConfirm,
                            onValueChange = { passwordConfirm = it },
                            label = { Text("Confirma la clau de pas") },
                            singleLine = true,
                            enabled = step != Steps.Processing,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            isError = !passwordsMatch && passwordConfirm.isNotEmpty(),
                            supportingText = {
                                if (!passwordsMatch && passwordConfirm.isNotEmpty()) {
                                    Text(
                                        text = "Les claus de pas no coincideixen",
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

                        // Register button
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
                                        if (userRegister.name.isNotEmpty() &&
                                            userRegister.surname.isNotEmpty() &&
                                            userRegister.gov_id.isNotEmpty() &&
                                            userRegister.email.isNotEmpty() &&
                                            isEmailValid &&
                                            userRegister.phone.isNotEmpty() &&
                                            isPhoneValid &&
                                            userRegister.birthdate.isNotEmpty() &&
                                            isDateValid &&
                                            userRegister.password.isNotEmpty() &&
                                            passwordConfirm.isNotEmpty() &&
                                            passwordsMatch) {
                                            step = Steps.Processing
                                            registerEmpty = false
                                        } else {
                                            registerEmpty = true
                                        }
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

                        //// MESSAGES

                        // Only show if Error
                        if (step == Steps.Error) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "S'ha produït un error en registrar-te." +
                                        "\n\n" +
                                        "Error ${registerApiResult?.code}: ${registerApiResult?.message?.get("ca")}",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error,
                                fontStyle = FontStyle.Italic
                            )
                        }

                        // Only show if register is empty fields
                        if (registerEmpty) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "Cal completar tots els camps correctament",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error,
                                fontStyle = FontStyle.Italic
                            )
                        }

                        //// END MESSAGES

                        // Back to login button
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "Ja tens compte?",
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
                                        showRegister = false
                                        step = Steps.Start
                                    }
                                )
                                .padding(PaddingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Torna a l'inici de sessió",
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
                                    text = "Registrant el teu compte...",
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
                                registerApiResult = registerApiCall(userRegister)
                            }
                            if (registerApiResult?.success == true) {
                                onLoginSuccess()
                            } else {
                                step = Steps.Error
                            }
                        }
                    }
                }

                // Date Picker Dialog
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        val selectedDate = dateFormat.format(Date(millis))
                                        userRegister = userRegister.copy(birthdate = selectedDate)
                                    }
                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel·la")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
        }
    }
}