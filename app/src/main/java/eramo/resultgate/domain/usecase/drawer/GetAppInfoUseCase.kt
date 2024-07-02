package eramo.resultgate.domain.usecase.drawer

import eramo.resultgate.data.remote.dto.drawer.ContactUsResponse
import eramo.resultgate.data.remote.dto.drawer.MyAppInfoResponse
import eramo.resultgate.domain.repository.DrawerRepository
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppInfoUseCase @Inject constructor(private val repository: DrawerRepository) {
    suspend operator fun invoke(): Flow<Resource<MyAppInfoResponse>> {
        return repository.getAppInfo()
    }

    suspend fun getContactUsAppInfo(): Flow<Resource<ContactUsResponse>> {
        return repository.getContactUsAppInfo()
    }
}