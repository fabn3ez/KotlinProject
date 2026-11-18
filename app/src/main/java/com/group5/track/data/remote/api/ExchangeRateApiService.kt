package com.group5.track.data.remote.api

import com.group5.track.data.remote.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Exchange Rate API Service interface for fetching currency conversion rates.
 * Default base: KES (Kenyan Shilling).
 */
interface ExchangeRateApiService {

    @GET("v6/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("baseCurrency") baseCurrency: String = "KES"
    ): Response<ExchangeRateResponse>
}
