package eramo.resultgate.domain.repository

import eramo.resultgate.data.remote.dto.alldevices.AllDevicesResponse
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow

interface AllDevicesRepo {
    suspend fun getAllDevices(): AllDevicesResponse
}