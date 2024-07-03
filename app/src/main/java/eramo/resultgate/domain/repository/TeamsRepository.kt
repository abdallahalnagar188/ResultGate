package eramo.resultgate.domain.repository

import androidx.paging.PagingData
import eramo.resultgate.domain.model.teams.TeamsModel
import kotlinx.coroutines.flow.Flow

interface TeamsRepository {
    suspend fun getAllTeams(): Flow<PagingData<TeamsModel>>
}