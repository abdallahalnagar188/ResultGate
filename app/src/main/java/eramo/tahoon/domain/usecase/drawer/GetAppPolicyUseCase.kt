package eramo.tahoon.domain.usecase.drawer

import eramo.tahoon.data.remote.dto.drawer.PolicyInfoDto
import eramo.tahoon.domain.repository.DrawerRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppPolicyUseCase @Inject constructor(private val repository: DrawerRepository) {
    //    suspend operator fun invoke(): Flow<Resource<List<PolicyInfoModel>>> {
    suspend operator fun invoke(): Flow<Resource<List<PolicyInfoDto>>> {
        return repository.getAppPolicy()
    }
}