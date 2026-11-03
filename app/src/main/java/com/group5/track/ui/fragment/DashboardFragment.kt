package com.group5.track.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.group5.track.R
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

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

        loadDashboardMetrics()
    }

    private fun loadDashboardMetrics() {
        val properties = listOf(
            PropertyEntity(1, "Modern 2-Bed Studio", "Kilimani, Nairobi", 28000.0, 2, true),
            PropertyEntity(2, "Cozy Budget Bedsitter", "Roysambu, Nairobi", 12000.0, 1, true),
            PropertyEntity(3, "Luxury Executive Villa", "Karen, Nairobi", 75000.0, 4, false),
            PropertyEntity(4, "Urban Penthouse Suite", "Westlands, Nairobi", 90000.0, 3, true)
        )

        // Calculate summary counts
        val totalCount = properties.size
        val availableCount = properties.count { it.isAvailable }
        val bookedCount = properties.count { !it.isAvailable }

        // Update UI metric cards
        binding.tvMetricTotal.text = totalCount.toString()
        binding.tvMetricAvailable.text = availableCount.toString()
        binding.tvMetricBooked.text = bookedCount.toString()

        // Bind highest price or featured item to the included property card
        val featuredProperty = properties.maxByOrNull { it.monthlyPrice }
        featuredProperty?.let { bindFeaturedCard(it) }
    }

    private fun bindFeaturedCard(property: PropertyEntity) {
        // Accessing views inside the XML <include> tag
        val cardBinding = binding.cardRecommendedProperty

        cardBinding.tvTitle.text = property.title
        cardBinding.tvAddress.text = property.address
        cardBinding.tvPrice.text = "KES ${String.format("%,.0f", property.monthlyPrice)}/mo"
        cardBinding.tvBedrooms.text = "${property.bedrooms} Beds"

        if (property.isAvailable) {
            cardBinding.tvStatusBadge.text = "Available"
            cardBinding.tvStatusBadge.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.status_available_bg)
            )
            cardBinding.tvStatusBadge.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.status_available_text)
            )
        } else {
            cardBinding.tvStatusBadge.text = "Booked"
            cardBinding.tvStatusBadge.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.status_booked_bg)
            )
            cardBinding.tvStatusBadge.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.status_booked_text)
            )
        }

        cardBinding.root.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Featured Property: ${property.title}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}