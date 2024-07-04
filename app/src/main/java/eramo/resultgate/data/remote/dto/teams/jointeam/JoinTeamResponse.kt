package eramo.resultgate.data.remote.dto.teams.jointeam


import com.google.gson.annotations.SerializedName

data class JoinTeamResponse(
    @SerializedName("massage")
    var massage: String?,
    @SerializedName("status")
    var status: Int?
)