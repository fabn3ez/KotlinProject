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
            PropertyEntity(
                id = 1,
                title = "Modern 2-Bedroom Studio",
                address = "Kilimani, Nairobi",
                propertyType = "Apartment",
                monthlyPrice = 28000.0,
                bedrooms = 2,
                isAvailable = true,
                agentPhone = "0712345678",
                description = "Modern apartment in Kilimani"
            ),

            PropertyEntity(
                id = 2,
                title = "Cozy Budget Bedsitter",
                address = "Roysambu, Nairobi",
                propertyType = "Bedsitter",
                monthlyPrice = 12000.0,
                bedrooms = 1,
                isAvailable = true,
                agentPhone = "0723456789",
                description = "Affordable bedsitter near Roysambu"
            ),

            PropertyEntity(
                id = 3,
                title = "Luxury Executive Villa",
                address = "Karen, Nairobi",
                propertyType = "House",
                monthlyPrice = 75000.0,
                bedrooms = 4,
                isAvailable = false,
                agentPhone = "0734567890",
                description = "Luxury villa located in Karen"
            ),

            PropertyEntity(
                id = 4,
                title = "Urban Penthouse Suite",
                address = "Westlands, Nairobi",
                propertyType = "Apartment",
                monthlyPrice = 90000.0,
                bedrooms = 3,
                isAvailable = true,
                agentPhone = "0745678901",
                description = "Premium penthouse suite in Westlands"
            )
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