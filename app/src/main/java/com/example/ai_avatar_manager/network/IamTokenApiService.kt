package com.example.ai_avatar_manager.network

import android.util.Log
import com.example.ai_avatar_manager.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * The TranscriptionApi serves as a network API to connect to IBM IAM Authentication Service.
 *
 * Please ensure that the service credentials have been added to gradle.properties in the format:
 * CLOUD_OBJECT_STORAGE_API_KEY="{apikey}"
 */

private const val TAG = "IamTokenApiService"

// Minimum remaining token validity in seconds
private const val MIN_VALIDITY = 10

// SERVICE CREDENTIALS
// URL Details
private const val BASE_URL = "https://iam.cloud.ibm.com"
private const val ENDPOINT = "/oidc/token"

// Request Headers
private const val ACCEPT = "Accept: application/json"
private const val CONTENT_TYPE = "Content-Type: application/x-www-form-urlencoded"

// Authentication Details
private const val API_KEY = BuildConfig.CLOUD_OBJECT_STORAGE_API_KEY

// Request Fields
private const val RESPONSE_TYPE = "cloud_iam"
private const val GRANT_TYPE = "urn:ibm:params:oauth:grant-type:apikey"

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
interface TokenApiService {
    /*
    * This function performs a POST request for an IAM Token.
     */
    @FormUrlEncoded
    @POST(ENDPOINT)
    @Headers(ACCEPT, CONTENT_TYPE)
    suspend fun getToken(
        @FieldMap params: Map<String, String>
    ): IamTokenResult
}

/*
* TokenApi connects to IBM IAM Authentication Service.
* It is initialised as a public singleton object to conserve resources
* by ensuring that the Retrofit API service is only initialised once.
 */
object TokenApi {

    // Initialise the retrofit service only at first usage (by lazy).
    private val retrofitService: TokenApiService by lazy {
        retrofit.create(TokenApiService::class.java)
    }

    // The last token requested is saved here.
    private var tokenResult: IamTokenResult? = null

    /*
    * This function returns a valid IBM IAM token. A new token
    * is requested from the service if the last available token
    * has less than MIN_VALIDITY seconds of validity. Null is
    * returned if an Exception occurs.
     */
    suspend fun getToken(): String? {
        // The minimum expiration timestamp required in Unix format.
        val targetUnixTimestamp =
            (System.currentTimeMillis() / 1000) + MIN_VALIDITY

        return if (tokenResult != null
            && targetUnixTimestamp < tokenResult!!.expiration
        ) {
            tokenResult!!.accessToken
        } else {
            Log.i(TAG, "New IAM token requested")
            getNewToken()
        }
    }

    /*
    * This function requests a new IAM token from the service
    * and returns null if an error occurs.
     */
    private suspend fun getNewToken(): String? {
        val params = mapOf(
            "apikey" to API_KEY,
            "response_type" to RESPONSE_TYPE,
            "grant_type" to GRANT_TYPE
        )
        return try {
            tokenResult = retrofitService.getToken(params)
            tokenResult!!.accessToken
        } catch (e: HttpException) {
            val httpError = e.response()?.errorBody()?.string() ?: "Unknown error"
            Log.e(TAG, "getNewToken: HTTP error: $httpError", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "getNewToken: exception occurred", e)
            null
        }
    }

}