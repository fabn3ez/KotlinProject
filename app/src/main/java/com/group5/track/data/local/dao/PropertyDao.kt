package com.group5.track.data.local.dao

import androidx.room.*
import com.group5.track.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity): Long

    @Update
    suspend fun updateProperty(property: PropertyEntity)

    @Delete
    suspend fun deleteProperty(property: PropertyEntity)

    @Query("SELECT * FROM properties ORDER BY created_at DESC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE is_available = 1 ORDER BY created_at DESC")
    fun getAvailableProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyById(id: Long): PropertyEntity?
}
