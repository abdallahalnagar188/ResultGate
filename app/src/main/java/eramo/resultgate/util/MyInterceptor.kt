package eramo.resultgate.util

import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request = request.newBuilder()
            .header("lang", if (LocalHelperUtil.isEnglish()) Constants.API_HEADER_LANG_EN else Constants.API_HEADER_LANG_AR)
            .header("Accept", "application/json")
//            .header("currency", UserUtil.getCurrency())
            .build()
        return chain.proceed(request)
    }


}