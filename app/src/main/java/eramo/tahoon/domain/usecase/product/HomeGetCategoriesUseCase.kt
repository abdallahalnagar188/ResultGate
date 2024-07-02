package eramo.tahoon.domain.usecase.product

import eramo.tahoon.data.remote.dto.home.HomeCategoriesResponse
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeGetCategoriesUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(brandId:String?): Flow<Resource<HomeCategoriesResponse>> {
        return repository.homeGetCategories(brandId)
    }

    suspend fun homeGetSubCategories(): Flow<Resource<HomeCategoriesResponse>> {
        return repository.homeGetSubCategories()
    }
}