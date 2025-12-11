package cat.nilcm01.portam.utils

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.cardemulation.CardEmulation
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

/**
 * NfcEmiter manages NFC Host Card Emulation (HCE) to simulate an NFC tag
 * with the device ID as the UID.
 */
class NfcEmiter(private val activity: Activity) {

    companion object {
        private const val TAG = "NfcEmiter"
        // AID for our HCE service (must match the one in aid_list.xml)
        private const val AID = "F0010203040506"
    }

    private var nfcAdapter: NfcAdapter? = null
    private var cardEmulation: CardEmulation? = null
    private var isEnabled = false

    init {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        if (nfcAdapter == null) {
            Log.w(TAG, "NFC not available on this device")
        } else {
            cardEmulation = CardEmulation.getInstance(nfcAdapter)
        }
    }

    /**
     * Enable NFC card emulation for the current view/activity
     */
    fun enable() {
        if (nfcAdapter == null) {
            Log.w(TAG, "Cannot enable NFC: adapter is null")
            return
        }

        if (!nfcAdapter!!.isEnabled) {
            Log.w(TAG, "NFC is disabled on the device")
            return
        }

        // Enable the HCE service component
        val componentName = ComponentName(
            activity,
            NfcHostApduService::class.java
        )

        try {
            activity.packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            // Set this service as the preferred service for the foreground activity
            cardEmulation?.setPreferredService(activity, componentName)

            isEnabled = true
            Log.d(TAG, "NFC emulation enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error enabling NFC emulation", e)
        }
    }

    /**
     * Disable NFC card emulation
     */
    fun disable() {
        if (nfcAdapter == null) {
            return
        }

        try {
            // Unset the preferred service
            cardEmulation?.unsetPreferredService(activity)

            isEnabled = false
            Log.d(TAG, "NFC emulation disabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error disabling NFC emulation", e)
        }
    }

    /**
     * Check if NFC is available and enabled
     */
    fun isNfcAvailable(): Boolean {
        return nfcAdapter != null && nfcAdapter!!.isEnabled
    }

    /**
     * Check if emulation is currently enabled
     */
    fun isEmulationEnabled(): Boolean {
        return isEnabled
    }
}

/**
 * Host-based Card Emulation service that responds to NFC readers
 *
 * IMPORTANT: HCE cannot set the actual NFC tag UID (hardware-level).
 * Instead, this service returns the device ID in response to APDU commands.
 * The device ID is returned on SELECT and any READ command.
 *
 * To read the device ID from an NFC reader:
 * 1. SELECT the AID (F0010203040506)
 * 2. The response will contain the device ID followed by success status (9000)
 * 3. Alternatively, send GET DATA (00CA0000) or READ BINARY (00B00000) commands
 */
class NfcHostApduService : HostApduService() {

    companion object {
        private const val TAG = "NfcHostApduService"

        // ISO 7816-4 status words
        private const val STATUS_SUCCESS = "9000"
        private const val STATUS_FAILED = "6F00"

        // APDU command constants
        private const val SELECT_APDU_HEADER = "00A40400"
        private const val GET_DATA_APDU = "00CA0000"
        private const val READ_BINARY_APDU = "00B0"  // READ BINARY command
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Service deactivated: $reason")
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommand = bytesToHex(commandApdu)
        Log.d(TAG, "Received APDU: $hexCommand")

        val deviceId = StorageManager.getDeviceId() ?: "UNKNOWN"

        return when {
            // SELECT command - immediately return device ID as the "card identity"
            hexCommand.startsWith(SELECT_APDU_HEADER) -> {
                Log.d(TAG, "SELECT command received, returning device ID as UID: $deviceId")

                // Return device ID directly as the SELECT response (this simulates the UID)
                val response = deviceId.toByteArray(Charsets.UTF_8) +
                              hexStringToByteArray(STATUS_SUCCESS)
                Log.d(TAG, "Response length: ${response.size}, Data: ${bytesToHex(response)}")
                response
            }

            // GET DATA command - return the device ID
            hexCommand.startsWith(GET_DATA_APDU) -> {
                Log.d(TAG, "GET DATA command received, returning device ID: $deviceId")

                val response = deviceId.toByteArray(Charsets.UTF_8) +
                              hexStringToByteArray(STATUS_SUCCESS)
                Log.d(TAG, "Response length: ${response.size}, Data: ${bytesToHex(response)}")
                response
            }

            // READ BINARY command - return the device ID
            hexCommand.startsWith(READ_BINARY_APDU) -> {
                Log.d(TAG, "READ BINARY command received, returning device ID: $deviceId")

                val response = deviceId.toByteArray(Charsets.UTF_8) +
                              hexStringToByteArray(STATUS_SUCCESS)
                Log.d(TAG, "Response length: ${response.size}, Data: ${bytesToHex(response)}")
                response
            }

            // For any other command, return the device ID
            else -> {
                Log.d(TAG, "Command received: $hexCommand, returning device ID: $deviceId")

                val response = deviceId.toByteArray(Charsets.UTF_8) +
                              hexStringToByteArray(STATUS_SUCCESS)
                Log.d(TAG, "Response length: ${response.size}, Data: ${bytesToHex(response)}")
                response
            }
        }
    }

    /**
     * Convert byte array to hex string
     */
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }

    /**
     * Convert hex string to byte array
     */
    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) +
                    Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}