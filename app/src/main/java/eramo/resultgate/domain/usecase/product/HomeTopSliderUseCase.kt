package eramo.resultgate.domain.usecase.product

import eramo.resultgate.data.remote.dto.home.HomePageSliderResponse
import eramo.resultgate.domain.repository.ProductsRepository
import eramo.resultgate.util.state.Resource
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