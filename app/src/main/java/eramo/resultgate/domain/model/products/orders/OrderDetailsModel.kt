package eramo.resultgate.domain.model.products.orders

import android.os.Parcelable
import eramo.resultgate.data.remote.dto.products.common.AllImagesDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderDetailsModel(
    var id: String,
    var orderIdFk: String,
    var productIdFk: String,
    var quantity: String,
    var price: String,
    var installationPrice: String,
    var supplierIdFk: String,
    var fanSn: String,
    var compressorSn: String,
    var supplierName: String,
    var manufacturerName: String,
    var modelName: String,
    var powerName: String,
    var productName: String,
    var modelNumber: String,
    var sku: String,
    var status: String,
    var mainCat: String,
    var subCat: String,
    var shippingPrice: String,
    var taxesPrice: String,
    var allImageDtos: List<AllImagesDto>?
) : Parcelable