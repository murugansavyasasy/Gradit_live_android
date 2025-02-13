package com.vsca.vsnapvoicecollege.Repository


import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//class RestClient : OkHttpClient() {
//    companion object {
//        private var BASE_URL = "https://gradit.voicesnap.com/"
//        lateinit var apiInterfaces: ApiInterfaces
//        private var retrofit: Retrofit? = null
//        val client: Retrofit?
//            get() {
//                run { retrofit = builder.build() }
//                return retrofit
//            }
//        private val builder = Retrofit.Builder()
//            .client(RestClient())
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//
//        fun changeApiBaseUrl(CountryBaseUrl: String) {
//            Log.d("CheckBaseurl", CountryBaseUrl)
//            BASE_URL = CountryBaseUrl
//            Log.d("BASE_URL", BASE_URL)
//            apiInterfaces = Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .client(RestClient())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(ApiInterfaces::class.java)
//        }
//    }
//
//    init {
//        val client = Builder()
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        client.interceptors().add(interceptor)
//        val client1 = Builder()
//            .addInterceptor(interceptor)
//            .connectTimeout(300, TimeUnit.SECONDS)
//            .readTimeout(5, TimeUnit.MINUTES)
//            .writeTimeout(5, TimeUnit.MINUTES)
//            .build()
//        apiInterfaces = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(client1)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiInterfaces::class.java)
//    }
//}
//

class RestClient : OkHttpClient() {
    companion object {

        // private var BASE_URL = "http://192.168.1.116:8090/api/"
        private var BASE_URL = "https://gradit.voicesnap.com/"
        lateinit var apiInterfaces: ApiInterfaces
        private var retrofit: Retrofit? = null
        val client: Retrofit?
            get() {
                run { retrofit = builder.build() }
                return retrofit
            }
        private val builder = Retrofit.Builder()
            .client(RestClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        fun changeApiBaseUrl(CountryBaseUrl: String) {
            BASE_URL = CountryBaseUrl
            apiInterfaces = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(RestClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterfaces::class.java)
        }
    }

    init {
        val client = Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.interceptors().add(interceptor)
        val client1 = Builder()
            .addInterceptor(interceptor)
            .cache(cache)
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
        apiInterfaces = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client1)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterfaces::class.java)
    }
}