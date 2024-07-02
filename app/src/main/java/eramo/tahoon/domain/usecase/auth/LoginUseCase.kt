package eramo.tahoon.domain.usecase.auth

import eramo.tahoon.domain.model.auth.LoginModel
import eramo.tahoon.domain.repository.AuthRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(
        user_phone: String, user_pass: String
    ): Flow<Resource<LoginModel>> {
        val isBlank = user_phone.isBlank() || user_pass.isBlank()

        return if (isBlank) flow { }
        else repository.loginApp(user_phone, user_pass)
    }
}