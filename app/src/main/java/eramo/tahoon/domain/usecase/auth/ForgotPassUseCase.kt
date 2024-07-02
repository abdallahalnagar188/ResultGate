package eramo.tahoon.domain.usecase.auth

import eramo.tahoon.data.remote.dto.auth.ValidateForgetPasswordResponse
import eramo.tahoon.data.remote.dto.auth.forget.GiveMeEmailResponse
import eramo.tahoon.domain.repository.AuthRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForgotPassUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(
        user_email: String
    ): Flow<Resource<GiveMeEmailResponse>> {
        return if (user_email.isBlank()) flow { }
        else repository.forgetPass(user_email)
    }

    suspend fun validateCode(code: String, user_email: String): Flow<Resource<ValidateForgetPasswordResponse>> {
        return if (user_email.isBlank()) flow { }
        else repository.validateForgetPasswordCode(code, user_email)
    }

    suspend fun changePassword(
        password: String,
        rePassword: String,
        user_email: String
    ): Flow<Resource<ValidateForgetPasswordResponse>> {
        return if (user_email.isBlank()) flow { }
        else repository.changePassword(password,rePassword,user_email)
    }
}