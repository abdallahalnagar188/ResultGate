package eramo.tahoon.presentation.viewmodel.drawer.myaccount.queries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.drawer.myaccount.QueryDetailsResponse
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
class QueryDetailsViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    private val _queryDetailsState = MutableSharedFlow<UiState<QueryDetailsResponse>>()
    val queryDetailsState: SharedFlow<UiState<QueryDetailsResponse>> = _queryDetailsState

    private var queryDetailsJob: Job? = null

    fun cancelRequest() = queryDetailsJob?.cancel()

    fun getQueryDetails(queryId: String) {
        queryDetailsJob?.cancel()
        queryDetailsJob = viewModelScope.launch {
            withContext(coroutineContext) {
                repository.queryDetails(queryId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _queryDetailsState.emit(UiState.Success(result.data))
                        }

                        is Resource.Error -> {
                            _queryDetailsState.emit(UiState.Error(result.message!!))
                        }

                        is Resource.Loading -> {
                            _queryDetailsState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }
}