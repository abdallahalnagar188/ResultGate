package eramo.tahoon.data.remote.dto.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubCategoryModel(
    val id: Int,
    val parentId: Int,
    val title: String,
    val imageUrl: String,
    val type: String
) : Parcelable