package eramo.resultgate.domain.usecase.product

import eramo.resultgate.domain.repository.AllDevicesRepo
import javax.inject.Inject

class GetAllDevices @Inject constructor(private val allDevicesRepo: AllDevicesRepo) {
    suspend operator fun invoke() = allDevicesRepo.getAllDevices()
}