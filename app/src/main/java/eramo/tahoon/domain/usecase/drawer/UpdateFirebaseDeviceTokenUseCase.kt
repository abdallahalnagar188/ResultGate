package eramo.tahoon.domain.usecase.drawer

import eramo.tahoon.domain.model.ResultModel
import eramo.tahoon.domain.repository.DrawerRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateFirebaseDeviceTokenUseCase @Inject constructor(private val repository: DrawerRepository) {
    suspend operator fun invoke(deviceToken: String): Flow<Resource<ResultModel>> {
        return repository.updateFirebaseDeviceToken(deviceToken)
    }
}