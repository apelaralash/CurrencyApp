package ru.tatuna.mycurrency.pojo

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class CurrencyDateResponse(
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("timestamp") var timestamp: Long? = null,
    @SerializedName("historical") var historical: String? = null,
    @SerializedName("base") var base: String? = null,
    @SerializedName("date") var date: String? = null,
    var history: List<CurrencyItem> = listOf()
)

class CurrencyDateDeserializer : JsonDeserializer<CurrencyDateResponse?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CurrencyDateResponse? {
        val currencyDateResponse: CurrencyDateResponse = GsonBuilder().create().fromJson(json, CurrencyDateResponse::class.java)
        val jsonObject = json.asJsonObject
        val ratesList = mutableListOf<CurrencyItem>()
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
                    if (currencyDateResponse.base != list[0]) ratesList.add(CurrencyItem(name = list[0], value = 1F / list[1].toFloat()))
                }
            }
        }
        currencyDateResponse.history = ratesList
        return currencyDateResponse
    }
}
