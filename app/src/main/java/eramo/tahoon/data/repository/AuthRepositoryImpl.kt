package eramo.tahoon.data.repository

import eramo.tahoon.data.remote.EramoApi
import eramo.tahoon.data.remote.dto.UpdateFcmTokenResponse
import eramo.tahoon.data.remote.dto.auth.CheckVerifyMailCodeResponse
import eramo.tahoon.data.remote.dto.auth.SendVerifyMailResponse
import eramo.tahoon.data.remote.dto.auth.UpdatePasswordResponse
import eramo.tahoon.data.remote.dto.auth.ValidateForgetPasswordResponse
import eramo.tahoon.data.remote.dto.auth.forget.GiveMeEmailResponse
import eramo.tahoon.data.remote.dto.drawer.myaccount.SuspendAccountResponse
import eramo.tahoon.domain.model.auth.CitiesModel
import eramo.tahoon.domain.model.auth.CitiesWithRegionsModel
import eramo.tahoon.domain.model.auth.CountriesModel
import eramo.tahoon.domain.model.auth.LoginModel
import eramo.tahoon.domain.model.auth.OnBoardingModel
import eramo.tahoon.domain.model.auth.RegionsModel
import eramo.tahoon.domain.model.auth.SignUpModel
import eramo.tahoon.domain.model.auth.SubRegionsModel
import eramo.tahoon.domain.repository.AuthRepository
import eramo.tahoon.util.Constants
import eramo.tahoon.util.Constants.API_SUCCESS
import eramo.tahoon.util.MySingleton
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.state.ApiState
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiText
import eramo.tahoon.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepositoryImpl(private val eramoApi: EramoApi) : AuthRepository {

    override suspend fun onBoardingScreens(): Flow<Resource<List<OnBoardingModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.onBoardingScreens() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toOnBoardingModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

//    override suspend fun register(
//        store_name: RequestBody?,
//        store_email: RequestBody?,
//        store_phone: RequestBody?,
//        store_pass: RequestBody?,
//        country_id: RequestBody?,
//        city_id: RequestBody?,
//        region_id: RequestBody?,
//        address: RequestBody?,
//        commerical_img: MultipartBody.Part?,
//        tax_card_img: MultipartBody.Part?,
//        profile_img: MultipartBody.Part?
//    ): Flow<Resource<ResultModel>> {
//        return flow {
//            val result = toResultFlow {
//                eramoApi.register(
//                    store_name,
//                    store_email,
//                    store_phone,
//                    store_pass,
//                    country_id,
//                    city_id,
//                    region_id,
//                    address,
//                    commerical_img,
//                    tax_card_img,
//                    profile_img
//                )
//            }
//            result.collect {
//                when (it) {
//                    is ApiState.Loading -> emit(Resource.Loading())
//                    is ApiState.Error -> emit(Resource.Error(it.message!!))
//                    is ApiState.Success -> {
//                        val model = it.data?.toResultModel()
//                        if (it.data?.success == 1) emit(Resource.Success(model))
//                        else emit(Resource.Error(UiText.DynamicString(model?.message ?: "Error")))
//                    }
//                }
//            }
//        }
//    }


    override suspend fun register(
        firstName: RequestBody?,
        lastName: RequestBody?,
        email: RequestBody?,
        password: RequestBody?,
        passwordConfirmation: RequestBody?,
        phone: RequestBody?,
        address: RequestBody?,
        addressType: RequestBody?,
        birthDate: RequestBody?,
        gender: RequestBody?,
        signFrom: RequestBody?,
        countryId: RequestBody?,
        cityId: RequestBody?,
        regionId: RequestBody?,
        subRegionId: RequestBody?,
        image: MultipartBody.Part?
    ): Flow<Resource<SignUpModel>> {
        return flow {
            val result = toResultFlow {
                eramoApi.register(
                    firstName,
                    lastName,
                    email,
                    password,
                    passwordConfirmation,
                    phone,
                    address,
                    addressType,
                    birthDate,
                    gender,
                    signFrom,
                    countryId,
                    cityId,
                    regionId,
                    subRegionId,
                    image
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> {
                        emit(Resource.Loading())
                    }

                    is ApiState.Error -> {
                        emit(Resource.Error(it.message!!))
                    }

                    is ApiState.Success -> {
                        if (it.data?.status == API_SUCCESS) {
                            emit(Resource.Success(it.data.toSignUpModel()))
                        } else {
                            emit(Resource.Error(UiText.DynamicString(it.data?.message ?: "Error")))
                        }
                    }
                }
            }

        }
    }

    override suspend fun suspendAccount(
    ): Flow<Resource<SuspendAccountResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.suspendAccount("Bearer ${UserUtil.getUserToken()}")
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> {
                        emit(Resource.Loading())
                    }

                    is ApiState.Error -> {
                        emit(Resource.Error(it.message!!))
                    }

                    is ApiState.Success -> {
                        if (it.data?.status == API_SUCCESS) {
                            emit(Resource.Success(it.data))
                        } else {
                            emit(Resource.Error(UiText.DynamicString(it.data?.message ?: "Error")))
                        }
                    }
                }
            }

        }
    }

    override suspend fun loginApp(
        user_phone: String,
        user_pass: String
    ): Flow<Resource<LoginModel>> {
        return flow {
            val result = toResultFlow {
                eramoApi.loginApp(
                    user_phone, user_pass,
                    if (MySingleton.firebaseToken != "") MySingleton.firebaseToken else null
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> {
                        emit(Resource.Loading())
                    }

                    is ApiState.Error -> {
                        emit(Resource.Error(it.message!!))
                    }

                    is ApiState.Success -> {
                        if (it.data?.member != null) {
                            emit(Resource.Success(it.data.toLoginModel()))
                        } else {
                            emit(Resource.Error(UiText.DynamicString("Member is null")))
                        }
                    }
                }
            }
        }
    }

    override suspend fun updateFcmToken(
        fcmToken: String
    ): Flow<Resource<UpdateFcmTokenResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.updateFcmToken("Bearer ${UserUtil.getUserToken()}", fcmToken) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> {
                        emit(Resource.Loading())
                    }

                    is ApiState.Error -> {
                        emit(Resource.Error(it.message!!))
                    }

                    is ApiState.Success -> {
                        if (it.data?.status == API_SUCCESS) {
                            emit(Resource.Success(it.data))
                        } else {
                            emit(Resource.Error(UiText.DynamicString("Member is null")))
                        }
                    }
                }
            }
        }
    }

    override suspend fun forgetPass(user_email: String): Flow<Resource<GiveMeEmailResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.giveMeEmail(user_email) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        val model = it.data
//                        if (it.data?.success == 1) emit(Resource.Success(model))
                        if (model?.status == Constants.API_SUCCESS) emit(Resource.Success(model))
                        else emit(Resource.Error(UiText.DynamicString(model?.message ?: "Error")))
                    }
                }
            }
        }
    }

    override suspend fun validateForgetPasswordCode(code: String, email: String): Flow<Resource<ValidateForgetPasswordResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.validateForgetPasswordCode(code, email) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        emit(Resource.Success(it.data))
//                        if (data.status == API_SUCCESS) emit(Resource.Success(it.data))
//                        else emit(Resource.Error(UiText.DynamicString("Error")))
                    }
                }
            }
        }
    }

    override suspend fun changePassword(
        password: String,
        rePassword: String,
        user_email: String
    ): Flow<Resource<ValidateForgetPasswordResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.changePassword(password, rePassword, user_email) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        emit(Resource.Success(it.data))
//                        val msg = it.data?.msg
//                        if (!msg.isNullOrEmpty()) emit(Resource.Success(it.data))
//                        else emit(Resource.Error(UiText.DynamicString("Error")))
                    }
                }
            }
        }
    }

    override suspend fun updatePass(
        password: String,
        new_password: String,
        confirm_password: String
    ): Flow<Resource<UpdatePasswordResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.updatePass("Bearer ${UserUtil.getUserToken()}", password, new_password, confirm_password)
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        if (it.data != null) emit(Resource.Success(it.data))
                        else emit(Resource.Error(UiText.DynamicString("Error")))
                    }
//                    is ApiState.Loading -> emit(Resource.Loading())
//                    is ApiState.Error -> emit(Resource.Error(it.message!!))
//                    is ApiState.Success -> {
//                        val model = it.data?.toResultModel()
//                        if (it.data?.success == 1) emit(Resource.Success(model))
//                        else emit(Resource.Error(UiText.DynamicString(model?.message ?: "Error")))
//                    }
                }
            }
        }
    }

    override suspend fun allCountries(): Flow<Resource<List<CountriesModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.allCountries() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toCountriesModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun allCities(countryId: String): Flow<Resource<List<CitiesModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.allCities(countryId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toCitiesModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun allCitiesWithRegions(countryId: String): Flow<Resource<List<CitiesWithRegionsModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.allCities(countryId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toCitiesWithRegionsModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun allRegions(cityId: String): Flow<Resource<List<RegionsModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.allRegions(cityId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toRegionsModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun allSubRegions(regionId: String): Flow<Resource<List<SubRegionsModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.allSubRegions(regionId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toSubRegionModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    // ---------------------------------- SignUpVerification ---------------------------------- //
    override suspend fun sendVerifyMail(userEmail: String): Flow<Resource<SendVerifyMailResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.sendVerifyMail(userEmail) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        if (apiState.data?.status == API_SUCCESS) {
                            emit(Resource.Success(apiState.data))
                        } else {
                            emit(Resource.Error(UiText.DynamicString(apiState.data?.message ?: "Error")))
                        }
                    }
                }
            }
        }
    }

    override suspend fun checkVerifyMailCode(code: String, userEmail: String): Flow<Resource<CheckVerifyMailCodeResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.checkVerifyMailCode(userEmail, code) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        if (apiState.data?.status == API_SUCCESS) {
                            emit(Resource.Success(apiState.data))
                        } else {
                            emit(Resource.Error(UiText.DynamicString(apiState.data?.message ?: "Error")))
                        }
                    }
                }
            }
        }
    }

}