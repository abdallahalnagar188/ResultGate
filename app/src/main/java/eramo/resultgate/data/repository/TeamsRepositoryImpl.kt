package eramo.resultgate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.dto.teams.deleteteam.ExitTeamResponse
import eramo.resultgate.data.remote.dto.teams.jointeam.JoinTeamResponse
import eramo.resultgate.data.remote.dto.teams.myteam.MyTeamResponse
import eramo.resultgate.data.remote.dto.teams.teamdetails.TeamDetailsResponse
import eramo.resultgate.data.remote.paging.PagingAllTeams
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.domain.repository.TeamsRepository
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.pagingConfig
import eramo.resultgate.util.state.ApiState
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TeamsRepositoryImpl @Inject constructor(
    val eramoApi: EramoApi
):TeamsRepository{
    override suspend fun getAllTeams(): Flow<PagingData<TeamsModel>> {
        return Pager(
            config = pagingConfig(),
            pagingSourceFactory = {
                PagingAllTeams(eramoApi)
            }
        ).flow
    }

    override suspend fun getMyTeams(): Flow<Resource<MyTeamResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.getMyTeams(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null
                )
            }
            result.collect(){apiState->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> { emit(Resource.Success(apiState.data)) }
                }

            }
        }
    }

    override suspend fun joinTeam(teamId: Int, grams: Int): Flow<Resource<JoinTeamResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.joinTeam(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                    teamId = teamId,
                    grams = grams
                )
            }
            result.collect(){apiState->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> { emit(Resource.Success(apiState.data)) }
                }

            }
        }
    }

    override suspend fun exitTeam(teamId: Int): Flow<Resource<ExitTeamResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.deleteTeam(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                    teamId = teamId,
                )
            }
            result.collect(){apiState->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> { emit(Resource.Success(apiState.data)) }
                }

            }
        }
    }

    override suspend fun teamDetails(teamId: Int): Flow<Resource<TeamDetailsResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.teamDetails(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                    teamId = teamId,
                )
            }
            result.collect(){apiState->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> { emit(Resource.Success(apiState.data)) }
                }

            }
        }
    }


}