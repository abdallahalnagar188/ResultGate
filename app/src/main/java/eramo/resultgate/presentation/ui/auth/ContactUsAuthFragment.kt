package eramo.resultgate.presentation.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.drawer.AppInfo
import eramo.resultgate.data.remote.dto.drawer.ContactUsResponse
import eramo.resultgate.databinding.FragmentContactUsAuthBinding
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.drawer.ContactUsViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import org.json.JSONObject


@AndroidEntryPoint
class ContactUsAuthFragment : Fragment(R.layout.fragment_contact_us_auth), View.OnClickListener {

    private val viewModel by viewModels<ContactUsViewModel>()
    private lateinit var binding: FragmentContactUsAuthBinding
    private var appInfo: AppInfo? = null
    private var contactUsInfo: ContactUsResponse.ContactUsResponseItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContactUsAuthBinding.bind(view)
        setupToolbar()

        binding.apply {
            FContactUsBtnSend.setOnClickListener(this@ContactUsAuthFragment)
            FContactUsIvTwitter.setOnClickListener(this@ContactUsAuthFragment)
            FContactUsIvWebsite.setOnClickListener(this@ContactUsAuthFragment)
            FContactUsIvInsta.setOnClickListener(this@ContactUsAuthFragment)
            FContactUsIvFacebook.setOnClickListener(this@ContactUsAuthFragment)
        }
        fetchContactUsAppInfoState()
        fetchContactMsgState()
        handleLoadingCancellation()

        this@ContactUsAuthFragment.onBackPressed {
//            findNavController().popBackStack()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun setupToolbar() {
        binding.apply {
            FContactUsToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FContactUsToolbar.setNavigationOnClickListener {
//                findNavController().popBackStack()
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun setupSend() {
        binding.apply {
            val fullName = FContactUsEtFullName.text.toString().trim()
            val email = FContactUsEtEmail.text.toString().trim()
            val phone = FContactUsEtPhone.text.toString().trim()
            val subject = FContactUsEtSubject.text.toString().trim()
            val message = FContactUsEtMessage.text.toString().trim()
            val iamNotRobot = if (binding.checkBox.isChecked) "true" else "false"

            if (TextUtils.isEmpty(fullName)) {
                itlFullName.error = getString(R.string.txt_full_name_is_required)
                itlFullName.requestFocus()
                return
            } else itlFullName.error = null

            if (TextUtils.isEmpty(email)) {
                itlEmail.error = getString(R.string.txt_email_is_required)
                return
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                itlEmail.error = getString(R.string.txt_please_enter_a_valid_email_address)
                return
            } else itlEmail.error = null

            if (TextUtils.isEmpty(phone)) {
                itlPhone.error = getString(R.string.txt_phone_is_required)
                return
            } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
                itlPhone.error = getString(R.string.txt_please_enter_a_valid_phone_number)
                return
            } else itlPhone.error = null

            if (TextUtils.isEmpty(subject)) {
                itlSubject.error = getString(R.string.txt_subject_is_required)
                return
            } else itlSubject.error = null

            if (TextUtils.isEmpty(message)) {
                itlMessage.error = getString(R.string.txt_message_is_required)
                return
            } else itlMessage.error = null

            if (!binding.checkBox.isChecked) {
                binding.checkBox.error = getString(R.string.are_you_robot)
                return
            } else {
                binding.checkBox.error = null
            }

            viewModel.contactMsg(fullName, email, phone, subject, message, iamNotRobot)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.FContactUs_btn_send -> setupSend()

            R.id.FContactUs_iv_twitter -> {} //openUrl(contactUsInfo?.twitter)

            R.id.FContactUs_iv_website -> openUrl(contactUsInfo?.linkedIn)

            R.id.FContactUs_iv__insta -> openUrl(contactUsInfo?.instegram)

            R.id.FContactUs_iv_facebook -> openUrl(contactUsInfo?.facebook)
        }
    }

    private fun fetchContactUsAppInfoState() {
        lifecycleScope.launchWhenCreated {
            viewModel.getAppInfoState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        try {
                            contactUsInfo = state.data?.get(0)
                            binding.apply {
                                FContactUsTvEmail.text = contactUsInfo?.email
                                FContactUsTvPhone.text = contactUsInfo?.phone1
                                FContactUsTvLocation.text = contactUsInfo?.address

                                if (UserUtil.isRememberUser()) {
                                    FContactUsEtFullName.setText(UserUtil.getUserName())
                                    FContactUsEtEmail.setText(UserUtil.getUserEmail())
                                    FContactUsEtPhone.setText(UserUtil.getUserPhone())
                                }
                            }
                        } catch (e: Exception) {
                            showToast(getString(R.string.invalid_data))
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

    private fun fetchContactMsgState() {
        lifecycleScope.launchWhenCreated {
            viewModel.contactMsgState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
//                        findNavController().popBackStack()
                        findNavController().navigate(R.id.loginFragment)

                        showToast(state.data?.msg!!)
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
                            val jsonErrorBody = JSONObject(string)
                            val errors = jsonErrorBody.getString("errors")

                            val errorsObj = JSONObject(errors)
                            val keys = errorsObj.keys()

                            val errorMessage = errorsObj.getString(keys.next())
                            val errorMessageSub = errorMessage.substring(2, (errorMessage.length - 2))

                            showToast(errorMessageSub)
                        } catch (e: Exception) {
                            showToast(string)
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun handleLoadingCancellation() {
        LoadingDialog.cancelCurrentRequest.observe(viewLifecycleOwner) { isCancel ->
            if (isCancel) {
                viewModel.cancelRequest()
                LoadingDialog.dismissDialog()
                LoadingDialog.cancelCurrentRequest.value = false
            }
        }
    }

    private fun openUrl(url: String?) {
        if (contactUsInfo != null) {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            requireActivity().startActivity(i)
        } else showToast(getString(R.string.txt_no_internet))
    }
}