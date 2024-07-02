package eramo.resultgate.domain.usecase.drawer

import eramo.resultgate.data.remote.dto.drawer.PolicyInfoDto
import eramo.resultgate.domain.repository.DrawerRepository
import eramo.resultgate.util.state.Resource
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