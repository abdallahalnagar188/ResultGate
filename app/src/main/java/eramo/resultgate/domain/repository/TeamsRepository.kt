package eramo.resultgate.domain.repository

import androidx.paging.PagingData
import eramo.resultgate.data.remote.dto.teams.myteam.MyTeamResponse
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow

interface TeamsRepository {
    suspend fun getAllTeams(): Flow<PagingData<TeamsModel>>
    suspend fun getMyTeams():Flow<Resource<MyTeamResponse>>
}