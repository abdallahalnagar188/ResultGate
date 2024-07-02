package eramo.resultgate.domain.usecase.auth

import eramo.resultgate.data.remote.dto.auth.UpdatePasswordResponse
import eramo.resultgate.domain.repository.AuthRepository
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePassUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(
        password: String,
        new_password: String,
        confirm_password: String
    ): Flow<Resource<UpdatePasswordResponse>> {
        val isBlank = password.isBlank() || new_password.isBlank() || confirm_password.isBlank()

        return if (isBlank) flow { }
        else repository.updatePass(password, new_password, confirm_password)
    }
}