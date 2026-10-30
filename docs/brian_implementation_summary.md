# Room Database Layer Summary

**Branch**: `feature/database-room-brian`  
**Module**: `app` (Local Data Persistence)

---

## Overview

This module sets up Android Room database persistence for the Track rental property listing application, providing a local SQLite store and repository for ViewModel access.

---

## Components

### 1. PropertyEntity
**Location**: `app/src/main/java/com/group5/track/data/local/entity/PropertyEntity.kt`

Represents a rental listing in the `properties` table:
- `id`: Auto-generated Long primary key
- `title`: Property title
- `address`: Full property address
- `propertyType`: Category ("House", "Apartment", "Commercial")
- `monthlyPrice`: Rent in KES
- `bedrooms`: Number of bedrooms
- `isAvailable`: Availability boolean (true = Available, false = Booked)
- `agentPhone`: Contact phone number
- `description`: Listing details and description
- `createdAt`: Timestamp for sorting

Indices are added on `is_available`, `monthly_price`, and `property_type` for common query filters.

---

### 2. PropertyDao
**Location**: `app/src/main/java/com/group5/track/data/local/dao/PropertyDao.kt`

Data access object defining operations on `properties`:
- `insertProperty(property: PropertyEntity): Long` (suspend)
- `updateProperty(property: PropertyEntity)` (suspend)
- `deleteProperty(property: PropertyEntity)` (suspend)
- `getAllProperties(): Flow<List<PropertyEntity>>` (reactive query, newest first)
- `getAvailableProperties(): Flow<List<PropertyEntity>>` (reactive filter query)
- `getPropertyById(id: Long): PropertyEntity?` (single item fetch)

---

### 3. AppDatabase
**Location**: `app/src/main/java/com/group5/track/data/local/database/AppDatabase.kt`

Room database singleton with thread-safe access:
- Database name: `"track_database.db"`
- Version: `1`
- Includes initial database callback to pre-populate sample properties for testing.

---

### 4. PropertyRepository
**Location**: `app/src/main/java/com/group5/track/data/repository/PropertyRepository.kt`

Repository coordinating data access for the UI and ViewModels:
- Exposes `getAllProperties()` and `getAvailableProperties()` as `Flow` streams.
- Runs write operations (`insert`, `update`, `delete`) on background threads (`Dispatchers.IO`).
- Provides helpers for toggling property availability and calculating price brackets / recommendation tags.

---

## Integration Guide

### For ViewModel (Baraka):
Access the repository through `TrackApplication`:
```kotlin
val repository = (application as TrackApplication).repository

// Collect properties flow
val properties = repository.getAllProperties()
```

### For UI / RecyclerView (Daniella):
- Use `PropertyEntity` directly in adapters.
- Fields match all required card elements (title, address, price, bedrooms, availability).
