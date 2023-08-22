package com.example.avatar_ai_manager.network

import android.annotation.SuppressLint
import android.util.Log
import com.example.avatar_ai_manager.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private const val TAG = "CloudAnchorApiService"

private const val EXPIRED = "Expired"

// SERVICE CREDENTIALS
// URL Details
private const val BASE_URL = "https://arcore.googleapis.com"
private const val ENDPOINT = "/v1beta2/management/anchors"
private const val ANCHOR_ID = "anchorId"

// Request Headers
private const val AUTHORISATION_HEADER = "Authorization"
private const val BEARER_PREFIX = "Bearer"
private const val TOKEN = BuildConfig.CLOUD_ANCHORS_BEARER_TOKEN
private const val CONTENT_TYPE = "Content-Type: application/json"

// Request Queries
private const val PAGE_SIZE_QUERY = "page_size"
private const val PAGE_SIZE = 1000
private const val UPDATE_MASK_QUERY = "?updateMask=expire_time"

// ISO 8601 Timestamp Formatting
private const val ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
private const val TIME_ZONE = "UTC"

// Time in milliseconds before a TimeoutException
// is called on a download (GET) request.
private const val TIMEOUT_MS = 7000L

/*
* Moshi Converter Factory - decodes JSON web data.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/*
* Retrofit object with the base URL. Fetches data
* and decodes it with the Moshi Converter Factory.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/*
* Network layer: this interface defines the Retrofit HTTP requests.
 */
interface CloudAnchorApiService {
    @GET(ENDPOINT)
    suspend fun getAnchors(
        @Header(AUTHORISATION_HEADER) bearerToken: String,
        @Query(PAGE_SIZE_QUERY) pageSize: Int
    ): CloudAnchorResult

    @GET("$ENDPOINT/{$ANCHOR_ID}")
    suspend fun getAnchor(
        @retrofit2.http.Path(value = ANCHOR_ID, encoded = true) anchorId: String,
        @Header(AUTHORISATION_HEADER) bearerToken: String
    ): CloudAnchor

    @PATCH("$ENDPOINT/{$ANCHOR_ID}$UPDATE_MASK_QUERY")
    @Headers(CONTENT_TYPE)
    suspend fun extendAnchor(
        @retrofit2.http.Path(value = ANCHOR_ID, encoded = true) anchorId: String,
        @Header(AUTHORISATION_HEADER) bearerToken: String,
        @Body requestBody: ExtendAnchorRequestBody
    ): CloudAnchor

    @DELETE("$ENDPOINT/{$ANCHOR_ID}")
    suspend fun deleteAnchor(
        @retrofit2.http.Path(value = ANCHOR_ID, encoded = true) anchorId: String,
        @Header(AUTHORISATION_HEADER) bearerToken: String
    )
}

object CloudAnchorApi {

    // Initialise the retrofit service only at first usage (by lazy).
    private val retrofitService: CloudAnchorApiService by lazy {
        retrofit.create(CloudAnchorApiService::class.java)
    }

    private suspend fun <T> execute(request: suspend () -> T): T? {
        return try {
            withTimeout(TIMEOUT_MS) {
                request()
            }
        } catch (e: HttpException) {
            val httpError = e.response()?.errorBody()?.string() ?: "Unknown error"
            Log.e(TAG, "execute: HTTP error: $httpError", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            null
        }
    }

    suspend fun getAnchors(): Map<String, String>? {
        return execute {
            retrofitService.getAnchors("$BEARER_PREFIX $TOKEN", PAGE_SIZE).anchors?.associateBy(
                { it.name.substring(8) }, { getDaysToExpiration(it.expireTime) }
            ) ?: emptyMap()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDaysToExpiration(isoDate: String): String {
        val dateFormat = SimpleDateFormat(ISO_8601_PATTERN)
        dateFormat.timeZone = TimeZone.getTimeZone(TIME_ZONE)

        val targetDate = dateFormat.parse(isoDate)

        val timeDifference = targetDate?.let {
            it.time - Date().time
        }

        val daysLeft = timeDifference?.let {
            TimeUnit.MILLISECONDS.toDays(it)
        }
        return daysLeft?.toString() ?: EXPIRED
    }

    suspend fun extendAnchor(anchorId: String): CloudAnchor? {
        return execute {
            val requestBody = ExtendAnchorRequestBody(
                retrofitService.getAnchor(
                    anchorId,
                    "$BEARER_PREFIX $TOKEN"
                ).maximumExpireTime
            )
            retrofitService.extendAnchor(anchorId, "$BEARER_PREFIX $TOKEN", requestBody)
        }
    }

    suspend fun deleteAnchor(anchorId: String): Boolean {
        return try {
            withTimeout(TIMEOUT_MS) {
                retrofitService.deleteAnchor(anchorId, "$BEARER_PREFIX $TOKEN")
                true
            }
        } catch (e: HttpException) {
            val httpError = e.response()?.errorBody()?.string() ?: "Unknown error"
            Log.e(TAG, "deleteAnchor: HTTP error: $httpError", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            false
        }
    }

}