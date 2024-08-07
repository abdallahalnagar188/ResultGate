package eramo.resultgate.data.repository

import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.dto.alldevices.AllDevicesResponse
import eramo.resultgate.domain.repository.AllDevicesRepo
import javax.inject.Inject

class AllDevicesRepoImpl @Inject constructor(private val api: EramoApi) : AllDevicesRepo {
    override suspend fun getAllDevices(): AllDevicesResponse {
        return api.allDevices()
    }
}