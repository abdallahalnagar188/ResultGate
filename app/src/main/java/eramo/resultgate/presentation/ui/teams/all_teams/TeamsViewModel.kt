package eramo.resultgate.presentation.ui.teams.all_teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.teams.AllTeamsResponse
import eramo.resultgate.data.remote.dto.teams.myteam.MyTeamResponse
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.domain.repository.TeamsRepository
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
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

    private val _getMyTeamsState = MutableStateFlow<UiState<MyTeamResponse>?>(null)
    val getMyTeamsState: MutableStateFlow<UiState<MyTeamResponse>?> = _getMyTeamsState

    private var getAllTeamsJob: Job? = null
    private var getMyTeamsJob: Job? = null
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
    fun getMyTeams() {
        getMyTeamsJob?.cancel()
        getMyTeamsJob = viewModelScope.launch {
            withContext(coroutineContext){
                teamsRepository.getMyTeams().collect(){
                    when(it){
                        is Resource.Success->{
                            _getMyTeamsState.value = UiState.Success(it.data)
                        }
                        is Resource.Error->{
                            _getMyTeamsState.value = UiState.Error(it.message!!)
                        }
                        is Resource.Loading-> {
                            _getMyTeamsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}