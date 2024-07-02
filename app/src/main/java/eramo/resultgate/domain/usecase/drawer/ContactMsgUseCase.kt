package eramo.resultgate.domain.usecase.drawer

import eramo.resultgate.data.remote.dto.drawer.ContactUsSendMessageResponse
import eramo.resultgate.domain.repository.DrawerRepository
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactMsgUseCase @Inject constructor(private val repository: DrawerRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        phone: String,
        subject: String,
        message: String,
        iam_not_robot: String
    ): Flow<Resource<ContactUsSendMessageResponse>> {
        val isBlank = name.isBlank() || email.isBlank() || phone.isBlank()
                || subject.isBlank() || message.isBlank() || iam_not_robot.isBlank()
        return if (isBlank) flow { }
        else repository.contactMsg(name, email, phone, subject, message, iam_not_robot)
    }
}