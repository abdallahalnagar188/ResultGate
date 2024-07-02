package eramo.resultgate.domain.usecase.drawer.myaccount

import eramo.resultgate.data.remote.dto.auth.UpdateInformationResponse
import eramo.resultgate.domain.repository.DrawerRepository
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditProfileUseCase @Inject constructor(private val repository: DrawerRepository) {
    suspend operator fun invoke(
        firstName: RequestBody?,
        lastName: RequestBody?,
        birthDate: RequestBody?,

        image: MultipartBody.Part?
    ): Flow<Resource<UpdateInformationResponse>> {
        return repository.editProfile(
             firstName, lastName, birthDate, image
        )
    }
}