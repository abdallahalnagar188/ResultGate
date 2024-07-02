package eramo.tahoon.domain.usecase.product

import androidx.paging.PagingData
import eramo.tahoon.domain.model.products.ShopProductModel
import eramo.tahoon.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AllCategorizationByUserIdUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(catId: String,brandId:String): Flow<PagingData<ShopProductModel>> {
        return repository.allCategorizationByUserId(catId,brandId)
    }
}