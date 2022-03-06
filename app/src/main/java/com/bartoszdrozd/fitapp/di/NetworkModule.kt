package com.bartoszdrozd.fitapp.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.adapter.KotlinxInstantAdapter
import com.apollographql.apollo3.adapter.KotlinxLocalDateAdapter
import com.apollographql.apollo3.network.okHttpClient
import com.bartoszdrozd.fitapp.BuildConfig
import com.bartoszdrozd.fitapp.data.auth.IAuthService
import com.bartoszdrozd.fitapp.data.auth.RegisterUserResponseErrorCode
import com.bartoszdrozd.fitapp.type.Date
import com.bartoszdrozd.fitapp.type.DateTime
import com.google.gson.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.LocalDate
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // Auth server address
    private const val BASE_URL: String = "https://10.0.2.2:7170"
    private const val BASE_URL_BACKEND = "https://10.0.2.2:5001"

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder()
            // Kotlin Date types
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonDeserializer { json, _, _ ->
                    LocalDate.parse(json.asJsonPrimitive.asString)
                })
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonSerializer { src: LocalDate, _, _ ->
                    JsonPrimitive(src.toString())
                })
            .registerTypeAdapter(
                kotlinx.datetime.Instant::class.java,
                JsonDeserializer { json, _, _ ->
                    kotlinx.datetime.Instant.parse(json.asString)
                })
            .registerTypeAdapter(
                kotlinx.datetime.Instant::class.java,
                JsonSerializer { src: kotlinx.datetime.Instant, _, _ ->
                    JsonPrimitive(src.toString())
                })

            // Java Date types
            .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
                LocalDateTime.parse(json.asString)
            })
            .registerTypeAdapter(
                RegisterUserResponseErrorCode::class.java,
                JsonDeserializer { json, _, _ ->
                    RegisterUserResponseErrorCode.values()[json.asInt]
                })
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonSerializer { src: LocalDateTime, _, _ ->
                    JsonPrimitive(src.toString())
                })
            .create()
    }

    private val httpClient = if (BuildConfig.DEBUG) {
        UnsafeHttpClient.okHttpClient
    } else {
        OkHttpClient()
            .newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesUsersService(): IAuthService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(providesGson()))
            .client(httpClient)
            .build()
            .create(IAuthService::class.java)
    }

    @Provides
    @Singleton
    fun providesApolloClient(): ApolloClient =
        ApolloClient.Builder()
            .okHttpClient(httpClient)
            .serverUrl("$BASE_URL_BACKEND/graphql")
            .addCustomScalarAdapter(Date.type, KotlinxLocalDateAdapter)
            .addCustomScalarAdapter(DateTime.type, KotlinxInstantAdapter)
            .build()
}