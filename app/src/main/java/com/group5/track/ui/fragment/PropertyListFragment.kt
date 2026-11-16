package com.group5.track.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.group5.track.R
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.databinding.FragmentPropertyListBinding
import com.group5.track.ui.adapter.PropertyListAdapter

class PropertyListFragment : Fragment() {

    private var _binding: FragmentPropertyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var propertyAdapter: PropertyListAdapter

    // List holding property records
    private var rawPropertyList = listOf<PropertyEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchAndFilters()
        loadSampleData()
    }

    private fun setupRecyclerView() {
        propertyAdapter = PropertyListAdapter { selectedProperty ->
            Toast.makeText(
                requireContext(),
                "Clicked: ${selectedProperty.title}",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.rvPropertyList.adapter = propertyAdapter
    }

    private fun setupSearchAndFilters() {
        // Real-time Search Listener
        binding.etSearchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Price Filter Chips Listener
        binding.chipGroupPrice.setOnCheckedStateChangeListener { _, _ ->
            applyFilters()
        }
    }

    private fun applyFilters() {
        val query = binding.etSearchQuery.text.toString().trim()
        val checkedChipId = binding.chipGroupPrice.checkedChipId

        val filteredList = rawPropertyList.filter { property ->
            // Text Search Matching using title or address)
            val matchesQuery = property.title.contains(query, ignoreCase = true) ||
                    property.address.contains(query, ignoreCase = true)

            // Price range matching
            val matchesPrice = when (checkedChipId) {
                R.id.chipBudget -> property.monthlyPrice < 15000
                R.id.chipMid -> property.monthlyPrice in 15000.0..30000.0
                R.id.chipPremium -> property.monthlyPrice > 30000
                else -> true // R.id.chipAll or unselected
            }

            matchesQuery && matchesPrice
        }

        if (filteredList.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvPropertyList.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvPropertyList.visibility = View.VISIBLE
        }

        // Update RecyclerView with filtered data
        propertyAdapter.submitList(filteredList)
    }

    private fun loadSampleData() {
        // Sample dataset for testing UI before database integration
        rawPropertyList = listOf(
            PropertyEntity(
                id = 1,
                title = "Modern 2-Bed Studio",
                address = "Kilimani, Nairobi",
                monthlyPrice = 28000.0,
                bedrooms = 2,
                isAvailable = true
            ),
            PropertyEntity(
                id = 2,
                title = "Cozy Budget Bedsitter",
                address = "Roysambu, Nairobi",
                monthlyPrice = 12000.0,
                bedrooms = 1,
                isAvailable = true
            ),
            PropertyEntity(
                id = 3,
                title = "Luxury Executive Villa",
                address = "Karen, Nairobi",
                monthlyPrice = 75000.0,
                bedrooms = 4,
                isAvailable = false
            )
        )
        applyFilters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clears binding reference to prevent memory leaks
    }
}