package edu.utap.weatherwizard.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private val units: Array<String> by lazy {
        resources.getStringArray(R.array.units_array)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun initMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Clear menu because we don't want settings icon
                menu.clear()
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Do nothing
                return false
            }
        }, viewLifecycleOwner)
    }

    private fun createAdapterFromResource(arrayResource: Int):
            ArrayAdapter<CharSequence> {
        val adapter = ArrayAdapter.createFromResource(requireActivity().applicationContext,
            arrayResource,
            android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()

        binding.unitsSpinner.adapter = createAdapterFromResource(R.array.units_array)
        val currentUnit = viewModel.observeCurrentUM().value?.units
        val spinnerPosition = units.indexOf(currentUnit)
        var unit = currentUnit
        binding.unitsSpinner.setSelection(spinnerPosition);

        binding.cancelBut.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.okBut.setOnClickListener {
            if(unit != currentUnit) {
                Log.d("XXX", "setting units to $unit")
                val um = viewModel.observeCurrentUM().value!!
                um.units = unit!!
                viewModel.setUnitsMeta(um)
                viewModel.updateUnitsMeta(um, unit!!)
            }

            findNavController().popBackStack()
        }

        binding.unitsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val unitsPos = binding.unitsSpinner.selectedItemPosition
                unit =  units[unitsPos]
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}