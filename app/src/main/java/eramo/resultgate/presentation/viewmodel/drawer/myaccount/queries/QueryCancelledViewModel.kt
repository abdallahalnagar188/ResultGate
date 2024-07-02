package eramo.resultgate.presentation.viewmodel.drawer.myaccount.queries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.drawer.myaccount.MyQueriesResponse
import eramo.resultgate.domain.repository.RequestRepository
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QueryCancelledViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    private val _cancelledRequestsState = MutableSharedFlow<UiState<List<MyQueriesResponse.Data.MyQueryModel?>>>()
    val cancelledRequestsDtoState: SharedFlow<UiState<List<MyQueriesResponse.Data.MyQueryModel?>>> = _cancelledRequestsState

    private var cancelledRequestsJob: Job? = null

    fun cancelRequest() = cancelledRequestsJob?.cancel()

    fun getCancelledRequests() {
        cancelledRequestsJob?.cancel()
        cancelledRequestsJob = viewModelScope.launch {
            withContext(coroutineContext) {
                repository.cancelledRequests().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _cancelledRequestsState.emit(UiState.Success(result.data))
                        }
                        is Resource.Error -> {
                            _cancelledRequestsState.emit(UiState.Error(result.message!!))
                        }
                        is Resource.Loading -> {
                            _cancelledRequestsState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }
}