package pro.qyoga.core.clients.cards.api

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table
import pro.azhidkov.platform.spring.sdj.erpo.hydration.Identifiable
import pro.qyoga.core.users.therapists.Therapist
import java.time.Instant
import java.time.LocalDate

typealias ClientRef = AggregateReference<Client, Long>

@Table("clients")
data class Client(
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val birthDate: LocalDate?,
    val phoneNumber: String,
    val email: String?,
    val address: String?,
    val complaints: String?,
    val anamnesis: String?,
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "distribution_source_")
    val distributionSource: DistributionSource?,
    val therapistId: AggregateReference<Therapist, Long>,

    @Id
    override val id: Long = 0,
    @CreatedDate
    val createdAt: Instant = Instant.now(),
    @LastModifiedDate
    val modifiedAt: Instant? = null,
    @Version
    val version: Long = 0
) : Identifiable<Long> {

    constructor(therapistId: Long, createRequest: ClientCardDto) :
            this(
                createRequest.firstName,
                createRequest.lastName,
                createRequest.middleName,
                createRequest.birthDate,
                createRequest.phoneNumber,
                createRequest.email,
                createRequest.address,
                createRequest.complaints,
                createRequest.anamnesis,
                createRequest.distributionSource,
                AggregateReference.to(therapistId)
            )

    fun fullName() = listOf(lastName, firstName, middleName)
        .filter { it?.isNotBlank() ?: false }
        .joinToString(" ")

    fun updateBy(clientCardDto: ClientCardDto): Client = Client(
        firstName = clientCardDto.firstName,
        lastName = clientCardDto.lastName,
        middleName = clientCardDto.middleName,
        birthDate = clientCardDto.birthDate,
        phoneNumber = clientCardDto.phoneNumber,
        email = clientCardDto.email,
        address = clientCardDto.address,
        distributionSource = clientCardDto.distributionSource,
        complaints = clientCardDto.complaints,
        anamnesis = clientCardDto.anamnesis,
        therapistId = therapistId,

        id = id,
        version = version
    )

}
