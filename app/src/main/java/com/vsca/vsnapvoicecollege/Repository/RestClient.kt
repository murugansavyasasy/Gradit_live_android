package com.vsca.vsnapvoicecollege.Repository

import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RestClient {

    companion object {
//        private var BASE_URL = "https://gradit.voicesnap.com/"
        private var BASE_URL = "https://www.thegradit.com/mobileapp/"
//        private var BASE_URL = "http://future.thegradit.com/mobileapp/"
        private const val RESUME_URL = "http://placement.thegradit.com/v1/api/"

        private var retrofit: Retrofit? = null
        private var resumeRetrofit: Retrofit? = null
        private var okHttpClient: OkHttpClient? = null
        private var _apiInterfaces: ApiInterfaces? = null
        private var _resumeApiInterfaces: ApiInterfaces? = null

        val apiInterfaces: ApiInterfaces
            get() {
                if (_apiInterfaces == null) {
                    initDefaultRetrofit()
                }
                return _apiInterfaces!!
            }

        val resumeApiInterfaces: ApiInterfaces
            get() {
                if (_resumeApiInterfaces == null) {
                    initResumeRetrofit()
                }
                return _resumeApiInterfaces!!
            }

        private fun initDefaultRetrofit() {
            if (okHttpClient == null) {
                createHttpClient()
            }

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient!!)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            _apiInterfaces = retrofit!!.create(ApiInterfaces::class.java)
        }

        private fun initResumeRetrofit() {
            if (okHttpClient == null) {
                createHttpClient()
            }

            resumeRetrofit = Retrofit.Builder()
                .baseUrl(RESUME_URL)
                .client(okHttpClient!!)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            _resumeApiInterfaces = resumeRetrofit!!.create(ApiInterfaces::class.java)
        }

        private fun createHttpClient() {
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

        fun changeApiBaseUrl(newBaseUrl: String) {
            BASE_URL = newBaseUrl
            retrofit = null
            _apiInterfaces = null
            initDefaultRetrofit()
        }


        val client: Retrofit
            get() {
                if (retrofit == null) {
                    initDefaultRetrofit()
                }
                return retrofit!!
            }
    }
}






