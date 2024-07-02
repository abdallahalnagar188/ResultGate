package eramo.tahoon.domain.usecase.drawer.myaccount

import eramo.tahoon.data.remote.dto.general.Member
import eramo.tahoon.domain.repository.DrawerRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetProfileUseCase @Inject constructor(private val repository: DrawerRepository) {
    suspend operator fun invoke(): Flow<Resource<Member>> {
        return repository.getProfile()
    }
}