package eramo.resultgate.presentation.ui.auth

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.local.entity.MyCartDataEntity
import eramo.resultgate.data.remote.dto.products.ProductDetailsResponse
import eramo.resultgate.databinding.FragmentLoginBinding
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.auth.LoginViewModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import org.json.JSONObject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private val args by navArgs<LoginFragmentArgs>()
    private val proceedRequire get() = args.proceedRequire
    private lateinit var productModel: ProductDetailsResponse
    private var qty = 1
    lateinit var cartList:List<MyCartDataEntity>

    var cart:String?=""

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
        binding = FragmentLoginBinding.bind(view)

        lifecycleScope.launchWhenCreated {
            cartList = fetchCartList()
            cart = prepareCart()
        }

        binding.apply {
            FLoginTvSignUp.setOnClickListener(this@LoginFragment)
            loginBtnLogin.setOnClickListener(this@LoginFragment)
            loginTvForgot.setOnClickListener(this@LoginFragment)
            tvHome.setOnClickListener(this@LoginFragment)
            LoginIvBack.setOnClickListener(this@LoginFragment)
        }

        // Fetch
        fetchLoginState()

        handleLoadingCancellation()

        this@LoginFragment.onBackPressed { requireActivity().finish() }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.FLogin_tv_signUp -> findNavController().navigate(R.id.signUpFragment)

            R.id.login_tv_forgot -> findNavController().navigate(R.id.forgetPasswordFragment)

            R.id.login_btn_login -> setupLogin()


            R.id.tv_home -> {
                findNavController().navigate(
                    R.id.mainFragment, null,
                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                )
            }

            R.id.Login_iv_back -> {
                findNavController().navigate(
                    R.id.mainFragment, null,
                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                )
            }
        }
    }

    private fun setupLogin() {
        binding.apply {
            val phone = loginEtPhone.text.toString().trim()
            val password = loginEtPassword.text.toString().trim()

            if (TextUtils.isEmpty(phone)) {
                itlPhone.error = getString(R.string.txt_phone_is_required)
                return
            } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
                itlPhone.error = getString(R.string.txt_please_enter_a_valid_phone_number)
                return
            } else itlPhone.error = null

            if (TextUtils.isEmpty(password)) {
                itlPassword.error = getString(R.string.txt_password_is_required)
                return
            } else itlPassword.error = null

            viewModel.loginApp(phone, password, loginCbRemember.isChecked)
        }
    }





    private fun fetchLoginState() {
        lifecycleScope.launchWhenCreated {
            viewModel.loginState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        if (state.data?.member?.status == "1" && state.data.member?.verifiedStatus == 1) {
                            switchLocalCartToRemote(cart!!)
                        } else if (state.data?.member?.verifiedStatus == 0) {
                            val email = state.data.member?.email.toString()
                            val phone = binding.loginEtPhone.text.toString().trim()
                            val password = binding.loginEtPassword.text.toString().trim()

                            findNavController().navigate(
                                R.id.signUpVerificationFragment, SignUpVerificationFragmentArgs(
                                    email,phone, password,Constants.DESTINATION_FROM_LOGIN
                                ).toBundle(),
                                navOptionsAnimation()
                            )
                        } else
                            if (state.data?.member?.status == "") {
//                                showToast(getString(R.string.this_account_is_suspend))
                                findNavController().navigate(R.id.deletedAccountDialogFragment)
                            } else if (state.data?.member?.status == "0"){
//                                showToast(getString(R.string.this_account_is_suspend))
                                findNavController().navigate(R.id.deletedAccountDialogFragment)
                            }

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
                            parseErrorResponse(string)
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
    private fun createAddToCartJsonObject(productId: Int, quantity: Int, detailsId: Int, vendorId: Int): String {

//        val jsonObject = JSONObject()
//        jsonObject.put("product_id", productId)
//        jsonObject.put("quantity", quantity)
//        jsonObject.put("details_id", detailsId)
//        jsonObject.put("vendor_id", vendorId)
        val product = "{\"product_id\": ${productId},\"quantity\": ${quantity},\"details_id\":${detailsId} ,\"vendor_id\":${vendorId}}"
//        jsonArray.add(jsonObject)

        return product
    }
    fun getDetailsId(colorId: Int, sizeId: Int): Int {
        var detailsId = 0
        //cartList[0].stocks!!
        //productModel.data?.get(0)?.stocks!!
        for (i in cartList) {
            if (i.colorId == colorId && i.sizeId == sizeId) {
                detailsId = i.productId!!
            }
        }
        return detailsId
    }
    var size = -1
    var color = -1

    fun getVendorId(id: String?): Int {
        return if (id == "") {
            0
        } else id?.toInt() ?: 0
    }
    suspend fun fetchCartList():List<MyCartDataEntity>{
        cartList = viewModel.getCartList()
        return cartList
    }
   fun prepareCart():String{
       val cart = arrayListOf<String>()
       for(item in cartList ){
           cart.add(createAddToCartJsonObject(
               productId =  item.productId!! ,
               quantity =  item.productQty!!,
               detailsId = getDetailsId(item.colorId!!, item.sizeId!!),
               vendorId = item.vendorId!!.toInt()
           ))

       }

       return cart.toString()
   }

    private fun switchLocalCartToRemote(cart:String) {
        lifecycleScope.launchWhenStarted {
            viewModel.switchLocalCartToRemote(cart).collect {
                when (it) {
                    is Resource.Success -> {
                        LoadingDialog.dismissDialog()
                        findNavController().navigate(
                                R.id.mainFragment, null,
                                NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                            )
//                        findNavController().popBackStack()
                    }

                    is Resource.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(it.message!!.asString(requireContext()))
                    }

                    is Resource.Loading -> {
                        LoadingDialog.showDialog()
                    }
                }
            }
        }
    }

    private fun parseErrorResponse(string: String) {
        val jsonErrorBody = JSONObject(string)
        val errorMessage = jsonErrorBody.getString("errors")
        showToast(errorMessage)
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