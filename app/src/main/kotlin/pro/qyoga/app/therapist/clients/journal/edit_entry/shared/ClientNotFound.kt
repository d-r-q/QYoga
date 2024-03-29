package pro.qyoga.app.therapist.clients.journal.edit_entry.shared

import pro.azhidkov.platform.errors.DomainError

class ClientNotFound(clientId: Long) : DomainError("Client with id $clientId not found")