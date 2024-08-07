package eramo.resultgate.domain.validation

import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Validation @Inject constructor(
    val validateText: ValidateText,
    val validateField: ValidateField,
    val validatePhone: ValidatePhone,
    val validateEmail: ValidateEmail,
    val validateNumber: ValidateNumber,
    val validateNotZero: ValidateNotZero,
    val validateNotMinusOne: ValidateNotMinusOne,
    val validatePassword: ValidatePassword,
    val validateRepeatPassword: ValidateRepeatPassword,

)
