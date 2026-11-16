package com.group5.track.domain

object PropertyDomainHelper {

    enum class PriceCategory(val label: String) {
        ALL("All"),
        BUDGET("Budget (<15k)"),
        MID("Mid (15k-30k)"),
        PREMIUM("Premium (30k-65k)"),
        LUXURY("Luxury (>65k)")
    }

    fun getRecommendation(price: Double, isAvailable: Boolean): String = when {
        isAvailable && price < 30000 -> "⭐ Recommended: Available and affordable!"
        isAvailable && price >= 30000 -> "Available (Premium)"
        !isAvailable -> "Currently Booked"
        else -> "Check Details"
    }

    fun matchesPriceCategory(price: Double, category: PriceCategory): Boolean = when (category) {
        PriceCategory.ALL -> true
        PriceCategory.BUDGET -> price < 15000.0
        PriceCategory.MID -> price in 15000.0..30000.0
        PriceCategory.PREMIUM -> price > 30000.0 && price <= 65000.0
        PriceCategory.LUXURY -> price > 65000.0
    }
}
