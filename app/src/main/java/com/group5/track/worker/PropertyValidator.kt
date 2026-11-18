package com.group5.track.worker

object PropertyValidator {

    private val kenyanPhoneRegex = Regex("^(\\+?254|0)[17]\\d{8}$")

    fun isValidPhone(phone: String): Boolean {
        return kenyanPhoneRegex.matches(phone)
    }

    fun isValidPrice(price: Double): Boolean {
        return price > 0.0
    }

    fun isValidBedrooms(bedrooms: Int): Boolean {
        return bedrooms > 0
    }

    fun isNotBlank(text: String): Boolean {
        return text.isNotBlank()
    }
}
