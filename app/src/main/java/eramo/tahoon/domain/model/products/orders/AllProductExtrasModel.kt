package eramo.tahoon.domain.model.products.orders

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllProductExtrasModel(
    var id: String,
    var productIdFk: String,
    var extraIdFk: String,
    var extraQuantity: String,
    var extraPrice: String,
    var extraCost: String,
    var extraName: String,
    var isChecked: Boolean = false,
    var extraQty: String = ""
) : Parcelable