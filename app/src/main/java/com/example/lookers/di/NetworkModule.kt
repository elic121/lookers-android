package com.example.lookers.di

import android.content.Context
import com.example.lookers.BuildConfig
import com.example.lookers.model.network.AuthService
import com.example.lookers.model.network.DrawerService
import com.example.lookers.model.network.EmbeddedService
import com.example.lookers.model.network.ExampleService
import com.example.lookers.model.network.SearchService
import com.example.lookers.repository.AuthRepository
import com.example.lookers.repository.DataStoreRepository
import com.example.lookers.util.NetworkUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * @see AuthService
 * @see DrawerService
 * @see ExampleService
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    val gson: Gson =
        GsonBuilder()
            .setLenient()
            .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(appInterceptor: AppInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(appInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            ).build()

    @Provides
    @Singleton
    fun provideAppInterceptor(
        dataStoreRepository: DataStoreRepository,
        authRepositoryProvider: Provider<AuthRepository>,
        @ApplicationContext context: Context,
    ): AppInterceptor = AppInterceptor(dataStoreRepository, authRepositoryProvider, context)

    class AppInterceptor
        @Inject
        constructor(
            private val dataStoreRepository: DataStoreRepository,
            private val authRepositoryProvider: Provider<AuthRepository>,
            @ApplicationContext private val context: Context,
        ) : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                if (!NetworkUtils.isNetworkAvailable(context)) {
                    throw IOException("No network available")
                }

                val token = runBlocking { dataStoreRepository.getAccessToken().first() }

                val request =
                    chain
                        .request()
                        .newBuilder()
                        .apply {
                            addHeader("Authorization", "Bearer $token")
                        }.build()

                val response = chain.proceed(request)

                if (response.code == 401) {
                    response.close()

                    val refreshToken = runBlocking { dataStoreRepository.getRefreshToken().first() }
                    val newAccessToken =
                        runBlocking {
                            val tokenResponse =
                                authRepositoryProvider.get().refreshAccessToken(refreshToken)
                            if (tokenResponse.isSuccess) {
                                tokenResponse.getOrNull()
                            } else {
                                null
                            }
                        }

                    if (newAccessToken != null) {
                        val newRequest =
                            request
                                .newBuilder()
                                .header("Authorization", "Bearer ${newAccessToken.accessToken}")
                                .build()

                        val newResponse = chain.proceed(newRequest)

                        return newResponse
                    }
                }

                return response
            }
        }

    @Provides
    @Singleton
    fun provideExampleService(retrofit: Retrofit): ExampleService = retrofit.create(ExampleService::class.java)

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideEmbeddedService(retrofit: Retrofit): EmbeddedService = retrofit.create(EmbeddedService::class.java)

    @Provides
    @Singleton
    fun provideDrawerService(retrofit: Retrofit): DrawerService = retrofit.create(DrawerService::class.java)

    @Provides
    @Singleton
    fun provideSearchService(retrofit: Retrofit): SearchService = retrofit.create(SearchService::class.java)
}
