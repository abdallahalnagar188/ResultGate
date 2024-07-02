package eramo.resultgate.data.repository

import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.dto.auth.UpdateInformationResponse
import eramo.resultgate.data.remote.dto.drawer.ContactUsResponse
import eramo.resultgate.data.remote.dto.drawer.ContactUsSendMessageResponse
import eramo.resultgate.data.remote.dto.drawer.MyAppInfoResponse
import eramo.resultgate.data.remote.dto.drawer.PolicyInfoDto
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.AddToMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.DeleteFromMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.GetMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.UpdateAddressResponse
import eramo.resultgate.data.remote.dto.general.Member
import eramo.resultgate.domain.model.ResultModel
import eramo.resultgate.domain.repository.DrawerRepository
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.state.ApiState
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DrawerRepositoryImpl(private val eramoApi: EramoApi) : DrawerRepository {

    override suspend fun updateFirebaseDeviceToken(deviceToken: String): Flow<Resource<ResultModel>> {
        return flow {
            val result = toResultFlow {
                eramoApi.updateFirebaseDeviceToken(
                    UserUtil.getUserId(),
                    deviceToken
                )
            }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        apiState.data?.let {
                            emit(Resource.Success(it.toResultModel()))
                        } ?: emit(Resource.Error(UiText.DynamicString("Empty member")))
                    }
                }
            }
        }
    }

    override suspend fun getProfile(): Flow<Resource<Member>> {
        return flow {
            val result = toResultFlow { eramoApi.getProfile("Bearer ${UserUtil.getUserToken()}") }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> emit(Resource.Success(apiState.data?.data?.totoMemberModel()))
                }
            }
        }
    }

    override suspend fun editProfile(
        firstName: RequestBody?,
        lastName: RequestBody?,
        birthDate: RequestBody?,

        image: MultipartBody.Part?
    ): Flow<Resource<UpdateInformationResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.editProfile(
                    "Bearer ${UserUtil.getUserToken()}",firstName,lastName,birthDate,image
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }

    //---------------------------- Addresses ----------------------------//
    override suspend fun getMyAddresses(
    ): Flow<Resource<GetMyAddressesResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.getMyAddresses("Bearer ${UserUtil.getUserToken()}")
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }

    override suspend fun addToMyAddresses(
        addressType: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        subRegionId:String
    ): Flow<Resource<AddToMyAddressesResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.addToMyAddresses(
                    "Bearer ${UserUtil.getUserToken()}", addressType, address, countryId, cityId, regionId,subRegionId
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }

    override suspend fun deleteFromMyAddresses(addressId: String): Flow<Resource<DeleteFromMyAddressesResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.deleteFromMyAddresses(
                    "Bearer ${UserUtil.getUserToken()}", addressId
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }

    override suspend fun updateAddress(
        addressId: String,
        addressType: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        subRegionId: String
    ): Flow<Resource<UpdateAddressResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.updateAddress(
                    "Bearer ${UserUtil.getUserToken()}", addressId, addressType, address, countryId, cityId, regionId,subRegionId
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }

    //---------------------------- Addresses ----------------------------//

    override suspend fun getAppInfo(): Flow<Resource<MyAppInfoResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.getAppInfo() }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }

    override suspend fun getContactUsAppInfo(): Flow<Resource<ContactUsResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.getContactUsAppInfo() }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }

    //    override suspend fun getAppPolicy(): Flow<Resource<List<PolicyInfoModel>>> {
    override suspend fun getAppPolicy(): Flow<Resource<List<PolicyInfoDto>>> {
        return flow {
            val result = toResultFlow { eramoApi.getAppPolicy() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
//                        val list = apiState.data?.PolicyInfoDto?.map { it.toPolicyInfoModel() }
                        val list = apiState.data
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun contactMsg(
        name: String,
        email: String,
        phone: String,
        subject: String,
        message: String,
        iam_not_robot: String
    ): Flow<Resource<ContactUsSendMessageResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.contactMsg(
                    name,
                    email,
                    phone,
                    subject,
                    message,
                    iam_not_robot
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data!!))
                }
            }
        }
    }
}