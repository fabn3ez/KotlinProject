package com.group5.track.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.group5.track.R
import com.group5.track.TrackApplication
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.databinding.FragmentPropertyListBinding
import com.group5.track.ui.adapter.PropertyListAdapter
import com.group5.track.viewmodel.PropertyViewModel
import com.group5.track.viewmodel.PropertyViewModelFactory
import com.group5.track.worker.ListingAuditScheduler
import kotlinx.coroutines.launch

class PropertyListFragment : Fragment() {

    private var _binding: FragmentPropertyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var propertyAdapter: PropertyListAdapter

    // Properties currently stored in Room (before the local search/price filters)
    private var rawPropertyList = listOf<PropertyEntity>()

    private val trackApp get() = requireActivity().application as TrackApplication

    // Shared with the detail and add/edit screens
    private val viewModel: PropertyViewModel by activityViewModels {
        PropertyViewModelFactory(trackApp.repository, trackApp.currencyRepository)
    }

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
        observeProperties()
        setupRoleControls()
    }

    private fun setupRecyclerView() {
        propertyAdapter = PropertyListAdapter { selectedProperty ->
            // Open the detail screen for the tapped property
            findNavController().navigate(
                R.id.action_propertyListFragment_to_propertyDetailFragment,
                bundleOf("propertyId" to selectedProperty.id)
            )
        }
        binding.rvPropertyList.adapter = propertyAdapter
    }

    // Collect the Room-backed list from the ViewModel and refresh the RecyclerView
    private fun observeProperties() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect
                    rawPropertyList = state.properties
                    applyFilters()
                }
            }
        }
    }

    // Tenant/Agent switch and the agent-only "Add listing" button
    private fun setupRoleControls() {
        binding.switchAgentMode.setOnCheckedChangeListener { _, isChecked ->
            trackApp.isAgentMode.value = isChecked
        }
        binding.fabAddProperty.setOnClickListener {
            findNavController().navigate(R.id.addEditPropertyFragment)
        }
        binding.btnRunAudit.setOnClickListener {
            // staleDays = 0 so every available listing triggers the notification in the demo
            ListingAuditScheduler.runNow(requireContext(), staleDays = 0)
            Toast.makeText(
                requireContext(),
                "Audit started. Check your notifications.",
                Toast.LENGTH_SHORT
            ).show()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                trackApp.isAgentMode.collect { isAgent ->
                    if (binding.switchAgentMode.isChecked != isAgent) {
                        binding.switchAgentMode.isChecked = isAgent
                    }
                    binding.fabAddProperty.visibility = if (isAgent) View.VISIBLE else View.GONE
                    binding.btnRunAudit.visibility = if (isAgent) View.VISIBLE else View.GONE
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clears binding reference to prevent memory leaks
    }
}