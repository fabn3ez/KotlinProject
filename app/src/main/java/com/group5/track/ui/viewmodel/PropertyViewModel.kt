package com.group5.track.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.group5.track.TrackApplication
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.data.repository.PropertyRepository
import kotlinx.coroutines.launch

/**
 * Stage 2: ViewModel Implementation
 * Manages UI-related data and business logic for Property listing and Dashboard metrics.
 * Uses LiveData and transformations to prepare data for UI consumption without coupling to Views.
 */
class PropertyViewModel(application: Application) : AndroidViewModel(application) {

    // Access the repository via Application class (safe from Activity context leaks)
    private val repository: PropertyRepository =
        (application as TrackApplication).repository

    // ── 1. CORE DATA ──────────────────────────────────────────────────────────
    // Observes Room DB changes via Flow converted to LiveData
    val properties: LiveData<List<PropertyEntity>> = repository.getAllProperties().asLiveData()
    val availableProperties: LiveData<List<PropertyEntity>> = repository.getAvailableProperties().asLiveData()

    // ── 2. TRANSFORMATIONS: Data formatted and ready for Screens ──────────────
    // Total count metric
    val totalCountText: LiveData<String> = properties.map { list ->
        list.size.toString()
    }

    // Available count metric
    val availableCountText: LiveData<String> = properties.map { list ->
        list.count { it.isAvailable }.toString()
    }

    // Booked / Occupied count metric
    val bookedCountText: LiveData<String> = properties.map { list ->
        list.count { !it.isAvailable }.toString()
    }

    // Total monthly rental value calculation
    val totalRentText: LiveData<String> = properties.map { list ->
        val total = list.sumOf { it.monthlyPrice }
        "KSh %,.0f".format(total)
    }

    // Summary text of all properties (Lab 8 pattern)
    val allPropertiesSummary: LiveData<String> = properties.map { list ->
        if (list.isEmpty()) {
            "No properties listed yet."
        } else {
            list.joinToString("\n\n") { property ->
                "Property: ${property.title}\nAddress: ${property.address}\nPrice: KSh %,.0f/mo\nStatus: ${if (property.isAvailable) "Available" else "Booked"}"
                    .format(property.monthlyPrice)
            }
        }
    }

    // ── 3. ACTIONS / BUSINESS LOGIC ───────────────────────────────────────────
    /**
     * Validates input fields and inserts a new property into the database.
     * Returns true if valid and dispatched; false if validation fails.
     */
    fun addProperty(
        title: String,
        address: String,
        propertyType: String,
        priceString: String,
        bedroomsString: String,
        agentPhone: String,
        description: String
    ): Boolean {
        val cleanTitle = title.trim()
        val cleanAddress = address.trim()
        val cleanPhone = agentPhone.trim()
        val price = priceString.trim().toDoubleOrNull()
        val bedrooms = bedroomsString.trim().toIntOrNull() ?: 1

        // Validation rule: mandatory fields
        if (cleanTitle.isEmpty() || cleanAddress.isEmpty() || cleanPhone.isEmpty() || price == null || price <= 0.0) {
            return false
        }

        val newProperty = PropertyEntity(
            title = cleanTitle,
            address = cleanAddress,
            propertyType = propertyType.ifBlank { "Apartment" },
            monthlyPrice = price,
            bedrooms = bedrooms,
            isAvailable = true,
            agentPhone = cleanPhone,
            description = description.trim()
        )

        viewModelScope.launch {
            repository.insertProperty(newProperty)
        }
        return true
    }

    /**
     * Toggles availability status of a property (Available <-> Booked)
     */
    fun toggleAvailability(propertyId: Long) {
        viewModelScope.launch {
            repository.toggleAvailability(propertyId)
        }
    }

    /**
     * Deletes a property from the database
     */
    fun deleteProperty(property: PropertyEntity) {
        viewModelScope.launch {
            repository.deleteProperty(property)
        }
    }

    // ── 4. LIFECYCLE LOGS (Required by Lab 8 specification) ────────────────────
    init {
        Log.d("PROPERTY_LIFECYCLE", "PropertyViewModel: created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("PROPERTY_LIFECYCLE", "PropertyViewModel: onCleared")
    }
}
