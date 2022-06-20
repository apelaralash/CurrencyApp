package ru.tatuna.mycurrency.repositories

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import retrofit2.HttpException
import ru.tatuna.mycurrency.pojo.CurrencyDateDeserializer
import ru.tatuna.mycurrency.pojo.CurrencyDateResponse
import ru.tatuna.mycurrency.service.CurrencyService
import java.net.SocketTimeoutException

class CurrencyListRepository(private val service: CurrencyService) {
    suspend fun execute(date: String): Result {
        return try {
            val response = service.getCurrencyList(date = date)
            val gson = GsonBuilder()
                .registerTypeAdapter(
                    CurrencyDateResponse::class.java,
                    CurrencyDateDeserializer()
                )
                .create()
            val result = gson.fromJson(response, CurrencyDateResponse::class.java)
            if (result != null) Result.Success(result) else Result.Error
        } catch (e: JsonParseException) {
            e.printStackTrace()
            Result.Error
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error
        }
    }

    sealed class Result {
        data class Success(val data: CurrencyDateResponse) : Result()
        object Error : Result()
    }
}