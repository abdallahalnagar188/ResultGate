package eramo.tahoon.presentation.viewmodel.drawer.myaccount.queries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.drawer.myaccount.MyQueriesResponse
import eramo.tahoon.domain.repository.RequestRepository
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QueryNewViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    private val _newRequestsState = MutableSharedFlow<UiState<List<MyQueriesResponse.Data.MyQueryModel?>>>()
    val newRequestsDtoState: SharedFlow<UiState<List<MyQueriesResponse.Data.MyQueryModel?>>> = _newRequestsState

    private var newRequestsJob: Job? = null

    fun cancelRequest() = newRequestsJob?.cancel()

    fun getNewRequests() {
        newRequestsJob?.cancel()
        newRequestsJob = viewModelScope.launch {
            withContext(coroutineContext) {
                repository.newRequests().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _newRequestsState.emit(UiState.Success(result.data))
                        }
                        is Resource.Error -> {
                            _newRequestsState.emit(UiState.Error(result.message!!))
                        }
                        is Resource.Loading -> {
                            _newRequestsState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }
}