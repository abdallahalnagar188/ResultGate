package eramo.tahoon.domain.repository

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
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRepository {

    suspend fun onBoardingScreens(): Flow<Resource<List<OnBoardingModel>>>

//    suspend fun register(
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
//    ): Flow<Resource<ResultModel>>


    suspend fun register(
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
    ): Flow<Resource<SignUpModel>>

    suspend fun suspendAccount(): Flow<Resource<SuspendAccountResponse>>

    suspend fun loginApp(user_phone: String, user_pass: String): Flow<Resource<LoginModel>>

    suspend fun updateFcmToken(
        fcmToken: String
    ): Flow<Resource<UpdateFcmTokenResponse>>

    suspend fun forgetPass(user_email: String): Flow<Resource<GiveMeEmailResponse>>

    suspend fun validateForgetPasswordCode(code: String, email: String): Flow<Resource<ValidateForgetPasswordResponse>>

    suspend fun changePassword(
        password: String,
        rePassword: String,
        user_email: String
    ): Flow<Resource<ValidateForgetPasswordResponse>>

    suspend fun updatePass(
        password: String,
        new_password: String,
        confirm_password: String
    ): Flow<Resource<UpdatePasswordResponse>>

    suspend fun allCountries(): Flow<Resource<List<CountriesModel>>>

    suspend fun allCities(countryId: String): Flow<Resource<List<CitiesModel>>>

    suspend fun allCitiesWithRegions(countryId: String): Flow<Resource<List<CitiesWithRegionsModel>>>

    suspend fun allRegions(cityId: String): Flow<Resource<List<RegionsModel>>>

    suspend fun allSubRegions(regionId: String): Flow<Resource<List<SubRegionsModel>>>

    // ---------------------------------- SignUpVerification ---------------------------------- //
    suspend fun sendVerifyMail(userEmail: String): Flow<Resource<SendVerifyMailResponse>>

    suspend fun checkVerifyMailCode(code: String, userEmail: String): Flow<Resource<CheckVerifyMailCodeResponse>>
}