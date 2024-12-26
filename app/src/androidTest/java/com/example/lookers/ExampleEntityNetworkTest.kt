package com.example.lookers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lookers.di.NetworkModule
import com.example.lookers.di.dataStore
import com.example.lookers.model.entity.ResultState
import com.example.lookers.model.entity.example.ExampleEntity
import com.example.lookers.model.network.AuthService
import com.example.lookers.model.network.ExampleService
import com.example.lookers.repository.AuthRepository
import com.example.lookers.repository.DataStoreRepository
import com.example.lookers.repository.ExampleRepository
import com.example.lookers.viewmodel.ExampleViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Provider

@RunWith(AndroidJUnit4::class)
class ExampleEntityNetworkTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: ExampleService
    private lateinit var authRepository: Provider<AuthRepository>
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var viewModel: ExampleViewModel

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // DataStoreRepository 초기화
        dataStoreRepository = DataStoreRepository(context.dataStore)

        // AuthRepository 초기화
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val authService = retrofit.create(AuthService::class.java)
        authRepository = Provider { AuthRepository(authService, dataStoreRepository) }

        val okHttpClient =
            OkHttpClient
                .Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(
                    NetworkModule.AppInterceptor(
                        dataStoreRepository,
                        authRepository,
                        context,
                    ),
                ).addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    },
                ).build()

        service = retrofit.create(ExampleService::class.java)
        viewModel = ExampleViewModel(ExampleRepository(service))
    }

    @Test
    fun testNetworkError() =
        runBlocking {
            val errorRetrofit =
                Retrofit
                    .Builder()
                    .baseUrl("https://invalid-url/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val errorService = errorRetrofit.create(ExampleService::class.java)

            try {
                errorService.getExampleData().execute()
                fail("Should throw an exception")
            } catch (e: Exception) {
                assertTrue(true)
                println("Expected error occurred: ${e.message}")
            }
        }

    @Test
    fun testNetworkCallThroughViewModel() {
        val latch = CountDownLatch(1)
        var result: ResultState<ExampleEntity>? = null

        viewModel.exampleEntity.observeForever { state ->
            result = state
            if (state !is ResultState.Loading) {
                latch.countDown()
            }
        }

        viewModel.getExampleData()

        assertTrue("Network call timed out", latch.await(5, TimeUnit.SECONDS))
        assertNotNull("Response should not be null", result)

        when (val currentState = result) {
            is ResultState.Success -> {
                val example = currentState.data
                assertNotNull(
                    "X-Cloud-Trace-Context should not be null",
                    example.xCloudTraceContext,
                )
                assertNotNull("Traceparent should not be null", example.traceparent)
                assertNotNull("User-Agent should not be null", example.userAgent)
                assertNotNull("Host should not be null", example.host)
                println("Response: $example")
            }

            is ResultState.Error -> {
                println("Error: ${currentState.message}")
                fail("Expected a successful response, but got an error: ${currentState.message}")
            }

            else -> fail("Unexpected state")
        }
    }

    @Test
    fun testDirectApiCall() =
        runBlocking {
            val response = service.getExampleData().execute()

            assertTrue("API call should be successful", response.isSuccessful)
            assertNotNull("Response body should not be null", response.body())

            val example = response.body()

            assertNotNull("X-Cloud-Trace-Context should not be null", example?.xCloudTraceContext)
            assertNotNull("Traceparent should not be null", example?.traceparent)
            assertNotNull("User-Agent should not be null", example?.userAgent)
            assertNotNull("Host should not be null", example?.host)

            println("API Response: ${response.body()}")
        }
}
