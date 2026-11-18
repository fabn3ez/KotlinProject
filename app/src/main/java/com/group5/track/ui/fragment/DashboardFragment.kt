package com.group5.track.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.group5.track.databinding.FragmentDashboardBinding
import com.group5.track.viewmodel.PropertyViewModel
import com.group5.track.viewmodel.PropertyViewModelFactory
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Same activity-scoped ViewModel as the list, detail and add/edit screens
    private val viewModel: PropertyViewModel by activityViewModels {
        val app = requireActivity().application as TrackApplication
        PropertyViewModelFactory(app.repository, app.currencyRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeDashboardMetrics()
    }

    // Live counts and recommended listing come from Room via the ViewModel
    private fun observeDashboardMetrics() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect

                    binding.tvMetricTotal.text = state.totalCount.toString()
                    binding.tvMetricAvailable.text = state.availableCount.toString()
                    binding.tvMetricBooked.text = state.bookedCount.toString()

                    val recommended = state.recommendedProperty
                    if (recommended != null) {
                        binding.cardRecommendedProperty.root.visibility = View.VISIBLE
                        bindFeaturedCard(recommended)
                    } else {
                        binding.cardRecommendedProperty.root.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun bindFeaturedCard(property: PropertyEntity) {
        // Accessing views inside the XML <include> tag
        val cardBinding = binding.cardRecommendedProperty

        cardBinding.tvTitle.text = property.title
        cardBinding.tvAddress.text = property.address
        cardBinding.tvPrice.text = "KES ${String.format("%,.0f", property.monthlyPrice)}/mo"
        cardBinding.tvBedrooms.text = if (property.bedrooms == 1) "1 Bed" else "${property.bedrooms} Beds"

        if (property.isAvailable) {
            cardBinding.tvStatusBadge.text = "Available"
            cardBinding.tvStatusBadge.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.status_available_bg)
            cardBinding.tvStatusBadge.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.status_available_text)
            )
        } else {
            cardBinding.tvStatusBadge.text = "Booked"
            cardBinding.tvStatusBadge.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.status_booked_bg)
            cardBinding.tvStatusBadge.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.status_booked_text)
            )
        }

        // Open the detail screen for the featured property
        cardBinding.root.setOnClickListener {
            findNavController().navigate(
                R.id.propertyDetailFragment,
                bundleOf("propertyId" to property.id)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}