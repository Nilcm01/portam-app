package cat.nilcm01.portam.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build

class NfcHandler(private val activity: Activity) {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    init {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)

        val intent = Intent(activity, activity.javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                activity,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                activity,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    fun isNfcAvailable(): Boolean {
        return nfcAdapter != null
    }

    fun isNfcEnabled(): Boolean {
        return nfcAdapter?.isEnabled == true
    }

    fun enableForegroundDispatch() {
        nfcAdapter?.enableForegroundDispatch(
            activity,
            pendingIntent,
            null,
            null
        )
    }

    fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    companion object {
        fun extractTagUid(intent: Intent?): String? {
            if (intent == null) return null

            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }

            return tag?.id?.let { bytesToHexString(it) }
        }

        private fun bytesToHexString(bytes: ByteArray): String {
            return bytes.joinToString("") { byte ->
                "%02x".format(byte)
            }
        }
    }
}

