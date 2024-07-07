package eramo.resultgate.data.remote.dto.teams.teamdetails


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.teams.TeamsModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TeamDetailsResponse(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
) : Parcelable

@Parcelize
data class Data(
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
    @SerializedName("grams_orderd_before")
    var gramsOrderdBefore: Int?,
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
    @SerializedName("primary_image")
    var primaryImage: String?,
    @SerializedName("real_price")
    var realPrice: String?,
    @SerializedName("remaining_grams")
    var remainingGrams: Int?,
    @SerializedName("researcher")
    var researcher: String?,
    @SerializedName("status")
    var status: String?
) : Parcelable{
    fun toTeamModel(): TeamsModel {
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