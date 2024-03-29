package pro.qyoga.tests.assertions

import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import pro.azhidkov.platform.file_storage.api.FileMetaData


infix fun FileMetaData.shouldMatch(another: FileMetaData) {
    this.shouldBeEqualToIgnoringFields(another, FileMetaData::id)
}