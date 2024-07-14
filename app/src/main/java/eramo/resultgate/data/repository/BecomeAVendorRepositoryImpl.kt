package eramo.resultgate.data.repository

import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.dto.becomeavendor.BecomeAVendorResponse
import eramo.resultgate.domain.repository.BecomeAVednorRepository
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.state.ApiState
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BecomeAVendorRepositoryImpl @Inject constructor(
    val api: EramoApi
) : BecomeAVednorRepository {
    override suspend fun becomeAVednor(
        name: String,
        email: String,
        phone: String,
        comment: String?,
        vendorType: String
    ): Flow<Resource<BecomeAVendorResponse>> {
        return flow {
            val result = toResultFlow {
                api.becomeAVendor(
                    userToken = if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                    name = name, email = email, phone = phone, comment = comment, vendorType = vendorType
                )
            }
            result.collect() {
                when (it) {
                    is ApiState.Success -> {
                        emit(Resource.Success(it.data))
                    }

                    is ApiState.Error -> {
                        emit(Resource.Error(it.message!!))
                    }
                    is ApiState.Loading -> {
                        emit(Resource.Loading())
                    }
                }

            }
        }
    }
}