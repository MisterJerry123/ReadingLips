package com.withsejong.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASEURL = "http://144.24.87.139:8086/"
    val instance : Api by lazy {
        //로깅 인터셉터 설정
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        //gson 유연화 설정
        //MalformedJsonException 때문에 설정

        val gson = GsonBuilder()
            .setLenient()
            .create()

        //커스텀okhttp클라이언트 생성
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()



        val retrofit = Retrofit.Builder()
            .baseUrl(BASEURL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(Api::class.java)
    }
}