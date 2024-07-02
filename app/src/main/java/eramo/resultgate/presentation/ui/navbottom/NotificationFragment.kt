package eramo.resultgate.presentation.ui.navbottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.NotificationResponse
import eramo.resultgate.databinding.FragmentNotificationBinding
import eramo.resultgate.presentation.adapters.recycleview.vertical.NotificationAdapter
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.ui.drawer.myaccount.order.OrderDetailsFragmentArgs
import eramo.resultgate.presentation.ui.navbottom.extension.NotificationInfoFragmentArgs
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.navbottom.extension.NotificationViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : Fragment(R.layout.fragment_notification),
    NotificationAdapter.OnItemClickListener {

    @Inject
    lateinit var notificationAdapter: NotificationAdapter
    private lateinit var binding: FragmentNotificationBinding
    private val viewModel by viewModels<NotificationViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()

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
        binding = FragmentNotificationBinding.bind(view)
        setupToolbar()

        viewModel.getNotification()

        notificationAdapter.setListener(this)
        binding.FFavouriteRvNotification.adapter = notificationAdapter
        fetchNotificationState()

        this@NotificationFragment.onBackPressed { findNavController().popBackStack() }
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


    private fun fetchNotificationState() {
        lifecycleScope.launchWhenStarted {
            viewModel.notificationsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        val sortedList = state.data?.data?.sortedByDescending { it?.createdAt }

                        notificationAdapter.submitList(sortedList)

                        if (state.data?.data.isNullOrEmpty()) {
                            binding.lottieNoData.visibility = View.VISIBLE
                        } else {
                            binding.lottieNoData.visibility = View.GONE
                        }
                    }

                    is UiState.Error -> {
                        if (UserUtil.isUserLogin()){
                            showToast(state.message!!.asString(requireContext()))
                        }
                        LoadingDialog.dismissDialog()
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onNotificationClick(model: NotificationResponse.Data) {
//        if (model.orderId != null) {
//            findNavController().navigate(
//                R.id.orderDetailsFragment,
//                OrderDetailsFragmentArgs(null, model.orderId.toString()).toBundle(),
//                navOptionsAnimation()
//            )
//        } else {

        when (model.type) {
            "" -> {
                findNavController().navigate(
                    R.id.notificationInfoFragment,
                    NotificationInfoFragmentArgs(model.id.toString()).toBundle(),
                    navOptionsAnimation()
                )
            }

            "order" -> {
                findNavController().navigate(
                    R.id.orderDetailsFragment,
                    OrderDetailsFragmentArgs(model.orderId ?: "",model.id.toString()).toBundle(),
                    navOptionsAnimation()
                )
            }
        }

//        }
    }

}