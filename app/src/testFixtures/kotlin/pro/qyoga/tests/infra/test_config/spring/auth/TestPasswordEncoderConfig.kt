@file:Suppress("DEPRECATION")

package pro.qyoga.tests.infra.test_config.spring.auth

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@TestConfiguration
class TestPasswordEncoderConfig(
    private val userDetailsService: UserDetailsService
) {

    // Стандартный BCryptPasswordEncoder кодирует пароли по 300мс, что слишком расточительно для тестов
    @Suppress("DEPRECATION")
    @Primary
    @Bean
    fun fastPasswordEncoder(): PasswordEncoder = NoOpPasswordEncoder.getInstance()

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {
        val daoAuthenticationProvider = DaoAuthenticationProvider(fastPasswordEncoder())
        daoAuthenticationProvider.setUserDetailsService(userDetailsService)
        return daoAuthenticationProvider
    }

}