package pro.qyoga.tests.infra.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import java.sql.DriverManager
import java.sql.SQLException

const val PROVIDED_DB_URL = "jdbc:postgresql://localhost:54502/qyoga"

object TestDb

private val log = LoggerFactory.getLogger(TestDb::class.java)

private const val DB_USER = "postgres"
private const val DB_PASSWORD = "password"

val jdbcUrl: String by lazy {
    try {
        val con = DriverManager.getConnection(
            PROVIDED_DB_URL.replace("qyoga", DB_USER),
            DB_USER,
            DB_PASSWORD
        )
        log.info("Provided db found, recreating it")
        con.prepareStatement(
            """
                DROP DATABASE IF EXISTS qyoga;
                CREATE DATABASE qyoga;
            """.trimIndent()
        )
            .execute()
        log.info("Provided db found, recreated")
        PROVIDED_DB_URL
    } catch (e: SQLException) {
        log.info("Provided Db not found: ${e.message}")
        pgContainer.jdbcUrl
    }
}

val testDataSource by lazy {
    migrateSchema(jdbcUrl)
    val config = HikariConfig().apply {
        this.jdbcUrl = pro.qyoga.tests.infra.db.jdbcUrl
        this.username = DB_USER
        this.password = DB_PASSWORD
    }
    HikariDataSource(config)
}

fun migrateSchema(jdbcUrl: String) {
    log.info("Migrating schema")
    Flyway.configure()
        .dataSource(jdbcUrl, DB_USER, DB_PASSWORD)
        .locations("classpath:/db/migration/common")
        .load()
        .migrate()
    log.info("Schema migrated")
}
