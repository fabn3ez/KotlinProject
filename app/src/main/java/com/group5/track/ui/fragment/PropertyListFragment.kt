package com.group5.track.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.group5.track.R
import com.group5.track.TrackApplication
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.databinding.FragmentPropertyListBinding
import com.group5.track.domain.PropertyDomainHelper.PriceCategory
import com.group5.track.ui.adapter.PropertyListAdapter
import com.group5.track.viewmodel.PropertyViewModel
import com.group5.track.viewmodel.PropertyViewModelFactory
import kotlinx.coroutines.launch

class PropertyListFragment : Fragment() {

    private var _binding: FragmentPropertyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var propertyAdapter: PropertyListAdapter

    // 1. Initialize shared PropertyViewModel
    private val viewModel: PropertyViewModel by activityViewModels {
        val app = requireActivity().application as TrackApplication
        PropertyViewModelFactory(app.repository)
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
        observeViewModelState()
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
        // 2. Forward search text directly to ViewModel
        binding.etSearchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateSearchQuery(s?.toString()?.trim().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 3. Forward price chip selections directly to ViewModel
        binding.chipGroupPrice.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedChipId = checkedIds.firstOrNull() ?: View.NO_ID
            val category = when (checkedChipId) {
                R.id.chipBudget -> PriceCategory.BUDGET
                R.id.chipMid -> PriceCategory.MID
                R.id.chipPremium -> PriceCategory.PREMIUM
                else -> PriceCategory.ALL
            }
            viewModel.updatePriceCategoryFilter(category)
        }
    }

    // 4. Observe live filtered list and UX states from PropertyViewModel
    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.properties.isEmpty()) {
                        binding.layoutEmptyState.visibility = View.VISIBLE
                        binding.rvPropertyList.visibility = View.GONE
                    } else {
                        binding.layoutEmptyState.visibility = View.GONE
                        binding.rvPropertyList.visibility = View.VISIBLE
                    }

                    propertyAdapter.submitList(state.properties)

                    state.userMessage?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        viewModel.clearUserMessage()
                    }
                }
            }
        }
    }

    // 5. Delete Confirmation Dialog + Undo Snackbar (ready for card delete/swipe triggers)
    fun confirmAndDeleteProperty(property: PropertyEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Property")
            .setMessage("Are you sure you want to remove '${property.title}'?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteProperty(property)
                Snackbar.make(requireView(), "Listing removed", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.undoDelete()
                    }
                    .show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clears binding reference to prevent memory leaks
    }
}