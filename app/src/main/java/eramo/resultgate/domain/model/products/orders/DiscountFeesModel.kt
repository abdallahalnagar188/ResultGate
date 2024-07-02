package eramo.resultgate.domain.model.products.orders

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscountFeesModel(
    var id: String,
    var orderIdFk: String,
    var discount: String,
    var userId: String,
    var addedDate: String,
    var addedTime: String,
    var notes: String
) : Parcelable