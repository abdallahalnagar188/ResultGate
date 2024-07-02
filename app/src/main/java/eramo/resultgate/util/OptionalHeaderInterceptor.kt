package eramo.resultgate.util

import okhttp3.Interceptor
import okhttp3.Response

class OptionalHeaderInterceptor(private val optionalHeaderValue: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Check if optionalHeaderValue is provided
        val modifiedRequest = if (optionalHeaderValue != "-1") {
            originalRequest.newBuilder()
                .addHeader("city_id", optionalHeaderValue)
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(modifiedRequest)
    }
}