package kg.iaau.diploma.network.interceptors

import kg.iaau.diploma.data.Refresh
import kg.iaau.diploma.local_storage.prefs.StoragePreferences
import kg.iaau.diploma.network.api.ApiAuth
import okhttp3.Interceptor
import okhttp3.Request

class RefreshTokenInterceptor(private val prefs: StoragePreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {

        val request = chain.request()
        // Make a request and save the response here
        val response = chain.proceed(request)

        // Don't try to refresh auth token if user is not authorized yet
        if (prefs.token.isNullOrEmpty()) {
            return response
        }

        // If the response code is 401, then fetch new token and make a new request with new token
        val responseCode = response.code

        if (responseCode == 401 || responseCode == 500) {
            return makeTokenRefreshCall(request, chain)
        }

        // If the response is not 401 then return the normal response
        return response
    }

    private fun makeTokenRefreshCall(req: Request, chain: Interceptor.Chain): okhttp3.Response {
        /* fetch refresh token and save it to appPrefs */
        fetchRefreshToken()

        /* make a new request which is same as the original one, except that its headers now contain a refreshed token */
        val newRequest: Request = req.newBuilder()
            .header("Authorization", "Bearer ${prefs.token}")
            .build()

        return chain.proceed(newRequest)
    }

    private fun fetchRefreshToken() {

        val request = RefreshTokenInstance.serviceBuilder(ApiAuth::class.java)

        // Synchronous call to api to get the new access and refresh token
        val callAccessToken = request.refresh(
            Refresh(accessToken = prefs.token, refreshToken = prefs.refreshToken, username = prefs.phone)
        )

        val accessToken = callAccessToken.execute().body()

        // Save to prefs new access and refresh token
        prefs.token = accessToken?.accessToken ?: ""
        prefs.refreshToken = accessToken?.refreshToken ?: ""

    }
}