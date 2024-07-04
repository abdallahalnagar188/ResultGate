package eramo.resultgate.data.remote.dto.teams.deleteteam


import com.google.gson.annotations.SerializedName

data class ExitTeamResponse(
    @SerializedName("massage")
    var massage: String?,
    @SerializedName("status")
    var status: Int?
)