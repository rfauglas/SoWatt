package org.sowatt.sowattweb.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories ("org.sowatt.sowattweb")
class SowattConfig {
}