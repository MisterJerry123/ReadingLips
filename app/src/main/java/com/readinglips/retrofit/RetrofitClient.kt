package com.withsejong.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASEURL = "http://112.187.179.65:8080/"
    val instance: Api by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val gson = GsonBuilder()
            .setLenient()
            .create()

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

//    val instance2 : Api by lazy {
//        //로깅 인터셉터 설정
//        val logging = HttpLoggingInterceptor()
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
//
//        //gson 유연화 설정
//        //MalformedJsonException 때문에 설정
//
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
//
//        //커스텀okhttp클라이언트 생성
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .connectTimeout(5, TimeUnit.MINUTES)  // 연결 타임아웃 설정
//            .readTimeout(5, TimeUnit.MINUTES)     // 읽기 타임아웃 설정
//            .writeTimeout(5, TimeUnit.MINUTES)    // 쓰기 타임아웃 설정
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://112.187.179.65:8000/")
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//
//        retrofit.create(Api::class.java)
//    }
//}