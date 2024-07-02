package eramo.resultgate.presentation.viewmodel.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.drawer.myaccount.CancelOrderResponse
import eramo.resultgate.domain.repository.OrderRepository
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CancelDialogViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderDetailsState = MutableStateFlow<UiState<CancelOrderResponse>>(UiState.Empty())
    val orderDetailsState: StateFlow<UiState<CancelOrderResponse>> = _orderDetailsState

    private var orderDetailsJob: Job? = null

    fun cancelRequest() = orderDetailsJob?.cancel()

    fun cancelOrder(orderId: String) {
        orderDetailsJob?.cancel()
        orderDetailsJob = viewModelScope.launch {
            withContext(coroutineContext) {
                orderRepository.cancelProductOrder(orderId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _orderDetailsState.value = UiState.Success(it)
                            } ?: run { _orderDetailsState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _orderDetailsState.value = UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _orderDetailsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}