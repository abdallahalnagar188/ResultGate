package eramo.tahoon.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PreviewRequest(
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("user_name") var userName: String? = null,
    @SerializedName("user_phone") var userPhone: String? = null,
    @SerializedName("user_email") var userEmail: String? = null,
    @SerializedName("room_type_id_fk") var roomTypeIdFk: String? = null,
    @SerializedName("floor_type_id_fk") var floorTypeIdFk: String? = null,
    @SerializedName("width") var width: String? = null,
    @SerializedName("height") var height: String? = null,
    @SerializedName("num_windows") var numWindows: String? = null,
    @SerializedName("ac_type_id_fk") var acTypeIdFk: String? = null,
    @SerializedName("hot_cold") var hotCold: String? = null,
    @SerializedName("plasma_perfer") var plasmaPerfer: String? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("all_mo3ayna_details") var previewDetails: ArrayList<PreviewDetails> = arrayListOf()
) : Parcelable

@Parcelize
data class PreviewDetails(
    @SerializedName("window_internal_shape_fk") var windowInternalShapeFk: String? = null,
    @SerializedName("window_external_shape") var windowExternalShape: String? = null,
    @SerializedName("point_a") var pointA: String? = null,
    @SerializedName("point_b") var pointB: String? = null,
    @SerializedName("point_c") var pointC: String? = null,
    @SerializedName("point_d") var pointD: String? = null,
    @SerializedName("point_e") var pointE: String? = null,
    @SerializedName("point_f") var pointF: String? = null,
    @SerializedName("point_g") var pointG: String? = null,
    @SerializedName("point_h") var pointH: String? = null
) : Parcelable