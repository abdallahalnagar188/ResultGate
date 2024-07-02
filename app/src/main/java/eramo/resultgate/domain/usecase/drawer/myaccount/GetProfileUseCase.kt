package eramo.resultgate.domain.usecase.drawer.myaccount

import eramo.resultgate.data.remote.dto.general.Member
import eramo.resultgate.domain.repository.DrawerRepository
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetProfileUseCase @Inject constructor(private val repository: DrawerRepository) {
    suspend operator fun invoke(): Flow<Resource<Member>> {
        return repository.getProfile()
    }
}