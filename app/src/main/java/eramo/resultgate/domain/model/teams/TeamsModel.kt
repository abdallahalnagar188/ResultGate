package eramo.resultgate.domain.model.teams

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TeamsModel (
    var cap: Int,
    var completed: Int,
    var details: String,
    var fakePrice: String,
    var grams: Int,
    var gramsBought: Int,
    var id: Int,
    var isCompleted: Boolean,
    var isJoined: Boolean,
    var joinedMembers: Int,
    var name: String?,
    var realPrice: String,
    var remainingGrams: Int,
    var researcher: String,
    var status: String,
    val primaryImage: String

) : Parcelable
