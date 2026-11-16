package com.group5.track.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.data.repository.PropertyRepository
import com.group5.track.domain.PropertyDomainHelper
import com.group5.track.domain.PropertyDomainHelper.PriceCategory
import com.group5.track.domain.PropertyValidator
import com.group5.track.domain.ValidationResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PropertyViewModel(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow("All")
    private val _selectedPriceCategory = MutableStateFlow(PriceCategory.ALL)
    private val _validationState = MutableStateFlow(ValidationResult(isValid = true))
    private val _userMessage = MutableStateFlow<String?>(null)
    private val _lastDeletedProperty = MutableStateFlow<PropertyEntity?>(null)

    // Combines Room DB Flow with active search & filter StateFlows
    val uiState: StateFlow<PropertyUiState> = combine(
        repository.getAllProperties(),
        _searchQuery,
        _selectedType,
        _selectedPriceCategory,
        _validationState,
        _userMessage,
        _lastDeletedProperty
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val allProperties = values[0] as List<PropertyEntity>
        val query = values[1] as String
        val typeFilter = values[2] as String
        val priceFilter = values[3] as PriceCategory
        val validation = values[4] as ValidationResult
        val message = values[5] as String?
        val deleted = values[6] as PropertyEntity?

        val filteredList = allProperties.filter { property ->
            val matchesQuery = query.isBlank() ||
                    property.title.contains(query, ignoreCase = true) ||
                    property.address.contains(query, ignoreCase = true)

            val matchesType = typeFilter.equals("All", ignoreCase = true) ||
                    property.propertyType.equals(typeFilter, ignoreCase = true)

            val matchesPrice = PropertyDomainHelper.matchesPriceCategory(
                property.monthlyPrice,
                priceFilter
            )

            matchesQuery && matchesType && matchesPrice
        }

        val available = allProperties.count { it.isAvailable }
        val booked = allProperties.size - available
        val recommended = allProperties.firstOrNull { it.isAvailable && it.monthlyPrice < 30000.0 }
            ?: allProperties.firstOrNull { it.isAvailable }

        PropertyUiState(
            properties = filteredList,
            totalCount = allProperties.size,
            availableCount = available,
            bookedCount = booked,
            recommendedProperty = recommended,
            searchQuery = query,
            selectedPropertyType = typeFilter,
            selectedPriceCategory = priceFilter,
            isLoading = false,
            validationState = validation,
            userMessage = message,
            lastDeletedProperty = deleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PropertyUiState(isLoading = true)
    )

    // --- Search & Filter Actions ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updatePropertyTypeFilter(type: String) {
        _selectedType.value = type
    }

    fun updatePriceCategoryFilter(category: PriceCategory) {
        _selectedPriceCategory.value = category
    }

    // --- CREATE ---
    fun addProperty(
        title: String,
        address: String,
        propertyType: String,
        priceInput: String,
        bedroomsInput: String,
        agentPhone: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        val validation = PropertyValidator.validatePropertyInput(
            title, address, priceInput, bedroomsInput, agentPhone
        )
        _validationState.value = validation

        if (!validation.isValid) return

        viewModelScope.launch {
            val newProperty = PropertyEntity(
                title = title.trim(),
                address = address.trim(),
                propertyType = propertyType,
                monthlyPrice = priceInput.toDouble(),
                bedrooms = bedroomsInput.toInt(),
                isAvailable = true,
                agentPhone = agentPhone.trim(),
                description = description.trim(),
                createdAt = System.currentTimeMillis()
            )
            repository.insertProperty(newProperty)
            _userMessage.value = "Property listed successfully"
            onSuccess()
        }
    }

    // --- READ SINGLE ---
    suspend fun getPropertyById(id: Long): PropertyEntity? {
        return repository.getPropertyById(id)
    }

    // --- UPDATE ---
    fun updateProperty(
        existingProperty: PropertyEntity,
        title: String,
        address: String,
        propertyType: String,
        priceInput: String,
        bedroomsInput: String,
        isAvailable: Boolean,
        agentPhone: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        val validation = PropertyValidator.validatePropertyInput(
            title, address, priceInput, bedroomsInput, agentPhone
        )
        _validationState.value = validation

        if (!validation.isValid) return

        viewModelScope.launch {
            val updated = existingProperty.copy(
                title = title.trim(),
                address = address.trim(),
                propertyType = propertyType,
                monthlyPrice = priceInput.toDouble(),
                bedrooms = bedroomsInput.toInt(),
                isAvailable = isAvailable,
                agentPhone = agentPhone.trim(),
                description = description.trim()
            )
            repository.updateProperty(updated)
            _userMessage.value = "Property updated successfully"
            onSuccess()
        }
    }

    fun toggleBookingStatus(propertyId: Long) {
        viewModelScope.launch {
            val property = repository.getPropertyById(propertyId) ?: return@launch
            val updated = property.copy(isAvailable = !property.isAvailable)
            repository.updateProperty(updated)
            _userMessage.value = if (updated.isAvailable) "Marked as Available" else "Marked as Booked"
        }
    }

    // --- DELETE & UNDO ---
    fun deleteProperty(property: PropertyEntity) {
        viewModelScope.launch {
            _lastDeletedProperty.value = property
            repository.deleteProperty(property)
            _userMessage.value = "Property deleted"
        }
    }

    fun undoDelete() {
        val propertyToRestore = _lastDeletedProperty.value ?: return
        viewModelScope.launch {
            repository.insertProperty(propertyToRestore)
            _lastDeletedProperty.value = null
            _userMessage.value = "Property restored"
        }
    }

    fun clearValidationErrors() {
        _validationState.value = ValidationResult(isValid = true)
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}

class PropertyViewModelFactory(
    private val repository: PropertyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
