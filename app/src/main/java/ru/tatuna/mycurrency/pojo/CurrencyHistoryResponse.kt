package ru.tatuna.mycurrency.pojo

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.time.LocalDate

data class CurrencyHistoryResponse(
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("timeseries") var timeseries: Boolean? = null,
    @SerializedName("start_date") var startDate: String? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("base") var base: String? = null,
    var history: List<HistoryValue> = listOf()
)

class CurrencyHistoryResponseDeserializer : JsonDeserializer<CurrencyHistoryResponse?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CurrencyHistoryResponse? {
        val currencyHistoryResponse: CurrencyHistoryResponse = GsonBuilder().create().fromJson(json, CurrencyHistoryResponse::class.java)
        val jsonObject = json.asJsonObject
        val ratesList = mutableListOf<HistoryValue>()
        if (jsonObject.has("rates")) {
            val elem = jsonObject["rates"]
            if (elem != null && !elem.isJsonNull) {
                val array = elem
                    .toString()
                    .replace("{", "")
                    .replace("}", "")
                    .replace("\"", "")
                    .split(",")
                array.iterator().forEach { element ->
                    val list = element.split(":")
                    ratesList.add(HistoryValue(name = list[1], value = 1F / list[2].toFloat(), date = LocalDate.parse(list[0])))
                }
            }
        }
        currencyHistoryResponse.history = ratesList
        return currencyHistoryResponse
    }
}