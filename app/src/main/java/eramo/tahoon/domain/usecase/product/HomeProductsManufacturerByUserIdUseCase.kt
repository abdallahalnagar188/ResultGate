package eramo.tahoon.domain.usecase.product

import eramo.tahoon.domain.model.products.CategoryModel
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeProductsManufacturerByUserIdUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(): Flow<Resource<List<CategoryModel>>> {
        return repository.homeProductsManufacturersByUserId()
    }
}