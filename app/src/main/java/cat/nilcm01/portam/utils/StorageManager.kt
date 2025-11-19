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


class StorageManager private constructor(context: Context) {

    val appContext = context.applicationContext

    // Encrypted SharedPreferences for sensitive data
    private val securePrefs: SharedPreferences by lazy {
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

    companion object {
        private const val SECURE_PREFS_NAME = "portam_secure_preferences"
        private const val DATASTORE_NAME = "portam_datastore"

        @Volatile
        private var instance: StorageManager? = null

        // Get singleton instance of StorageManager
        fun getInstance(context: Context): StorageManager {
            return instance ?: synchronized(this) {
                instance ?: StorageManager(context).also { instance = it }
            }
        }

        // DataStore extension property
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = DATASTORE_NAME
        )
    }


    //// SECURE PREFERENCES (Sensitive data - encrypted)


    // String
    fun saveSecureString(key: String, value: String) {
        securePrefs.edit().putString(key, value).apply()
    }

    fun getSecureString(key: String, defaultValue: String? = null): String? {
        return securePrefs.getString(key, defaultValue)
    }

    // Bool

    fun saveSecureBoolean(key: String, value: Boolean) {
        securePrefs.edit().putBoolean(key, value).apply()
    }

    fun getSecureBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return securePrefs.getBoolean(key, defaultValue)
    }

    // Int

    fun saveSecureInt(key: String, value: Int) {
        securePrefs.edit().putInt(key, value).apply()
    }

    fun getSecureInt(key: String, defaultValue: Int = 0): Int {
        return securePrefs.getInt(key, defaultValue)
    }

    // Long

    fun saveSecureLong(key: String, value: Long) {
        securePrefs.edit().putLong(key, value).apply()
    }

    fun getSecureLong(key: String, defaultValue: Long = 0L): Long {
        return securePrefs.getLong(key, defaultValue)
    }

    // Float

    fun saveSecureFloat(key: String, value: Float) {
        securePrefs.edit().putFloat(key, value).apply()
    }

    fun getSecureFloat(key: String, defaultValue: Float = 0f): Float {
        return securePrefs.getFloat(key, defaultValue)
    }

    // General

    fun removeSecure(key: String) {
        securePrefs.edit().remove(key).apply()
    }

    fun clearAllSecure() {
        securePrefs.edit().clear().apply()
    }

    fun containsSecure(key: String): Boolean {
        return securePrefs.contains(key)
    }


    //// DATASTORE (Modern Flow-based preferences)


    // Save a value to DataStore (suspend function)
    suspend fun <T> saveToDataStore(key: String, value: T) {
        appContext.dataStore.edit { preferences ->
            when (value) {
                is String -> preferences[stringPreferencesKey(key)] = value
                is Int -> preferences[intPreferencesKey(key)] = value
                is Boolean -> preferences[booleanPreferencesKey(key)] = value
                is Long -> preferences[longPreferencesKey(key)] = value
                else -> throw IllegalArgumentException("Unsupported type: ${value?.let { it::class.simpleName }}")
            }
        }
    }

    // Get a value from DataStore as Flow
    inline fun <reified T> getFromDataStoreFlow(key: String, defaultValue: T): Flow<T> {
        return appContext.dataStore.data.map { preferences ->
            when (T::class) {
                String::class -> preferences[stringPreferencesKey(key)] as? T ?: defaultValue
                Int::class -> preferences[intPreferencesKey(key)] as? T ?: defaultValue
                Boolean::class -> preferences[booleanPreferencesKey(key)] as? T ?: defaultValue
                Long::class -> preferences[longPreferencesKey(key)] as? T ?: defaultValue
                else -> defaultValue
            }
        }
    }

    // Get a value from DataStore synchronously (suspend function)
    suspend inline fun <reified T> getFromDataStore(key: String, defaultValue: T): T {
        return getFromDataStoreFlow(key, defaultValue).first()
    }

    // Remove a key from DataStore (suspend function)
    suspend fun removeFromDataStore(key: String) {
        appContext.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(intPreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
            preferences.remove(longPreferencesKey(key))
        }
    }

    // Clear all DataStore preferences (suspend function)
    suspend fun clearDataStore() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
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
    const val REFRESH_TOKEN = "refresh_token"
    const val USER_ID = "user_id"
    const val USER_NAME = "user_name"
    const val USER_SURNAME = "user_surname"
    const val USER_EMAIL = "user_email"

    // Digital NFC suport
    const val NFC_CARD_UID = "nfc_card_uid"
    const val DEVICE_ID = "device_id"
}

