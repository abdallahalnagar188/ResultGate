package eramo.resultgate.presentation.ui.drawer.becomeavendor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.becomeavendor.BecomeAVendorResponse
import eramo.resultgate.domain.repository.BecomeAVednorRepository
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BecomeAVendorViewModel @Inject constructor(
    val repository: BecomeAVednorRepository
):ViewModel(){
    var _becomeAVendorState = MutableStateFlow<UiState<BecomeAVendorResponse>>(UiState.Empty())
    val becomeAVendorState : StateFlow<UiState<BecomeAVendorResponse>>  = _becomeAVendorState





    var becomeAVendorJob : Job? = null


    fun becomeAVendor(
        name:String,
        phone:String,
        email:String,
        message:String,
        type:String
    ){
        becomeAVendorJob?.cancel()
        becomeAVendorJob = viewModelScope.launch(Dispatchers.IO) {
            repository.becomeAVednor(
                name,
                phone,
                email,
                message,
                type
            ).collect(){
                when(it){
                    is Resource.Success->{
                        _becomeAVendorState.value = UiState.Success(it.data)
                    }
                    is Resource.Error->{
                        _becomeAVendorState.value = UiState.Error(it.message!!)
                    }
                    is Resource.Loading->{
                        _becomeAVendorState.value = UiState.Loading()
                    }
                }
            }

        }
    }
}