package eramo.resultgate.domain.usecase.product

import eramo.resultgate.domain.model.FilterCategoryModel
import eramo.resultgate.domain.repository.ProductsRepository
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFilterCategoriesUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(): Flow<Resource<List<FilterCategoryModel>>> {
        return repository.getFilterCategories()
    }
}