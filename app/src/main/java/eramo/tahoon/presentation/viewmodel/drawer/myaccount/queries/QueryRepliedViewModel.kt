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
class QueryRepliedViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    private val _repliedRequestsState = MutableSharedFlow<UiState<List<MyQueriesResponse.Data.MyQueryModel?>>>()
    val repliedRequestsDtoState: SharedFlow<UiState<List<MyQueriesResponse.Data.MyQueryModel?>>> = _repliedRequestsState

    private var repliedRequestsJob: Job? = null

    fun cancelRequest() = repliedRequestsJob?.cancel()

    fun getRepliedRequests() {
        repliedRequestsJob?.cancel()
        repliedRequestsJob = viewModelScope.launch {
            withContext(coroutineContext) {
                repository.repliedRequests().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _repliedRequestsState.emit(UiState.Success(result.data))
                        }
                        is Resource.Error -> {
                            _repliedRequestsState.emit(
                                UiState.Error(
                                    result.message!!
                                )
                            )
                        }
                        is Resource.Loading -> {
                            _repliedRequestsState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }
}