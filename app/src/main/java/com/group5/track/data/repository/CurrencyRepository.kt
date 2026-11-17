package com.group5.track.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.group5.track.data.remote.api.ExchangeRateApiService
import com.group5.track.data.remote.network.RetrofitClient

/**
 * CurrencyRepository:
 * Manages currency exchange rates.
 * Implements graceful offline fallback by caching the latest successful exchange rates
 * into SharedPreferences and falling back to sensible static defaults (e.g. 1 KES = ~0.0077 USD, ~0.0071 EUR).
 */
class CurrencyRepository(
    context: Context,
    private val apiService: ExchangeRateApiService = RetrofitClient.exchangeRateApi
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("currency_rates_cache", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "CurrencyRepository"
        private const val KEY_USD_RATE = "cached_rate_usd"
        private const val KEY_EUR_RATE = "cached_rate_eur"
        private const val KEY_LAST_UPDATED = "cached_rates_timestamp"

        // Default fallback rates (approximate: 1 USD ≈ 130 KES, 1 EUR ≈ 140 KES)
        const val DEFAULT_KES_TO_USD = 0.0077
        const val DEFAULT_KES_TO_EUR = 0.0071
    }

    /**
     * Fetches current exchange rates for KES.
     * In case of network failure, timeouts, or HTTP errors, gracefully falls back
     * to cached rates or hardcoded defaults.
     *
     * @return Pair<Double, Double> containing (rateKesToUsd, rateKesToEur)
     */
    suspend fun getKesExchangeRates(): Result<Pair<Double, Double>> {
        return try {
            val response = apiService.getLatestRates("KES")
            if (response.isSuccessful && response.body() != null) {
                val rates = response.body()?.conversionRates
                val usdRate = rates?.get("USD") ?: getCachedUsdRate()
                val eurRate = rates?.get("EUR") ?: getCachedEurRate()

                // Cache newly fetched rates
                saveRates(usdRate, eurRate)
                Log.d(TAG, "Successfully fetched live rates: 1 KES = $usdRate USD, $eurRate EUR")
                Result.success(Pair(usdRate, eurRate))
            } else {
                Log.w(TAG, "API returned error: ${response.code()}, falling back to cache")
                Result.success(getCachedRates())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network exception fetching exchange rates: ${e.message}, falling back to cache", e)
            Result.success(getCachedRates())
        }
    }

    private fun getCachedRates(): Pair<Double, Double> {
        return Pair(getCachedUsdRate(), getCachedEurRate())
    }

    fun getCachedUsdRate(): Double {
        return prefs.getFloat(KEY_USD_RATE, DEFAULT_KES_TO_USD.toFloat()).toDouble()
    }

    fun getCachedEurRate(): Double {
        return prefs.getFloat(KEY_EUR_RATE, DEFAULT_KES_TO_EUR.toFloat()).toDouble()
    }

    private fun saveRates(usdRate: Double, eurRate: Double) {
        prefs.edit()
            .putFloat(KEY_USD_RATE, usdRate.toFloat())
            .putFloat(KEY_EUR_RATE, eurRate.toFloat())
            .putLong(KEY_LAST_UPDATED, System.currentTimeMillis())
            .apply()
    }
}
