package eramo.tahoon.presentation.ui.navbottom.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.NotificationDetailsResponse
import eramo.tahoon.databinding.FragmentNotificationInfoBinding
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.navbottom.extension.NotificationViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.onBackPressed
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState

@AndroidEntryPoint
class NotificationInfoFragment : Fragment(R.layout.fragment_notification_info) {

    private lateinit var binding: FragmentNotificationInfoBinding
    private val viewModel by viewModels<NotificationViewModel>()
    private val args by navArgs<NotificationInfoFragmentArgs>()

    //    private val notificationDto get() = args.notificationDto
    private val notificationId get() = args.notificationId

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
        binding = FragmentNotificationInfoBinding.bind(view)
        setupToolbar()

        viewModel.getNotificationDetails(notificationId)

        fetchNotificationDetailsState()

        this@NotificationInfoFragment.onBackPressed {
            findNavController().popBackStack()
////            findNavController().navigate(R.id.notificationFragment)
//            if (fromFcmBackground){
////                findNavController().navigate(R.id.homeFragment)
//                findNavController().navigate(
//                    R.id.mainFragment, null,
//                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
//                )
//            }else{
//            findNavController().popBackStack(R.id.notificationFragment,false)
//            }
        }
    }

    private fun fetchNotificationDetailsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.notificationDetailsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        setupInfo(state.data)
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

    private fun setupInfo(data: NotificationDetailsResponse?) {
        binding.apply {
            FNotificationInfoTitle.text = data?.data?.title
            FNotificationInfoContent.text = data?.data?.body

            if (data?.data?.image.isNullOrEmpty())
                FNotificationInfoImage.visibility = View.GONE
            else Glide.with(requireContext())
                .load(data?.data?.image)
                .into(this.FNotificationInfoImage)

//            if(notificationDto.link.isNullOrEmpty())
//                FNotificationInfoLink.visibility = View.GONE
//            else FNotificationInfoLink.text = notificationDto.link
//
//            FNotificationInfoLink.setOnClickListener {
//                val i = Intent(Intent.ACTION_VIEW, Uri.parse(notificationDto.link))
//                startActivity(i)
//            }
        }
    }

    private fun setupToolbar() {
        binding.apply {
            FNotificationInfoToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FNotificationInfoToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
//                findNavController().navigate(R.id.notificationFragment)
//                if (fromFcmBackground){
//                    findNavController().navigate(R.id.homeFragment)
//                }else{
//                    findNavController().popBackStack(R.id.notificationFragment,false)
//                }
            }
        }
    }

}