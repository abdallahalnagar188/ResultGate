package eramo.resultgate.domain.usecase.drawer.myaccount

import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.AddToMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.DeleteFromMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.GetMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.UpdateAddressResponse
import eramo.resultgate.domain.repository.DrawerRepository
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyAddressesUseCase @Inject constructor(private val repository: DrawerRepository) {
    suspend fun getMyAddresses(): Flow<Resource<GetMyAddressesResponse>> {
        return repository.getMyAddresses()
    }

    suspend fun addToMyAddresses(
        addressType: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        subRegionId:String
    ): Flow<Resource<AddToMyAddressesResponse>> {
        return repository.addToMyAddresses(addressType, address, countryId, cityId, regionId,subRegionId)
    }

    suspend fun deleteFromMyAddresses(addressId: String): Flow<Resource<DeleteFromMyAddressesResponse>> {
        return repository.deleteFromMyAddresses(addressId)
    }

    suspend fun updateAddress(
        addressId: String,
        addressType: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        subRegionId: String
    ): Flow<Resource<UpdateAddressResponse>> {
        return repository.updateAddress(addressId, addressType, address, countryId, cityId, regionId,subRegionId)
    }

}