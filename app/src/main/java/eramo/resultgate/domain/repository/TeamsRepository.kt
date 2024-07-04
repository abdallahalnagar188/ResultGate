package eramo.resultgate.domain.repository

import androidx.paging.PagingData
import eramo.resultgate.data.remote.dto.teams.deleteteam.ExitTeamResponse
import eramo.resultgate.data.remote.dto.teams.jointeam.JoinTeamResponse
import eramo.resultgate.data.remote.dto.teams.myteam.MyTeamResponse
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Field

interface TeamsRepository {
    suspend fun getAllTeams(): Flow<PagingData<TeamsModel>>
    suspend fun getMyTeams():Flow<Resource<MyTeamResponse>>
    suspend fun joinTeam( teamId: Int,grams: Int):Flow<Resource<JoinTeamResponse>>
    suspend fun exitTeam( teamId: Int):Flow<Resource<ExitTeamResponse>>
}