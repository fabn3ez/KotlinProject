package com.group5.track.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Data model representing the exchange rate API response.
 * Uses open exchange rate schemas (e.g. exchangerate-api.com standard schema).
 */
data class ExchangeRateResponse(
    @SerializedName("result")
    val result: String?,

    @SerializedName("base_code")
    val baseCode: String?,

    @SerializedName("conversion_rates")
    val conversionRates: Map<String, Double>?
)
