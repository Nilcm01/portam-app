package cat.nilcm01.portam.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


object StorageManager {

    private const val SECURE_PREFS_NAME = "portam_secure_preferences"
    private const val DATASTORE_NAME = "portam_datastore"

    @Volatile
    internal lateinit var appContext: Context

    // Initialize StorageManager with context (call this once in Application.onCreate())
    fun initialize(context: Context) {
        if (!::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }

    // Encrypted SharedPreferences for sensitive data
    private val securePrefs: SharedPreferences by lazy {
        checkInitialized()
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            appContext,
            SECURE_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // DataStore extension property
    internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = DATASTORE_NAME
    )

    private fun checkInitialized() {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("StorageManager must be initialized with a Context before use. Call StorageManager.initialize(context) first.")
        }
    }


    //// SECURE PREFERENCES (Sensitive data - encrypted)


    // String
    fun saveSecureString(key: String, value: String) {
        checkInitialized()
        securePrefs.edit().putString(key, value).apply()
    }

    fun getSecureString(key: String, defaultValue: String? = null): String? {
        checkInitialized()
        return securePrefs.getString(key, defaultValue)
    }

    // Bool

    fun saveSecureBoolean(key: String, value: Boolean) {
        checkInitialized()
        securePrefs.edit().putBoolean(key, value).apply()
    }

    fun getSecureBoolean(key: String, defaultValue: Boolean = false): Boolean {
        checkInitialized()
        return securePrefs.getBoolean(key, defaultValue)
    }

    // Int

    fun saveSecureInt(key: String, value: Int) {
        checkInitialized()
        securePrefs.edit().putInt(key, value).apply()
    }

    fun getSecureInt(key: String, defaultValue: Int = 0): Int {
        checkInitialized()
        return securePrefs.getInt(key, defaultValue)
    }

    // Long

    fun saveSecureLong(key: String, value: Long) {
        checkInitialized()
        securePrefs.edit().putLong(key, value).apply()
    }

    fun getSecureLong(key: String, defaultValue: Long = 0L): Long {
        checkInitialized()
        return securePrefs.getLong(key, defaultValue)
    }

    // Float

    fun saveSecureFloat(key: String, value: Float) {
        checkInitialized()
        securePrefs.edit().putFloat(key, value).apply()
    }

    fun getSecureFloat(key: String, defaultValue: Float = 0f): Float {
        checkInitialized()
        return securePrefs.getFloat(key, defaultValue)
    }

    // General

    fun removeSecure(key: String) {
        checkInitialized()
        securePrefs.edit().remove(key).apply()
    }

    fun clearAllSecure() {
        checkInitialized()
        securePrefs.edit().clear().apply()
    }

    fun containsSecure(key: String): Boolean {
        checkInitialized()
        return securePrefs.contains(key)
    }


    //// DATASTORE (Modern Flow-based preferences)


    // Save String to DataStore
    suspend fun saveStringToDataStore(key: String, value: String) {
        checkInitialized()
        appContext.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    // Get String from DataStore as Flow
    fun getStringFromDataStoreFlow(key: String, defaultValue: String = ""): Flow<String> {
        checkInitialized()
        return appContext.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: defaultValue
        }
    }

    // Get String from DataStore synchronously
    suspend fun getStringFromDataStore(key: String, defaultValue: String = ""): String {
        return getStringFromDataStoreFlow(key, defaultValue).first()
    }

    // Save Int to DataStore
    suspend fun saveIntToDataStore(key: String, value: Int) {
        checkInitialized()
        appContext.dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    // Get Int from DataStore as Flow
    fun getIntFromDataStoreFlow(key: String, defaultValue: Int = 0): Flow<Int> {
        checkInitialized()
        return appContext.dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)] ?: defaultValue
        }
    }

    // Get Int from DataStore synchronously
    suspend fun getIntFromDataStore(key: String, defaultValue: Int = 0): Int {
        return getIntFromDataStoreFlow(key, defaultValue).first()
    }

    // Save Boolean to DataStore
    suspend fun saveBooleanToDataStore(key: String, value: Boolean) {
        checkInitialized()
        appContext.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    // Get Boolean from DataStore as Flow
    fun getBooleanFromDataStoreFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        checkInitialized()
        return appContext.dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: defaultValue
        }
    }

    // Get Boolean from DataStore synchronously
    suspend fun getBooleanFromDataStore(key: String, defaultValue: Boolean = false): Boolean {
        return getBooleanFromDataStoreFlow(key, defaultValue).first()
    }

    // Save Long to DataStore
    suspend fun saveLongToDataStore(key: String, value: Long) {
        checkInitialized()
        appContext.dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }

    // Get Long from DataStore as Flow
    fun getLongFromDataStoreFlow(key: String, defaultValue: Long = 0L): Flow<Long> {
        checkInitialized()
        return appContext.dataStore.data.map { preferences ->
            preferences[longPreferencesKey(key)] ?: defaultValue
        }
    }

    // Get Long from DataStore synchronously
    suspend fun getLongFromDataStore(key: String, defaultValue: Long = 0L): Long {
        return getLongFromDataStoreFlow(key, defaultValue).first()
    }

    // Remove a key from DataStore (suspend function)
    suspend fun removeFromDataStore(key: String) {
        checkInitialized()
        appContext.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(intPreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
            preferences.remove(longPreferencesKey(key))
        }
    }

    // Clear all DataStore preferences (suspend function)
    suspend fun clearDataStore() {
        checkInitialized()
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    //// SPECIFIC METHODS

    // Virtual NFC Card

    fun setVirtualCardUid(uid: String) {
        saveSecureString(StorageKeys.NFC_CARD_UID, uid)
    }

    fun getVirtualCardUid(): String? {
        return getSecureString(StorageKeys.NFC_CARD_UID, null)
    }

    // Device ID

    fun setDeviceId(deviceId: String) {
        saveSecureString(StorageKeys.DEVICE_ID, deviceId)
    }

    fun getDeviceId(): String? {
        return getSecureString(StorageKeys.DEVICE_ID, null)
    }

    // Authentication and User Session

    fun isLoggedIn(): Boolean {
        return getSecureBoolean(StorageKeys.IS_LOGGED_IN, false)
    }

    fun login(token: String, userId: Int, name: String, surname: String, email: String) {
        saveSecureBoolean(StorageKeys.IS_LOGGED_IN, true)
        saveSecureString(StorageKeys.AUTH_TOKEN, token)
        saveSecureInt(StorageKeys.USER_ID, userId)
        saveSecureString(StorageKeys.USER_NAME, name)
        saveSecureString(StorageKeys.USER_SURNAME, surname)
        saveSecureString(StorageKeys.USER_EMAIL, email)
    }

    fun getAuthToken(): String? {
        return getSecureString(StorageKeys.AUTH_TOKEN, null)
    }

    fun getUserData(): Map<String, Any?> {
        return mapOf(
            "userId" to getSecureInt(StorageKeys.USER_ID, -1),
            "name" to getSecureString(StorageKeys.USER_NAME, null),
            "surname" to getSecureString(StorageKeys.USER_SURNAME, null),
            "email" to getSecureString(StorageKeys.USER_EMAIL, null)
        )
    }

    fun logout() {
        clearAllSecure()
    }


    //// OTHER METHODS


    suspend fun clearEverything() {
        clearAllSecure()
        clearDataStore()
    }
}

object StorageKeys {
    //// Regular preferences keys (non-sensitive)

    const val LANGUAGE = "app_language"
    const val NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val FIRST_LAUNCH = "first_launch"

    //// Secure preferences keys (sensitive data)

    // Login and User
    const val IS_LOGGED_IN = "is_logged_in"
    const val AUTH_TOKEN = "auth_token"
    const val USER_ID = "user_id"
    const val USER_NAME = "user_name"
    const val USER_SURNAME = "user_surname"
    const val USER_EMAIL = "user_email"

    // Digital NFC suport
    const val NFC_CARD_UID = "nfc_card_uid"
    const val DEVICE_ID = "device_id"
}