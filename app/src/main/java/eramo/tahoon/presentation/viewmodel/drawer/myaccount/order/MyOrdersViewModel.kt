package eramo.tahoon.presentation.viewmodel.drawer.myaccount.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.domain.model.products.orders.OrderModel
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
class MyOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _myOrdersState = MutableStateFlow<UiState<List<OrderModel>>>(UiState.Empty())
    val myOrdersState: StateFlow<UiState<List<OrderModel>>> = _myOrdersState

    private var myOrdersJob: Job? = null

    fun cancelRequest() = myOrdersJob?.cancel()

    fun myOrders() {
        myOrdersJob?.cancel()
        myOrdersJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                orderRepository.allMyOrders().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _myOrdersState.value = UiState.Success(it)
                            } ?: run { _myOrdersState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _myOrdersState.value = UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _myOrdersState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}