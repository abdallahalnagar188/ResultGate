package eramo.resultgate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.paging.PagingAllTeams
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.domain.repository.TeamsRepository
import eramo.resultgate.util.pagingConfig
import kotlinx.coroutines.flow.Flow
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

}