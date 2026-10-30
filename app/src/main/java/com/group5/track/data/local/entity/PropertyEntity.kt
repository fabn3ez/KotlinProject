package com.group5.track.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "properties",
    indices = [
        Index(value = ["is_available"]),
        Index(value = ["monthly_price"]),
        Index(value = ["property_type"])
    ]
)
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "property_type")
    val propertyType: String,

    @ColumnInfo(name = "monthly_price")
    val monthlyPrice: Double,

    @ColumnInfo(name = "bedrooms")
    val bedrooms: Int,

    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean,

    @ColumnInfo(name = "agent_phone")
    val agentPhone: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
