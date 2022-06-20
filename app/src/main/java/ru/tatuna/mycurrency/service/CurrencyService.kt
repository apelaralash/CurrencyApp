package ru.tatuna.mycurrency.service

import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.tatuna.mycurrency.BuildConfig
import ru.tatuna.mycurrency.pojo.CurrencyDateResponse

interface CurrencyService {
    companion object {
        val INSTANCE = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(BuildConfig.API_KEY))
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyService::class.java)
    }

    @GET("{date}")
    suspend fun getCurrencyList(
        @Path("date") date: String,
        @Query("base") base: String = "USD"
    ): JsonElement

    @GET("timeseries")
    suspend fun getCurrencyHistory(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") base: String = "USD",
        @Query("symbols") symbols: String
    ): JsonElement
}