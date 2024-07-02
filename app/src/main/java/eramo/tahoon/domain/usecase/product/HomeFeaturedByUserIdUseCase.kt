package eramo.tahoon.domain.usecase.product

import eramo.tahoon.domain.repository.ProductsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeFeaturedByUserIdUseCase @Inject constructor(private val repository: ProductsRepository) {
//    suspend operator fun invoke(): Flow<Resource<List<ProductModel>>> {
    suspend operator fun invoke(){
//        return repository.homeFeaturedByUserId()
    }
}