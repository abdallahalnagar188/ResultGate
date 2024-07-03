package eramo.resultgate.domain.model.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.domain.model.products.ShopProductModel
import eramo.resultgate.domain.repository.TeamsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(
    val teamsRepository: TeamsRepository
) : ViewModel() {
    private val _getAllTeamsState = MutableStateFlow<PagingData<TeamsModel>?>(null)
    val getAllTeamsState: MutableStateFlow<PagingData<TeamsModel>?> = _getAllTeamsState

    var getAllTeamsJob: Job? = null

    fun getAllTeams() {
        getAllTeamsJob?.cancel()
        getAllTeamsJob = viewModelScope.launch {
            withContext(coroutineContext){
                teamsRepository.getAllTeams().cachedIn(viewModelScope).collect(){
                    _getAllTeamsState.value = it
                }
            }
        }
    }
}