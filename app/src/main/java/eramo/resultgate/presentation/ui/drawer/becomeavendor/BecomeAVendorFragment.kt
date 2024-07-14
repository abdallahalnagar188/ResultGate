package eramo.resultgate.presentation.ui.drawer.becomeavendor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentAboutUsBinding
import eramo.resultgate.databinding.FragmentBecomeAVendorBinding
import eramo.resultgate.databinding.FragmentSignupBinding
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BecomeAVendorFragment : Fragment(R.layout.fragment_become_a_vendor) {

    private lateinit var binding: FragmentBecomeAVendorBinding

    private val viewModelShared: SharedViewModel by activityViewModels()
    private val viewModel:BecomeAVendorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBecomeAVendorBinding.bind(view)
        setupToolbar()
        spinner()
        clickListeners()
        fetch()

    }

    private fun fetch() {
        fetchBecomeAVendor()
    }

    private fun fetchBecomeAVendor() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.becomeAVendorState.collect{
                    when(it){
                        is UiState.Loading -> {
                            LoadingDialog.showDialog()
                        }
                        is UiState.Success -> {
                            Toast.makeText(requireContext(), it.data?.message!!, Toast.LENGTH_SHORT).show()
                            LoadingDialog.dismissDialog()
                           if(it.data.status == 200){
                               findNavController().navigate(R.id.homeFragment)
                           }
                        }
                        is UiState.Error -> {
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                            LoadingDialog.dismissDialog()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun clickListeners() {
        binding.apply {
            submitBtn.setOnClickListener(){
                if (isValidForm()){
                    viewModel.becomeAVendor(
                       name =    etFullName.text.toString(),
                       email =  etEmail.text.toString(),
                       phone =  etPhone.text.toString(),
                       type =  typeSpinner.spinner.selectedItem.toString(),
                       message =  etMessage.text.toString()
                    )
                }else{
                    Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields_correctly), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        var isValid = true

        binding.apply {
            if (etFullName.text.toString().isEmpty()){
                isValid = false
            }
            if (etEmail.text.toString().isEmpty()){
                isValid = false
            }
            if (etPhone.text.toString().isEmpty()) {
                isValid = false
            }
            if (typeSpinner.spinner.selectedItemPosition == 0) {
                isValid = false
            }
            if (etMessage.text.toString().isEmpty()) {
                isValid = false
            }
        }
        return isValid
    }

    private fun spinner() {
        binding.apply {
            typeSpinner.spinner.adapter = ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.type_array))
        }
    }

    private fun setupToolbar() {
        binding.apply {
            inTbLayout.toolbarIvMenu.setOnClickListener {
                viewModelShared.openDrawer.value = true
            }

            inTbLayout.toolbarInNotification.root.setOnClickListener {
                if (UserUtil.isUserLogin())
                    findNavController().navigate(
                        R.id.notificationFragment,
                        null,
                        navOptionsAnimation()
                    )
                else findNavController().navigate(R.id.loginDialog)
            }

            inTbLayout.toolbarIvProfile.setOnClickListener {
                if (UserUtil.isUserLogin())
                    findNavController().navigate(
                        R.id.myAccountFragment,
                        null,
                        navOptionsAnimation()
                    )
                else findNavController().navigate(R.id.loginDialog)
            }

            if (UserUtil.isUserLogin()) {
                Glide.with(requireContext())
                    .load(UserUtil.getUserProfileImageUrl())
                    .into(inTbLayout.toolbarIvProfile)
            }
        }
    }

}