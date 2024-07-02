package eramo.tahoon.presentation.viewmodel.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.cart.CheckoutResponse
import eramo.tahoon.domain.model.ResultModel
import eramo.tahoon.domain.model.products.PaymentTypesModel
import eramo.tahoon.domain.repository.CartRepository
import eramo.tahoon.domain.repository.OrderRepository
import eramo.tahoon.util.Constants
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CheckoutStepThreeViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _checkoutState = MutableStateFlow<UiState<CheckoutResponse>>(UiState.Empty())
    val checkoutState: StateFlow<UiState<CheckoutResponse>> = _checkoutState

    private val _paymentState = MutableStateFlow<UiState<List<PaymentTypesModel>>>(UiState.Empty())
    val paymentState: StateFlow<UiState<List<PaymentTypesModel>>> = _paymentState

    private val _orderState = MutableStateFlow<UiState<ResultModel>>(UiState.Empty())
    val orderState: StateFlow<UiState<ResultModel>> = _orderState

    private var paymentJob: Job? = null
    private var orderJob: Job? = null
    private var checkoutJob: Job? = null

    fun cancelRequest() {
        checkoutJob?.cancel()
        paymentJob?.cancel()
        orderJob?.cancel()
    }

    fun paymentTypes() {
        orderJob?.cancel()
        orderJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                orderRepository.paymentTypes().collect { result ->
                    when (result) {
                        is Resource.Success -> _paymentState.value = UiState.Success(result.data)
                        is Resource.Error -> {
                            _paymentState.value = UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _paymentState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun checkout(userAddress: String?,coupon: String?,payment_type: String?,payment_id: String?) {
        checkoutJob?.cancel()
        checkoutJob = viewModelScope.launch {
            withContext(coroutineContext) {
                orderRepository.checkout(userAddress, coupon, payment_type,payment_id).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _checkoutState.value = UiState.Success(it)
                            } ?: run { _checkoutState.value = UiState.Empty() }

                        }
                        is Resource.Error -> {
                            _checkoutState.value = UiState.Error(result.message!!)
//                            _checkoutState.value = UiState.Error(UiText.DynamicString(result.data?.message.toString()))
                        }
                        is Resource.Loading -> {
                            _checkoutState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun saveOrderRequest(userAddress: String?,coupon: String?,payment_type: String?,payment_id:String?) {
        orderJob?.cancel()
        orderJob = viewModelScope.launch {
            withContext(coroutineContext) {
                orderRepository.saveOrderRequest(userAddress = userAddress, coupon = coupon, payment_type = payment_type, payment_id = payment_id).collect { result ->
                    when (result) {
                        is Resource.Success -> removeAllCart()
                        is Resource.Error -> {
                            _orderState.value = UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _orderState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    private fun removeAllCart() {
        orderJob?.cancel()
        orderJob = viewModelScope.launch {
            withContext(coroutineContext) {
                cartRepository.removeAllCartApi().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _orderState.value = UiState.Success(it)
                            } ?: run { _orderState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _orderState.value = UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _orderState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}