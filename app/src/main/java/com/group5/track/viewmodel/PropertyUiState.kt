package com.group5.track.viewmodel

import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.domain.PropertyDomainHelper.PriceCategory
import com.group5.track.domain.ValidationResult

data class PropertyUiState(
    val properties: List<PropertyEntity> = emptyList(),
    val totalCount: Int = 0,
    val availableCount: Int = 0,
    val bookedCount: Int = 0,
    val recommendedProperty: PropertyEntity? = null,
    val searchQuery: String = "",
    val selectedPropertyType: String = "All", // All, House, Apartment, Commercial
    val selectedPriceCategory: PriceCategory = PriceCategory.ALL,
    val isLoading: Boolean = true,
    val validationState: ValidationResult = ValidationResult(isValid = true),
    val userMessage: String? = null,
    val lastDeletedProperty: PropertyEntity? = null,
    val kesToUsdRate: Double = 0.0077,
    val kesToEurRate: Double = 0.0071,
    val isExchangeRateLoading: Boolean = false
)
