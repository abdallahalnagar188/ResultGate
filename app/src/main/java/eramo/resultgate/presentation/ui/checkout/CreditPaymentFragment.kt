package eramo.resultgate.presentation.ui.checkout


import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.databinding.FragmentCreditPaymentBinding
import eramo.resultgate.domain.model.AuthApiBodySend
import eramo.resultgate.domain.model.BillingData
import eramo.resultgate.domain.model.checkout.CardPaymentKeyBodySendModel
import eramo.resultgate.domain.model.checkout.OrderRegisterBodySend
import eramo.resultgate.domain.model.request.OrderRequest
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.checkout.CheckoutStepThreeViewModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.ContentScrapper
import eramo.resultgate.util.UserUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

object MyVariables {
    var orderRequest = OrderRequest()
}

@AndroidEntryPoint
class CreditPaymentFragment : Fragment(R.layout.fragment_credit_payment) {

    @Inject
    lateinit var belloutApi: EramoApi

    private val args by navArgs<CreditPaymentFragmentArgs>()
    private val totalToSend get() = args.totalToSend * 100f

    private val _callEvent = Channel<Boolean>()
    private val callEvent = _callEvent.receiveAsFlow()

    private lateinit var binding: FragmentCreditPaymentBinding
    private val viewModel by viewModels<CheckoutStepThreeViewModel>()
    val sharedViewModel by activityViewModels<SharedViewModel>()
    private var authToken = ""
    private var orderId = -1
    private var paymentToken = ""
    private var paymobUrl = ""
    var paymentId = ""
    var orderRequest = OrderRequest()
    private var integrationId = -1


    ///////////////////////////////////
    var userId = ""
    var transaction_id = ""
    var userName = ""
    var userPhone = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreditPaymentBinding.bind(view)

        // Listen on order registering on Paymob
        lifecycleScope.launchWhenStarted {
            callEvent.collect {
                if (it) {
                    registerThePaymentToPaymob()
                } else {
                }
            }
        }

        orderRequest = sharedViewModel.getOrderRequestInstance()
        userId = orderRequest.userId!!
        userName = orderRequest.userName!!
        userPhone = orderRequest.userPhone!!

        MyVariables.orderRequest = sharedViewModel.getOrderRequestInstance()
        MyVariables.orderRequest.payType = "online"
        requestAuthToken()
    }

    private fun requestAuthToken() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = belloutApi.getAuthToken3(Constants.Auth_API_URL, AuthApiBodySend(Constants.API_KEY))
            if (request.isSuccessful) {
                val token = request.body()?.token
                authToken = token!!
                orderRegister()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Request error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun orderRegister() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = belloutApi.orderRegister(
                Constants.ORDER_REGISTER_URL, OrderRegisterBodySend(
                    auth_token = authToken, delivery_needed = "false", amount_cents = totalToSend.toInt().toString(), currency = "EGP"
                )
            )
            if (request.isSuccessful) {
                orderId = request.body()?.id!!
                paymentId = request.body()?.id.toString()!!
                getCardPaymentKey()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Order registration error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCardPaymentKey() {
        val billingDate = BillingData(
            "NA",
            "NA",
            "Cairo",
            "EG",
            UserUtil.getUserEmail(),
            UserUtil.getUserFirstName(),
            "NA",
            UserUtil.getUserLastName(),
            UserUtil.getUserPhone(),
            "NA",
            "NA",
            "NA",
            "NA"
        )
//        Log.e("Mego",UserUtil.getUserPhone() )
        val integrationId = if (args.payType == "Online Payment") Constants.Integration_ID_O else (Constants.Integration_ID_S)

        val requestBody = CardPaymentKeyBodySendModel(
            totalToSend.toInt().toString(), authToken, billingDate, "EGP", 3600, integrationId, "false", orderId.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val request = belloutApi.cardPaymentKey(
                Constants.CARD_PAYMENT_KEY_URL, requestBody
            )

            if (request.isSuccessful) {
                paymentToken = request.body()?.token!!

                if (args.payType == "Online Payment") {

                    paymobUrl = "${Constants.PAYMOB_URL_O}$paymentToken"
                    MyVariables.orderRequest.payType = "Online Payment"
                } else {
                    paymobUrl = "${Constants.PAYMOB_URL_S}$paymentToken"

                }

                withContext(Dispatchers.Main) {



                    setupWebView(paymobUrl)
                }

            } else {
                withContext(Dispatchers.Main) {


                    Toast.makeText(context, "Card payment key error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupWebView(url: String) {
        //Log.e("Mego", url )
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = MyWebViewClient(belloutApi)
        binding.webView.settings.domStorageEnabled = true
        binding.webView.loadUrl(url)
    }

    inner class MyWebViewClient(api: EramoApi) : android.webkit.WebViewClient() {
        var classA = CreditPaymentFragment()
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            val checkBaseUrl: Boolean = "https://profile.bellout.shop/success" in url!!
            if (checkBaseUrl) {
                ContentScrapper.getHTMLData(activity as AppCompatActivity, url, object : ContentScrapper.ScrapListener {
                    override fun onResponse(html: String?) {
                        if (html != null) {
                            val orderDone: Boolean = "We received your purchase request we'll be in touch shortly!" in html

                            if (orderDone) {
//                                val subString = url.substringAfterLast("?id=")
//                                val orderID = subString.substringBefore("&pending=").trim()
//                                CreditPaymentFragment().paymentId = orderID
//
//                                MyVariables.orderRequest.transaction_id = orderId.toString()

                                lifecycleScope.launch {
                                    _callEvent.send(true)
                                }
                            } else {
                                Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                            }

                        } else {
                        }
                    }
                })
            }
        }
    }


    fun registerThePaymentToPaymob() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.saveOrderRequest(userAddress = sharedViewModel.checkoutModel?.userAddress!!,coupon =sharedViewModel.checkoutModel?.coupon , payment_id = paymentId , payment_type = sharedViewModel.paymentType  )

            lifecycleScope.launch {
                delay(5000)
                findNavController().navigate(R.id.checkoutStepThreeFragment)

            }
//            val request = belloutApi.saveOrderRequest(token =  )
//            if (request.isSuccessful) {
//                removeAllCartItems()
//
//            } else {
//                Toast.makeText(context, "saveProductOrders error", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    fun removeAllCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = belloutApi.removeAllCart(UserUtil.getUserId())
            if (request.isSuccessful) {
                withContext(Dispatchers.Main) {

                    // Reset cart count
                    val mainBn = activity?.findViewById<BottomNavigationView>(R.id.main_bn)
                    mainBn?.removeBadge(R.id.cartFragment)

                    findNavController().navigate(R.id.checkoutStepThreeFragment)
                }
            } else {
                Toast.makeText(context, "Clear cart error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



