package eramo.resultgate.domain.repository

import eramo.resultgate.data.remote.dto.SendQueryResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.MyQueriesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.QueryDetailsResponse
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow

interface RequestRepository {

    suspend fun questionsRequest(
        name: String,
        phone: String,
        email: String,
        message: String,
    ): Flow<Resource<SendQueryResponse>>

    suspend fun newRequests(): Flow<Resource<List<MyQueriesResponse.Data.MyQueryModel?>>>

    suspend fun repliedRequests(): Flow<Resource<List<MyQueriesResponse.Data.MyQueryModel?>>>

    suspend fun cancelledRequests(): Flow<Resource<List<MyQueriesResponse.Data.MyQueryModel?>>>

    suspend fun queryDetails(queryId: String): Flow<Resource<QueryDetailsResponse>>
}