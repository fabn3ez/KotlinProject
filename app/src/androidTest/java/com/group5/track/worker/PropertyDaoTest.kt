package com.group5.track.worker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.group5.track.data.local.database.AppDatabase
import com.group5.track.data.local.dao.PropertyDao
import com.group5.track.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PropertyDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: PropertyDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.propertyDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun sampleProperty(
        title: String = "Kilimani 2BR",
        createdAt: Long = 1_000L,
        isAvailable: Boolean = true
    ) = PropertyEntity(
        id = 0,
        title = title,
        address = "Kilimani, Nairobi",
        propertyType = "Apartment",
        monthlyPrice = 25_000.0,
        bedrooms = 2,
        isAvailable = isAvailable,
        agentPhone = "0712345678",
        description = "Test listing",
        createdAt = createdAt
    )

    @Test
    fun insert_thenGetById_returnsSameProperty() = runBlocking<Unit> {
        val id = dao.insertProperty(sampleProperty(title = "Kilimani 2BR"))
        val loaded = dao.getPropertyById(id)

        assertThat(loaded).isNotNull()
        assertThat(loaded?.title).isEqualTo("Kilimani 2BR")
    }

    @Test
    fun update_changesStoredValues() = runBlocking<Unit> {
        val id = dao.insertProperty(sampleProperty())
        val original = dao.getPropertyById(id)!!

        dao.updateProperty(original.copy(title = "Updated Title", isAvailable = false))

        val updated = dao.getPropertyById(id)!!
        assertThat(updated.title).isEqualTo("Updated Title")
        assertThat(updated.isAvailable).isFalse()
    }

    @Test
    fun delete_removesProperty() = runBlocking<Unit> {
        val id = dao.insertProperty(sampleProperty())
        val saved = dao.getPropertyById(id)!!

        dao.deleteProperty(saved)

        assertThat(dao.getPropertyById(id)).isNull()
    }

    @Test
    fun getAllProperties_ordersByCreatedAtDescending() = runBlocking<Unit> {
        dao.insertProperty(sampleProperty(title = "A", createdAt = 1_000L))
        dao.insertProperty(sampleProperty(title = "B", createdAt = 3_000L))
        dao.insertProperty(sampleProperty(title = "C", createdAt = 2_000L))

        val titles = dao.getAllProperties().first().map { it.title }

        assertThat(titles).containsExactly("B", "C", "A").inOrder()
    }

    @Test
    fun getAvailableProperties_excludesBookedListings() = runBlocking<Unit> {
        dao.insertProperty(sampleProperty(title = "Open", isAvailable = true))
        dao.insertProperty(sampleProperty(title = "Booked", isAvailable = false))

        val titles = dao.getAvailableProperties().first().map { it.title }

        assertThat(titles).containsExactly("Open")
    }
}
