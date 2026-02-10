package cat.nilcm01.portam.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
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

object LocalVirtualCardHandler {
    suspend fun assignToUser(): Boolean? {
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
                val deviceId = StorageManager.getDeviceId()
                val response: HttpResponse =
                    client.post(
                        "https://portam-server.vercel.app/api/users/" +
                                "${StorageManager.getUserData()["userId"]}/suports"
                    ) {
                        contentType(io.ktor.http.ContentType.Application.FormUrlEncoded)
                        setBody(
                            "uid=${deviceId}&info=spvirtual::${deviceId}"
                        )
                    }

                val responseBody = response.bodyAsText()
                val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

                client.close()

                // Return boolean
                return@withContext jsonResponse["success"]?.jsonPrimitive?.boolean ?: false
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }

    suspend fun removeFromUser(): Boolean? {
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
                    client.delete(
                        "https://portam-server.vercel.app/api/users/" +
                                "${StorageManager.getUserData()["userId"]}/suports/" +
                                StorageManager.getDeviceId()
                    ) {
                        contentType(io.ktor.http.ContentType.Application.Json)
                    }

                val responseBody = response.bodyAsText()
                val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject

                client.close()

                // Return boolean
                return@withContext jsonResponse["success"]?.jsonPrimitive?.boolean ?: false
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }
}

