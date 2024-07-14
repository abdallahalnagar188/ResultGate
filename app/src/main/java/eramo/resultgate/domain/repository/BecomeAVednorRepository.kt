package eramo.resultgate.domain.repository

import eramo.resultgate.data.remote.dto.becomeavendor.BecomeAVendorResponse
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Field

interface BecomeAVednorRepository {
    suspend fun becomeAVednor(
        name: String,
        email: String,
        phone: String,
        comment: String?,
        vendorType: String,
    ):Flow<Resource<BecomeAVendorResponse>>
}