package org.sowatt.sowattweb.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories("org.sowatt.sowattweb")
class SowattConfig {
}