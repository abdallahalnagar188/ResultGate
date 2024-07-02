package eramo.tahoon.domain.usecase.product

import eramo.tahoon.data.remote.dto.home.HomeCounterResponse
import eramo.tahoon.domain.model.products.MyProductModel
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeDealsByUserIdUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(): Flow<Resource<List<MyProductModel>>> {
        return repository.homeDealsByUserId()
    }
    suspend  fun getHomeCounter(): Flow<Resource<HomeCounterResponse>> {
        return repository.getHomeCounter()
    }
}