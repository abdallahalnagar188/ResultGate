package eramo.tahoon.domain.usecase.product

import eramo.tahoon.data.remote.dto.products.ProductDetailsResponse
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetProductByIdUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(productId: String): Flow<Resource<ProductDetailsResponse>> {
        return repository.getProductById(productId)
    }
}