package eramo.tahoon.domain.usecase.product

import androidx.paging.PagingData
import eramo.tahoon.domain.model.products.ProductModel
import eramo.tahoon.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LatestDealsByUserIdUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(): Flow<PagingData<ProductModel>> {
        return repository.latestDealsByUserId()
    }
}