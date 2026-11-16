package com.group5.track.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.group5.track.R
import com.group5.track.TrackApplication
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.databinding.FragmentPropertyDetailBinding
import com.group5.track.domain.PropertyDomainHelper
import com.group5.track.viewmodel.PropertyViewModel
import com.group5.track.viewmodel.PropertyViewModelFactory
import kotlinx.coroutines.launch

class PropertyDetailFragment : Fragment() {

    private var _binding: FragmentPropertyDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertyViewModel by activityViewModels {
        val app = requireActivity().application as TrackApplication
        PropertyViewModelFactory(app.repository)
    }

    private var currentProperty: PropertyEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val propertyId = arguments?.getLong("propertyId", -1L) ?: -1L
        observePropertyDetails(propertyId)
        setupActionButtons()
    }

    private fun observePropertyDetails(propertyId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val property = state.properties.find { it.id == propertyId }
                        ?: viewModel.getPropertyById(propertyId)

                    if (property != null) {
                        currentProperty = property
                        bindProperty(property)
                    }
                }
            }
        }
    }

    private fun bindProperty(property: PropertyEntity) {
        binding.tvTitle.text = property.title
        binding.tvAddress.text = property.address
        binding.tvPrice.text = "KES ${String.format("%,.0f", property.monthlyPrice)} / month"
        binding.tvBedrooms.text = "${property.bedrooms} Bedrooms (${property.propertyType})"
        binding.tvDescription.text = property.description.ifBlank { "No description provided." }
        binding.tvRecommendation.text = PropertyDomainHelper.getRecommendation(
            property.monthlyPrice,
            property.isAvailable
        )
        binding.btnToggleStatus.text = if (property.isAvailable) {
            "Mark as Booked"
        } else {
            "Mark as Available"
        }
    }

    private fun setupActionButtons() {
        binding.btnCall.setOnClickListener {
            val phone = currentProperty?.agentPhone ?: "0712345678"
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            startActivity(intent)
        }

        binding.btnBook.setOnClickListener {
            val property = currentProperty
            val phone = property?.agentPhone ?: "0712345678"
            val message = if (property != null) {
                "Hi, I am interested in viewing property #${property.id} (${property.title}) at ${property.address}."
            } else {
                "Hi, I am interested in viewing this property."
            }

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phone")
                putExtra("sms_body", message)
            }
            startActivity(intent)
        }

        // Instant toggle booking status
        binding.btnToggleStatus.setOnClickListener {
            val property = currentProperty ?: return@setOnClickListener
            viewModel.toggleBookingStatus(property.id)
        }

        // Navigate to Edit screen passing propertyId
        binding.btnEditProperty.setOnClickListener {
            val property = currentProperty ?: return@setOnClickListener
            val args = bundleOf("propertyId" to property.id)
            // Update destination ID if nav_graph uses a specific action ID
            findNavController().navigate(R.id.addEditPropertyFragment, args)
        }

        // Delete with confirmation dialog + Undo Snackbar
        binding.btnDeleteProperty.setOnClickListener {
            val property = currentProperty ?: return@setOnClickListener
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Property")
                .setMessage("Are you sure you want to remove '${property.title}'?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteProperty(property)
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Listing removed",
                        Snackbar.LENGTH_LONG
                    ).setAction("UNDO") {
                        viewModel.undoDelete()
                    }.show()
                    findNavController().navigateUp()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}