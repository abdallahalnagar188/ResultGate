package eramo.tahoon.presentation.ui.drawer.myaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses.GetMyAddressesResponse
import eramo.tahoon.databinding.FragmentMyAddressesBinding
import eramo.tahoon.presentation.adapters.recycleview.vertical.MyAddressesAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.ui.dialog.MyAddressesDialogArgs
import eramo.tahoon.presentation.viewmodel.drawer.myaccount.MyAddressesViewModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class MyAddressesFragment : Fragment(R.layout.fragment_my_addresses), MyAddressesAdapter.OnItemClickListener {

    private lateinit var binding: FragmentMyAddressesBinding
    private val viewModel by viewModels<MyAddressesViewModel>()

    @Inject
    lateinit var myAddressesAdapter: MyAddressesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyAddressesBinding.bind(view)

        setupToolbar()

        myAddressesAdapter.setListener(this)
        binding.rvAddresses.adapter = myAddressesAdapter

        binding.apply {
            ivAdd.setOnClickListener {
                findNavController().navigate(
                    R.id.myAddressesDialog,
                    MyAddressesDialogArgs(null, null, null, null, null, null, null, Constants.MY_ADDRESSES_FRAGMENT).toBundle(),
                    navOptionsAnimation()
                )
            }
        }

        // Request
        viewModel.getMyAddresses()

        // Fetch
        fetchGetMyAddressesState()
        fetchDeleteFromMyAddressesState()

        handelOnBackPressed()

    }


    private fun fetchGetMyAddressesState() {
        lifecycleScope.launchWhenStarted {
            viewModel.getMyAddressesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        myAddressesAdapter.submitList(state.data?.data)
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchDeleteFromMyAddressesState() {
        lifecycleScope.launchWhenStarted {
            viewModel.deleteFromMyAddressesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        if (state.data?.status == 200) {
                            viewModel.getMyAddresses()
                        } else {
                            showToast(state.data?.message ?: getString(R.string.invalid_data))
                        }

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.apply {
            FMyAddressesToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FMyAddressesToolbar.setNavigationOnClickListener { findNavController().navigate(R.id.myAccountFragment) }
        }
    }

    private fun handelOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(
                    R.id.myAccountFragment
                )
            }
        })
    }

    override fun onAddressDeleteClick(model: GetMyAddressesResponse.Data) {
        viewModel.deleteFromMyAddresses(model.id.toString())
    }

    override fun onAddressEditClick(model: GetMyAddressesResponse.Data) {
        findNavController().navigate(
            R.id.myAddressesDialog, MyAddressesDialogArgs(
                model.countryId.toString(),
                model.cityId.toString(),
                model.regionId.toString(),
                model.subRegionId.toString(),
                model.addressType,
                model.address,
                model.id.toString(),
                Constants.MY_ADDRESSES_FRAGMENT
            ).toBundle(), navOptionsAnimation()
        )
    }
}