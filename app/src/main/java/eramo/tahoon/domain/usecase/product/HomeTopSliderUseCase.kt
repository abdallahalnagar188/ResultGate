package eramo.tahoon.domain.usecase.product

import eramo.tahoon.data.remote.dto.home.HomePageSliderResponse
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeTopSliderUseCase @Inject constructor(private val repository: ProductsRepository) {
    suspend operator fun invoke(): Flow<Resource<HomePageSliderResponse>> {
//        return repository.homeAds()
        return repository.homeTopSlider()
    }
}