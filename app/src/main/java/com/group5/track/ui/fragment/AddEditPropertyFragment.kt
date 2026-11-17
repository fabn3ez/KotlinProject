package com.group5.track.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.group5.track.TrackApplication
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.databinding.FragmentAddEditPropertyBinding
import com.group5.track.viewmodel.PropertyViewModel
import com.group5.track.viewmodel.PropertyViewModelFactory
import kotlinx.coroutines.launch

class AddEditPropertyFragment : Fragment() {

    private var _binding: FragmentAddEditPropertyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertyViewModel by activityViewModels {
        val app = requireActivity().application as TrackApplication
        PropertyViewModelFactory(app.repository)
    }

    private var existingProperty: PropertyEntity? = null
    private val propertyTypes = listOf("Apartment", "House", "Commercial")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.clearValidationErrors()
        setupPropertyTypeSpinner()
        checkEditMode()
        observeValidationState()

        binding.btnSaveProperty.setOnClickListener {
            submitForm()
        }
    }

    private fun setupPropertyTypeSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            propertyTypes
        )
        binding.spinnerPropertyType.adapter = adapter
    }

    private fun checkEditMode() {
        val propertyId = arguments?.getLong("propertyId", -1L) ?: -1L
        if (propertyId > 0L) {
            binding.tvFormHeader.text = "Edit Property"
            binding.btnSaveProperty.text = "Update Property"

            viewLifecycleOwner.lifecycleScope.launch {
                val property = viewModel.getPropertyById(propertyId)
                if (property != null) {
                    existingProperty = property
                    binding.etTitle.setText(property.title)
                    binding.etAddress.setText(property.address)
                    binding.etPrice.setText(property.monthlyPrice.toString())
                    binding.etBedrooms.setText(property.bedrooms.toString())
                    binding.etPhone.setText(property.agentPhone)
                    binding.etDescription.setText(property.description)
                    binding.switchAvailable.isChecked = property.isAvailable

                    val typeIndex = propertyTypes.indexOfFirst {
                        it.equals(property.propertyType, ignoreCase = true)
                    }
                    if (typeIndex >= 0) {
                        binding.spinnerPropertyType.setSelection(typeIndex)
                    }
                }
            }
        }
    }

    private fun observeValidationState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val errors = state.validationState
                    binding.tilTitle.error = errors.titleError
                    binding.tilAddress.error = errors.addressError
                    binding.tilPrice.error = errors.priceError
                    binding.tilBedrooms.error = errors.bedroomsError
                    binding.tilPhone.error = errors.phoneError
                }
            }
        }
    }

    private fun submitForm() {
        val title = binding.etTitle.text?.toString().orEmpty()
        val address = binding.etAddress.text?.toString().orEmpty()
        val selectedType = binding.spinnerPropertyType.selectedItem?.toString() ?: "Apartment"
        val priceInput = binding.etPrice.text?.toString().orEmpty()
        val bedroomsInput = binding.etBedrooms.text?.toString().orEmpty()
        val phone = binding.etPhone.text?.toString().orEmpty()
        val description = binding.etDescription.text?.toString().orEmpty()
        val isAvailable = binding.switchAvailable.isChecked

        val propertyToUpdate = existingProperty
        if (propertyToUpdate == null) {
            // CREATE
            viewModel.addProperty(
                title = title,
                address = address,
                propertyType = selectedType,
                priceInput = priceInput,
                bedroomsInput = bedroomsInput,
                agentPhone = phone,
                description = description
            ) {
                Toast.makeText(requireContext(), "Property added!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        } else {
            // UPDATE
            viewModel.updateProperty(
                existingProperty = propertyToUpdate,
                title = title,
                address = address,
                propertyType = selectedType,
                priceInput = priceInput,
                bedroomsInput = bedroomsInput,
                isAvailable = isAvailable,
                agentPhone = phone,
                description = description
            ) {
                Toast.makeText(requireContext(), "Property updated!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}