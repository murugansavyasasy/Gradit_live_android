package com.vsca.vsnapvoicecollege.Repository

import android.util.Log
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

//class RestClient : OkHttpClient() {
//    companion object {
//
//        // private var BASE_URL = "http://192.168.1.116:8090/api/"
//        private var BASE_URL = "https://gradit.voicesnap.com/"
//
//        //        private var GET_PRESIGNED_BASE_URL = "https://api.schoolchimes.com/nodejs/api/MergedApi/"
//        lateinit var apiInterfaces: ApiInterfaces
//        private var retrofit: Retrofit? = null
//        val client: Retrofit?
//            get() {
//                run { retrofit = builder.build() }
//                return retrofit
//            }
//        fun getBaseUrl(): String {
//            return BASE_URL
//        }
//        private val builder = Retrofit.Builder()
//            .client(RestClient())
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//
//        fun changeApiBaseUrl(CountryBaseUrl: String) {
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
//            .cache(cache)
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


class RestClient {

    companion object {
        private var BASE_URL = "https://gradit.voicesnap.com/"
        private var retrofit: Retrofit? = null
        private var okHttpClient: OkHttpClient? = null
        private var _apiInterfaces: ApiInterfaces? = null

        val apiInterfaces: ApiInterfaces
            get() {
                if (_apiInterfaces == null) {
                    initRetrofit()
                }
                return _apiInterfaces!!
            }

        private fun initRetrofit() {
            if (okHttpClient == null) {
                val interceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .build()
            }

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient!!)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            _apiInterfaces = retrofit!!.create(ApiInterfaces::class.java)
        }

        fun changeApiBaseUrl(newBaseUrl: String) {
            BASE_URL = newBaseUrl
            retrofit = null
            _apiInterfaces = null
            initRetrofit()
        }

        val client: Retrofit
            get() {
                if (retrofit == null) {
                    initRetrofit()
                }
                return retrofit!!
            }
    }
}


