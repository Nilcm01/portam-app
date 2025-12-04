package cat.nilcm01.portam.profile

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.nilcm01.portam.ui.values.BarTopHeight
import cat.nilcm01.portam.ui.values.BarTopInnerPadding
import cat.nilcm01.portam.ui.values.IconSizeMediumSmall
import cat.nilcm01.portam.ui.values.PaddingLarge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import cat.nilcm01.portam.ui.theme.transparent
import cat.nilcm01.portam.ui.values.CornerRadiusMedium
import cat.nilcm01.portam.ui.values.CornerRadiusSmall
import cat.nilcm01.portam.ui.values.PaddingMedium
import cat.nilcm01.portam.ui.values.PaddingSmall
import cat.nilcm01.portam.ui.values.PaddingXLarge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private object SettingsSteps {
    const val Preloading = 0
    const val Ready = 1
    const val PreloadError = -1
    const val Saving = 10
    const val SaveError = -10
    const val JoiningGroup = 20
    const val JoinGroupError = -20
}

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

data class Group(
    var id: Int,
    var name: String,
    var description: String,
    var expiration: String
)

data class UserApiResult(
    var success: Boolean,
    var user: User
)

data class UserGroupsApiResult(
    var success: Boolean,
    var groups: List<Group>
)

data class UserUpdateApiResult(
    var success: Boolean,
    var error: String?
)

data class JoinGroupApiResult(
    var success: Boolean,
    var error: String?
)

fun getUserDataFromApi(): UserApiResult {
    // Placeholder implementation
    Thread.sleep(2000)
    return UserApiResult(
        success = true,
        user = User(
            id = 1,
            name = "John",
            surname = "Doe",
            gov_id = "123456789",
            email = "john@doe.com",
            phone = "123456789",
            birthdate = "1990-01-01",
            password = "hashed_password"
        )
    )
}

fun getUserGroupsFromApi(): UserGroupsApiResult {
    // Placeholder implementation
    Thread.sleep(2000)
    return UserGroupsApiResult(
        success = true,
        groups = listOf(
            Group(
                id = 1,
                name = "Group A",
                description = "Description for Group A",
                expiration = "2024-12-31"
            ),
            Group(
                id = 2,
                name = "Group B",
                description = "Description for Group B",
                expiration = "2025-06-30"
            )
        )
    )
}

fun getAvailableGroupsFromApi(): UserGroupsApiResult {
    // Placeholder implementation
    Thread.sleep(2000)
    return UserGroupsApiResult(
        success = true,
        groups = listOf(
            Group(
                id = 3,
                name = "Group C",
                description = "Description for Group C",
                expiration = "365"
            ),
            Group(
                id = 4,
                name = "Group D",
                description = "Description for Group D",
                expiration = "0"
            )
        )
    )

}

fun updateUserDataToApi(user: User): UserUpdateApiResult {
    // Placeholder implementation
    Thread.sleep(2000)
    return UserUpdateApiResult(
        success = true,
        error = null
    )
}

fun joinGroupToApi(groupId: Int): JoinGroupApiResult {
    // Placeholder implementation
    Thread.sleep(2000)
    return JoinGroupApiResult(
        success = true,
        error = null
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { }
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var step by remember { mutableStateOf(SettingsSteps.Preloading) }
    var user by remember { mutableStateOf(listOf<User>()) }
    var userGroups by remember { mutableStateOf(listOf<Group>()) }
    var availableGroups by remember { mutableStateOf(listOf<Group>()) }
    var userErrorMessage by remember { mutableStateOf("") }

    // Editable user data state
    var editedUser by remember { mutableStateOf(User(0, "", "", "", "", "", "", "")) }
    var passwordConfirm by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Group selection state
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var expandedGroupPicker by remember { mutableStateOf(false) }

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

    val isEmailValid = isValidEmail(editedUser.email)
    val isPhoneValid = isValidPhone(editedUser.phone)
    val isDateValid = isValidDate(editedUser.birthdate)
    val passwordsMatch = editedUser.password == passwordConfirm || passwordConfirm.isEmpty()

    // Intercept system back / gesture and call the provided onBack lambda
    BackHandler(enabled = true) {
        onBack()
    }

    // Load all data on first composition
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val resultUser = getUserDataFromApi()
            val resultUserGroups = getUserGroupsFromApi()
            val resultAvailableGroups = getAvailableGroupsFromApi()
            if (resultUser.success && resultUserGroups.success && resultAvailableGroups.success) {
                user = listOf(resultUser.user)
                userGroups = resultUserGroups.groups
                availableGroups = resultAvailableGroups.groups

                // Initialize edited user with loaded data
                editedUser = resultUser.user.copy()

                step = SettingsSteps.Ready
            } else {
                step = SettingsSteps.PreloadError
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
                text = "Dades personals",
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
            verticalArrangement =
                if (step == SettingsSteps.Preloading || step == SettingsSteps.PreloadError)
                    Arrangement.Center
                else
                    Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Preloading
            if (step == SettingsSteps.Preloading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Carregant totes les dades",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            // Preload error
            else if (step == SettingsSteps.PreloadError) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "S'ha produït un error en carregar les dades.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = FontStyle.Italic
                )
            }

            // Ready, Saving and SaveError
            else {
                // Saving (overlay)
                if (step == SettingsSteps.Saving) {
                    // Full screen overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Aplicant els canvis...",
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

                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            val result = updateUserDataToApi(editedUser)
                            if (result.success) {
                                user = listOf(editedUser.copy())
                                step = SettingsSteps.Ready
                            } else {
                                userErrorMessage = result.error ?: "Error desconegut"
                                Thread.sleep(5000)
                                step = SettingsSteps.SaveError
                            }
                        }
                    }
                }

                // Save error (overlay)
                else if (step == SettingsSteps.SaveError) {
                    // Full screen overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No s'han pogut desar els canvis:\n$userErrorMessage",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }

                //// User data display and edit

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
                    text = "Les teves dades personals",
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(PaddingMedium))

                // Name field
                OutlinedTextField(
                    value = editedUser.name,
                    onValueChange = { editedUser = editedUser.copy(name = it) },
                    label = { Text("Nom") },
                    singleLine = true,
                    enabled = step != SettingsSteps.Saving,
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
                    value = editedUser.surname,
                    onValueChange = { editedUser = editedUser.copy(surname = it) },
                    label = { Text("Cognoms") },
                    singleLine = true,
                    enabled = step != SettingsSteps.Saving,
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
                    value = editedUser.gov_id,
                    onValueChange = { editedUser = editedUser.copy(gov_id = it) },
                    label = { Text("DNI/NIE") },
                    singleLine = true,
                    enabled = step != SettingsSteps.Saving,
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
                    value = editedUser.email,
                    onValueChange = { editedUser = editedUser.copy(email = it) },
                    label = { Text("Correu electrònic") },
                    singleLine = true,
                    enabled = step != SettingsSteps.Saving,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = !isEmailValid && editedUser.email.isNotEmpty(),
                    supportingText = {
                        if (!isEmailValid && editedUser.email.isNotEmpty()) {
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
                    value = editedUser.phone,
                    onValueChange = { editedUser = editedUser.copy(phone = it) },
                    label = { Text("Telèfon") },
                    singleLine = true,
                    enabled = step != SettingsSteps.Saving,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = !isPhoneValid && editedUser.phone.isNotEmpty(),
                    supportingText = {
                        if (!isPhoneValid && editedUser.phone.isNotEmpty()) {
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
                        .clickable(enabled = step != SettingsSteps.Saving) {
                            showDatePicker = true
                        }
                ) {
                    OutlinedTextField(
                        value = editedUser.birthdate,
                        onValueChange = { },
                        label = { Text("Data de naixement") },
                        singleLine = true,
                        enabled = false,
                        readOnly = true,
                        isError = !isDateValid && editedUser.birthdate.isNotEmpty(),
                        supportingText = {
                            if (!isDateValid && editedUser.birthdate.isNotEmpty()) {
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
                    value = editedUser.password,
                    onValueChange = { editedUser = editedUser.copy(password = it) },
                    label = { Text("Clau de pas") },
                    singleLine = true,
                    enabled = step != SettingsSteps.Saving,
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
                    enabled = step != SettingsSteps.Saving,
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

                // Save button
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (step != SettingsSteps.Saving) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(CornerRadiusMedium)
                        )
                        .clickable(
                            enabled = step != SettingsSteps.Saving,
                            onClick = {
                                if (editedUser.name.isNotEmpty() &&
                                    editedUser.surname.isNotEmpty() &&
                                    editedUser.gov_id.isNotEmpty() &&
                                    editedUser.email.isNotEmpty() &&
                                    isEmailValid &&
                                    editedUser.phone.isNotEmpty() &&
                                    isPhoneValid &&
                                    editedUser.birthdate.isNotEmpty() &&
                                    isDateValid &&
                                    passwordsMatch) {
                                    step = SettingsSteps.Saving
                                }
                            }
                        )
                        .padding(PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Actualitza les dades",
                        color = if (step != SettingsSteps.Saving) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                                        editedUser = editedUser.copy(birthdate = selectedDate)
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

                //// UserGroups

                // Header text
                Spacer(modifier = Modifier.height(PaddingXLarge))
                Text(
                    modifier = Modifier
                        .fillMaxWidth() // Comment this line to center the text
                        .padding(
                            bottom = PaddingSmall,
                            top = PaddingSmall,
                            start = 0.dp,
                            end = 0.dp
                        ),
                    text = "Els teus grups bonificats:",
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(PaddingMedium))

                // Dynamically list user groups
                userGroups.forEach { group ->
                    // Date format origin: 2025-06-01T10:00:00Z
                    // Date format target: 01/06/2025
                    val formattedTimestamp = group.expiration.take(10)
                        .split("-")
                        .let { "${it[2]}/${it[1]}/${it[0]}" }

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
                                text = group.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Left
                            )
                            Text(
                                text = group.description,
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Left
                            )
                            Text(
                                text = "Caduca el: $formattedTimestamp",
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Left
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(PaddingMedium))
                }

                //// AvailableGroups

                // Header text
                Spacer(modifier = Modifier.height(PaddingXLarge))
                Text(
                    modifier = Modifier
                        .fillMaxWidth() // Comment this line to center the text
                        .padding(
                            bottom = PaddingSmall,
                            top = PaddingSmall,
                            start = 0.dp,
                            end = 0.dp
                        ),
                    text = "Sol·licita adherir-te a un grup:",
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(PaddingMedium))

                // Picker
                // Exposed dropdown menu for group selection
                ExposedDropdownMenuBox(
                    expanded = expandedGroupPicker,
                    onExpandedChange = {
                        if (step != SettingsSteps.JoiningGroup) {
                            expandedGroupPicker = !expandedGroupPicker
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedGroup?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        enabled = step != SettingsSteps.JoiningGroup,
                        label = { Text("Selecciona un grup") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGroupPicker)
                        },
                        colors = OutlinedTextFieldDefaults.colors(),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.transparent,
                                RoundedCornerShape(CornerRadiusSmall)
                            )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedGroupPicker,
                        onDismissRequest = { expandedGroupPicker = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableGroups.forEach { group ->
                            var expiration = "Vàlid durant ${group.expiration} dies"
                            if (group.expiration == "0") {
                                expiration = "Validesa pròpia"
                            }
                            DropdownMenuItem(
                                text = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = group.name,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Left
                                        )
                                        Text(
                                            text = group.description,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Left
                                        )
                                        Text(
                                            text = expiration,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Left
                                        )
                                    }
                                },
                                onClick = {
                                    selectedGroup = group
                                    expandedGroupPicker = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Save button
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (step != SettingsSteps.JoiningGroup && selectedGroup != null)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(CornerRadiusMedium)
                        )
                        .clickable(
                            enabled = step != SettingsSteps.JoiningGroup && selectedGroup != null,
                            onClick = {
                                selectedGroup?.let { group ->
                                    step = SettingsSteps.JoiningGroup
                                }
                            }
                        )
                        .padding(PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Sol·licita adhesió al grup",
                        color = if (step != SettingsSteps.JoiningGroup && selectedGroup != null)
                                MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Joining group (overlay)
                if (step == SettingsSteps.JoiningGroup) {
                    // Full screen overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Sol·licitant adhesió...",
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
                            selectedGroup?.let { group ->
                                val result = joinGroupToApi(group.id)
                                if (result.success) {
                                    // Reload groups data
                                    val resultUserGroups = getUserGroupsFromApi()
                                    val resultAvailableGroups = getAvailableGroupsFromApi()
                                    if (resultUserGroups.success && resultAvailableGroups.success) {
                                        userGroups = resultUserGroups.groups
                                        availableGroups = resultAvailableGroups.groups
                                        selectedGroup = null
                                    }
                                    step = SettingsSteps.Ready
                                } else {
                                    userErrorMessage = result.error ?: "Error desconegut"
                                    step = SettingsSteps.JoinGroupError
                                }
                            }
                        }
                    }
                }

                // Join group error (overlay)
                if (step == SettingsSteps.JoinGroupError) {
                    // Full screen overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No s'ha pogut adherir al grup:\n$userErrorMessage",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}