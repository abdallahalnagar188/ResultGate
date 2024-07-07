package eramo.resultgate.data.remote.dto.teams


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.teams.TeamsModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllTeamsResponse(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
) : Parcelable

@Parcelize
data class Data(
    @SerializedName("data")
    var `data`: List<DataX>?,
    @SerializedName("links")
    var links: Links?,
    @SerializedName("meta")
    var meta: Meta?
) : Parcelable

@Parcelize
data class DataX(
    @SerializedName("cap")
    var cap: Int?,
    @SerializedName("completed")
    var completed: Int?,
    @SerializedName("details")
    var details: String?,
    @SerializedName("fake_price")
    var fakePrice: String?,
    @SerializedName("grams")
    var grams: Int?,
    @SerializedName("grams_bought")
    var gramsBought: Int?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("is_completed")
    var isCompleted: Boolean?,
    @SerializedName("is_joined")
    var isJoined: Boolean?,
    @SerializedName("joined_members")
    var joinedMembers: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("real_price")
    var realPrice: String?,
    @SerializedName("remaining_grams")
    var remainingGrams: Int?,
    @SerializedName("researcher")
    var researcher: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("primary_image")
    val primaryImage: String?,
    @SerializedName("grams_orderd_before")
    val gramsOrderdBefore: Int?,
) : Parcelable{
    fun toTeamModel():TeamsModel{
        return TeamsModel(
            cap = cap?:-1,
            completed = completed?:-1,
            details = details?:"",
            fakePrice = fakePrice?:"",
            grams = grams?:-1,
            gramsBought = gramsBought?:-1,
            id = id?:-1,
            isCompleted = isCompleted?:false,
            isJoined = isJoined?:false,
            joinedMembers = joinedMembers?:-1,
            name = name?:"",
            realPrice = realPrice?:"",
            remainingGrams = remainingGrams?:-1,
            researcher = researcher?:"",
            status = status?:"",
            primaryImage = primaryImage?:"",
            gramsOrderdBefore = gramsOrderdBefore?:0
        )
    }
}

@Parcelize
data class Link(
    @SerializedName("active")
    var active: Boolean?,
    @SerializedName("label")
    var label: String?,
    @SerializedName("url")
    var url: String?
) : Parcelable

@Parcelize
data class Meta(
    @SerializedName("current_page")
    var currentPage: Int?,
    @SerializedName("from")
    var from: Int?,
    @SerializedName("last_page")
    var lastPage: Int?,
    @SerializedName("links")
    var links: List<Link?>?,
    @SerializedName("path")
    var path: String?,
    @SerializedName("per_page")
    var perPage: Int?,
    @SerializedName("to")
    var to: Int?,
    @SerializedName("total")
    var total: Int?
) : Parcelable

@Parcelize
data class Links(
    @SerializedName("first")
    var first: String?,
    @SerializedName("last")
    var last: String?,
    @SerializedName("next")
    var next: String?,
    @SerializedName("prev")
    var prev: String?
) : Parcelable