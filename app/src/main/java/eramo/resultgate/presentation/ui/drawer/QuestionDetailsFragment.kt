package eramo.resultgate.presentation.ui.drawer

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentQuestionDetailsBinding
import eramo.resultgate.domain.enums.ServicesType
import eramo.resultgate.domain.model.drawer.questions.ServicesTypeModel
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.drawer.QuestionDetailsViewModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.StringWithKey
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import org.json.JSONObject

@AndroidEntryPoint
class QuestionDetailsFragment : Fragment(R.layout.fragment_question_details) {

    private val viewModel by viewModels<QuestionDetailsViewModel>()
    private lateinit var binding: FragmentQuestionDetailsBinding

    private val servicesList = ArrayList<StringWithKey>()

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
        binding = FragmentQuestionDetailsBinding.bind(view)
        setupToolbar()

        binding.apply {
            FQuestionDetailsTitle.text = getString(R.string.quires_request)

            FQuestionDetailsBtnSend.setOnClickListener { setupSend() }
        }

        setupServiceTypeSpinner(servicesTypeList())

        fetchRequestState()
        fetchSelectedServiceKeyState()
        handleLoadingCancellation()
        initParams()
        this@QuestionDetailsFragment.onBackPressed { findNavController().popBackStack() }
    }

    private fun initParams() {
        binding.apply {
            FQuestionDetailsEtFullName.setText(UserUtil.getUserFirstName()+" "+UserUtil.getUserLastName())
            etPhone.setText(UserUtil.getUserPhone())
            etEmail.setText(UserUtil.getUserEmail())
        }
    }

    private fun setupToolbar() {
        binding.apply {
            FQuestionDetailsTbTitle.text = getString(R.string.quires_request)
            FQuestionDetailsToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FQuestionDetailsToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupSend() {
        binding.apply {
            val fullName = FQuestionDetailsEtFullName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val message = FQuestionDetailsEtMessage.text.toString().trim()
            val airConditioningCount = etAirConditionCount.text.toString().trim()

            if (TextUtils.isEmpty(fullName)) {
                itlFullName.error = getString(R.string.txt_full_name_is_required)
                itlFullName.requestFocus()
                return
            } else itlFullName.error = null

            if (TextUtils.isEmpty(phone)) {
                itlPhone.error = getString(R.string.txt_phone_is_required)
                return
            } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
                itlPhone.error = getString(R.string.txt_please_enter_a_valid_phone_number)
                return
            } else itlPhone.error = null

            if (email.isBlank()) {
                itlEmail.error = getString(R.string.txt_email_is_required)
                return
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                itlEmail.error = getString(R.string.txt_please_enter_a_valid_email_address)
                return
            } else {
                itlEmail.error = null
            }

//            if (viewModel.selectedServiceKey.value == "0") {
//                showToast(getString(R.string.please_select_sevice))
//                return
//            }

//            if (viewModel.selectedServiceKey.value != "0" && viewModel.selectedServiceKey.value != ServicesType.QUESTION.key) {
//                if (TextUtils.isEmpty(airConditioningCount)) {
//                    itlAirConditionCount.error = getString(R.string.please_select_air_conditioning_count)
//                    return
//                } else {
//                    itlAirConditionCount.error = null
//                }
//            }

            if (TextUtils.isEmpty(message)) {
                itlMessage.error = getString(R.string.txt_message_is_required)
                return
            } else itlMessage.error = null

            viewModel.postRequest(
                fullName, phone, email,
                message
            )
        }
    }

    private fun fetchRequestState() {
        lifecycleScope.launchWhenCreated {
            viewModel.requestState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
//                        findNavController().popBackStack()
                        findNavController().navigate(R.id.myQueriesFragment)
                        showToast(state.data?.message.toString())
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
//                        showToast(state.message!!.asString(requireContext()))
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

    private fun fetchSelectedServiceKeyState() {
        lifecycleScope.launchWhenCreated {
            viewModel.selectedServiceKey.collect { key ->
                if (key == "0" || key == ServicesType.QUESTION.key) {
                    binding.itlAirConditionCount.visibility = View.GONE
                    binding.etAirConditionCount.setText("0")
                } else {
                    binding.itlAirConditionCount.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupServiceTypeSpinner(list: List<ServicesTypeModel>) {
        binding.apply {

            // Initial item
            servicesList.clear()
            servicesList.add(StringWithKey(getString(R.string.service_type), "0"))

            // Assign the countries
            for (model in list)
                servicesList.add(StringWithKey(model.title, model.key))

            // Assign the spinner adapter
            spinnerServiceType.spinner.adapter = Constants.createServicesSpinnerAdapter(requireContext(), servicesList)

            // Listener
            spinnerServiceType.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            viewModel.selectedServiceKey.value = "0"
                        } else {
                            viewModel.selectedServiceKey.value =
                                (parentView?.getItemAtPosition(position) as StringWithKey).key
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // your code here
                    }
                }
        }
    }

    private fun servicesTypeList(): List<ServicesTypeModel> {
        val list = mutableListOf<ServicesTypeModel>()

        list.add(
            ServicesTypeModel(
                requireContext().getString(R.string.services_type_maintenance), ServicesType.MAINTENANCE.key
            )
        )

        list.add(
            ServicesTypeModel(
                requireContext().getString(R.string.services_type_installations), ServicesType.INSTALLATION.key
            )
        )

        list.add(
            ServicesTypeModel(
                requireContext().getString(R.string.services_type_installing_pipes), ServicesType.INSTALLING_PIPES.key
            )
        )

        list.add(
            ServicesTypeModel(
                requireContext().getString(R.string.services_type_request_site_visit),
                ServicesType.REQUEST_SITE_VISIT.key
            )
        )

        list.add(
            ServicesTypeModel(
                requireContext().getString(R.string.services_type_question), ServicesType.QUESTION.key
            )
        )

        return list
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
}