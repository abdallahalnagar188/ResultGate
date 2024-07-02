package eramo.tahoon.domain.usecase.product

import eramo.tahoon.domain.model.products.MyProductModel
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeGetFeaturedProductsUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(): Flow<Resource<List<MyProductModel>>> {
//    suspend operator fun invoke(): Flow<Resource<FeaturedProductsResponse>> {
        return repository.homeGetFeaturedProducts()
    }
}