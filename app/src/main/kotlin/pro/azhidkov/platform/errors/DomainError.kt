package pro.azhidkov.platform.errors

open class DomainError(
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)