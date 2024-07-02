package eramo.tahoon.domain.usecase.drawer

import eramo.tahoon.data.remote.dto.drawer.ContactUsResponse
import eramo.tahoon.data.remote.dto.drawer.MyAppInfoResponse
import eramo.tahoon.domain.repository.DrawerRepository
import eramo.tahoon.util.state.Resource
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