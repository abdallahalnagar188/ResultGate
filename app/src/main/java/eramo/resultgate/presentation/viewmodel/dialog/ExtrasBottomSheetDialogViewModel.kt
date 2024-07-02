package eramo.resultgate.presentation.viewmodel.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.domain.model.products.orders.ProductExtrasModel
import eramo.resultgate.domain.usecase.product.GetProductByIdUseCase
import eramo.resultgate.util.Constants
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ExtrasBottomSheetDialogViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val _getProductExtrasState =
        MutableStateFlow<UiState<List<ProductExtrasModel>>>(UiState.Empty())
    val getProductExtrasState: StateFlow<UiState<List<ProductExtrasModel>>> =
        _getProductExtrasState

    private var getProductExtrasJob: Job? = null

    fun cancelRequest() = getProductExtrasJob?.cancel()

    fun getProductExtras(productId: String) {
        getProductExtrasJob?.cancel()
        getProductExtrasJob = viewModelScope.launch {
            withContext(coroutineContext) {
                delay(Constants.ANIMATION_DELAY)
                getProductByIdUseCase(productId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val extrasList = result.data?.data?.get(0)?.extrasItems!!.map { it!!.toProductExtrasModel() }
                            _getProductExtrasState.emit(UiState.Success(extrasList))
                        }

                        is Resource.Error -> {
                            _getProductExtrasState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _getProductExtrasState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }
}