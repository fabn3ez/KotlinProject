package com.group5.track.data.repository

import com.group5.track.data.local.dao.PropertyDao
import com.group5.track.data.local.entity.PropertyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PropertyRepository(private val propertyDao: PropertyDao) {

    fun getAllProperties(): Flow<List<PropertyEntity>> {
        return propertyDao.getAllProperties()
    }

    fun getAvailableProperties(): Flow<List<PropertyEntity>> {
        return propertyDao.getAvailableProperties()
    }

    suspend fun getPropertyById(id: Long): PropertyEntity? {
        return withContext(Dispatchers.IO) {
            propertyDao.getPropertyById(id)
        }
    }

    suspend fun insertProperty(property: PropertyEntity): Long {
        return withContext(Dispatchers.IO) {
            propertyDao.insertProperty(property)
        }
    }

    suspend fun updateProperty(property: PropertyEntity) {
        withContext(Dispatchers.IO) {
            propertyDao.updateProperty(property)
        }
    }

    suspend fun deleteProperty(property: PropertyEntity) {
        withContext(Dispatchers.IO) {
            propertyDao.deleteProperty(property)
        }
    }

    suspend fun toggleAvailability(propertyId: Long) {
        withContext(Dispatchers.IO) {
            val property = propertyDao.getPropertyById(propertyId)
            property?.let {
                val updated = it.copy(isAvailable = !it.isAvailable)
                propertyDao.updateProperty(updated)
            }
        }
    }

    fun getRecommendation(property: PropertyEntity): String {
        return when {
            property.isAvailable && property.monthlyPrice < 30000 -> "⭐ Recommended: Available and affordable!"
            property.isAvailable && property.monthlyPrice >= 30000 -> "Available (Premium)"
            !property.isAvailable -> "Currently Booked"
            else -> "Check Details"
        }
    }

    fun getPriceCategory(price: Double): String {
        return when {
            price < 15000 -> "Budget"
            price < 30000 -> "Mid-range"
            price < 65000 -> "Premium"
            else -> "Luxury"
        }
    }
}
