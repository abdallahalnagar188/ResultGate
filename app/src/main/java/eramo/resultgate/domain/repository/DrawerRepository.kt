package eramo.resultgate.domain.repository

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
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface DrawerRepository {

    suspend fun updateFirebaseDeviceToken(deviceToken: String): Flow<Resource<ResultModel>>

    suspend fun getProfile(): Flow<Resource<Member>>

    suspend fun editProfile(
        firstName: RequestBody?,
        lastName: RequestBody?,
        birthDate: RequestBody?,

        image: MultipartBody.Part?
    ): Flow<Resource<UpdateInformationResponse>>

    suspend fun getMyAddresses(): Flow<Resource<GetMyAddressesResponse>>

    suspend fun addToMyAddresses(
        addressType: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        subRegionId:String
    ): Flow<Resource<AddToMyAddressesResponse>>

    suspend fun deleteFromMyAddresses(addressId: String): Flow<Resource<DeleteFromMyAddressesResponse>>

    suspend fun updateAddress(
        addressId: String,
        addressType: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        subRegionId: String
    ): Flow<Resource<UpdateAddressResponse>>


    suspend fun getAppInfo(): Flow<Resource<MyAppInfoResponse>>

    suspend fun getContactUsAppInfo(): Flow<Resource<ContactUsResponse>>

    //    suspend fun getAppPolicy(): Flow<Resource<List<PolicyInfoModel>>>
    suspend fun getAppPolicy(): Flow<Resource<List<PolicyInfoDto>>>

    suspend fun contactMsg(
        name: String,
        email: String,
        phone: String,
        subject: String,
        message: String,
        iam_not_robot: String
    ): Flow<Resource<ContactUsSendMessageResponse>>
}