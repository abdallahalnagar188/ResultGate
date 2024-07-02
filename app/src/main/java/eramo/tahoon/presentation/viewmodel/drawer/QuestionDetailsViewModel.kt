package eramo.tahoon.presentation.viewmodel.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.SendQueryResponse
import eramo.tahoon.domain.repository.RequestRepository
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class QuestionDetailsViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {

    private val _requestState = MutableStateFlow<UiState<SendQueryResponse>>(UiState.Empty())
    val requestState: StateFlow<UiState<SendQueryResponse>> = _requestState

    val selectedServiceKey = MutableStateFlow<String>("0")

    private var requestJob: Job? = null

    fun cancelRequest() {
        requestJob?.cancel()
    }

    fun postRequest(
        name: String,
        phone: String,
        email: String,
        message: String,
    ) {
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            withContext(coroutineContext) {
                requestRepository.questionsRequest(
                    name, phone, email, message
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _requestState.value = UiState.Success(it)
                            } ?: run { _requestState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _requestState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _requestState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    private fun convertToRequestBody(part: String): RequestBody? {
        return try {
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), part)
        } catch (e: Exception) {
            null
        }
    }
}