package ru.tashkent.currencyapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.tashkent.currencyapp.BuildConfig
import ru.tashkent.currencyapp.data.remote.CurrencyApi
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor {
                it.proceed(
                    it.request().newBuilder()
                        .url(
                            it.request().url.newBuilder()
//                                .addQueryParameter(
//                                    "apikey", BuildConfig.API_KEY
//                                )
                                .build()
                        )
                        .build()
                )
            }
            .addInterceptor(loggingInterceptor)
            .readTimeout(30L, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.currencyapi.com/v3/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi = retrofit.create()
}