package com.elcomercio.mvp_dagger2_kotlin.di.module

import okhttp3.logging.HttpLoggingInterceptor
import android.app.Application
import com.elcomercio.mvp_dagger2_kotlin.BuildConfig.DEBUG
import com.elcomercio.mvp_dagger2_kotlin.BuildConfig.BASE_URL
import com.elcomercio.mvp_dagger2_kotlin.data.repository.remote.api.SampleApi
import okhttp3.Cache
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.google.gson.FieldNamingPolicy
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(
                if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .cache(cache)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideSampleApi(client: Retrofit): SampleApi {
        return client.create(SampleApi::class.java)
    }

}