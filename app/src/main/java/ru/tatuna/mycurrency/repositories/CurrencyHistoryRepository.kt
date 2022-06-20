package ru.tatuna.mycurrency.repositories

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import ru.tatuna.mycurrency.pojo.CurrencyDateDeserializer
import ru.tatuna.mycurrency.pojo.CurrencyDateResponse
import ru.tatuna.mycurrency.pojo.CurrencyHistoryResponse
import ru.tatuna.mycurrency.pojo.CurrencyHistoryResponseDeserializer
import ru.tatuna.mycurrency.service.CurrencyService

class CurrencyHistoryRepository(private val service: CurrencyService) {
    suspend fun execute(startDate: String, endDate: String, currency: String): Result {
        return try {
            val response = service.getCurrencyHistory(startDate = startDate, endDate = endDate, symbols = currency)
            val gson = GsonBuilder()
                .registerTypeAdapter(
                    CurrencyHistoryResponse::class.java,
                    CurrencyHistoryResponseDeserializer()
                )
                .create()
            val result = gson.fromJson(response, CurrencyHistoryResponse::class.java)
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
        data class Success(val data: CurrencyHistoryResponse) : Result()
        object Error : Result()
    }
}