package pl.fintech.dragonsinvestments.investmentcalculator

import groovy.sql.Sql
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification

class PostgreSQLContainerSpecification extends Specification {

    static def SCHEMA_NAME = 'public'
    static def USER = 'TEST_USER'
    static PostgreSQLContainer postgreSQLContainer

    static {
        postgreSQLContainer = new PostgreSQLContainer("postgres:9.6")
        .withUsername(USER)
        .withPassword(USER)

        postgreSQLContainer.start()
        createUser()
        createSchemaForUser()
    }

    @DynamicPropertySource
    static void datasourceConfig(DynamicPropertyRegistry registry) {
        registry.add('spring.datasource.url', { -> postgreSQLContainer.getJdbcUrl() })
        registry.add('spring.datasource.username', { -> USER })
        registry.add('spring.datasource.password', { -> USER })

        registry.add('spring.liquibase.enabled', { -> 'true' })
        registry.add('spring.liquibase.url', { -> postgreSQLContainer.getJdbcUrl() })
        registry.add('spring.liquibase.user', { -> USER })
        registry.add('spring.liquibase.password', { -> USER })
    }

    private static void  createUser() {
        Sql admin = getAdminSql()
        admin.execute("CREATE USER ${admin.expand(USER)} WITH PASSWORD '${admin.expand(USER)}'")
    }

    private static void createSchemaForUser() {
        Sql admin = getAdminSql()
        admin.execute("CREATE SCHEMA IF NOT EXISTS ${admin.expand(SCHEMA_NAME)} AUTHORIZATION ${admin.expand(USER)}")
        admin.execute("ALTER DATABASE ${admin.expand(postgreSQLContainer.databaseName)} SET search_path to '${admin.expand(SCHEMA_NAME)}'")
    }

    private static Sql getAdminSql() {
        Sql.newInstance(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword(),
                postgreSQLContainer.getDriverClassName()
        )
    }
}
