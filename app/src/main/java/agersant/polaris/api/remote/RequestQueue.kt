package agersant.polaris.api.remote

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.IOException

internal class RequestQueue(auth: Auth) {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .addInterceptor(auth)
        .build()

    @Throws(IOException::class)
    fun requestSync(request: Request?): ResponseBody? {
        val response = client.newCall(request!!).execute()
        if (!response.isSuccessful) {
            throw IOException("Request failed with error code: " + response.code)
        }
        return response.body
    }

    fun requestAsync(request: Request?, callback: Callback?) {
        client.newCall(request!!).enqueue(callback!!)
    }
}
