package eramo.resultgate.domain.usecase.auth

import eramo.resultgate.data.remote.dto.auth.CheckVerifyMailCodeResponse
import eramo.resultgate.data.remote.dto.auth.SendVerifyMailResponse
import eramo.resultgate.domain.repository.AuthRepository
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class VerifyAccountUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend fun sendVerifyMail(userEmail: String): Flow<Resource<SendVerifyMailResponse>> {
        return if (userEmail.isBlank()) flow { }
        else repository.sendVerifyMail(userEmail)
    }

    suspend fun checkVerifyMailCode(code: String, userEmail: String): Flow<Resource<CheckVerifyMailCodeResponse>> {
        return if (code.isBlank()) flow { }
        else repository.checkVerifyMailCode(code, userEmail)
    }
}