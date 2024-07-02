package eramo.resultgate.data.repository

import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.dto.SendQueryResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.MyQueriesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.QueryDetailsResponse
import eramo.resultgate.domain.repository.RequestRepository
import eramo.resultgate.util.Constants.API_SUCCESS
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.state.ApiState
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RequestRepositoryImpl(private val eramoApi: EramoApi) : RequestRepository {

    override suspend fun questionsRequest(
        name: String,
        phone: String,
        email: String,
        message: String,
    ): Flow<Resource<SendQueryResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.questionsRequest(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                    name, phone, email, message
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        val dataModel = it.data!!
                        if (dataModel.status == API_SUCCESS) emit(Resource.Success(dataModel))
                        else emit(
                            Resource.Error(UiText.DynamicString("Error"))
                        )
                    }
                }
            }
        }
    }

    override suspend fun newRequests(): Flow<Resource<List<MyQueriesResponse.Data.MyQueryModel?>>> {
        return flow {
            val result = toResultFlow { eramoApi.myQueries("Bearer ${UserUtil.getUserToken()}") }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.pendding
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun repliedRequests(): Flow<Resource<List<MyQueriesResponse.Data.MyQueryModel?>>> {
        return flow {
            val result = toResultFlow { eramoApi.myQueries("Bearer ${UserUtil.getUserToken()}") }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
//                        val list = apiState.data?.allRequestDtos?.map { it.toRequestModel() }
                        val list = apiState.data?.data?.replyed
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun cancelledRequests(): Flow<Resource<List<MyQueriesResponse.Data.MyQueryModel?>>> {
        return flow {
            val result = toResultFlow { eramoApi.myQueries("Bearer ${UserUtil.getUserToken()}") }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
//                        val list = apiState.data?.allRequestDtos?.map { it.toRequestModel() }
                        val list = apiState.data?.data?.canceled
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun queryDetails(queryId: String): Flow<Resource<QueryDetailsResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.queryDetails("Bearer ${UserUtil.getUserToken()}", queryId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
//                        val list = apiState.data?.allRequestDtos?.map { it.toRequestModel() }
                        val list = apiState.data
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }
}