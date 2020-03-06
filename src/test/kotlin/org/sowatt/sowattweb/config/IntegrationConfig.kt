package org.sowatt.sowattweb.config

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.annotation.DirtiesContext
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

fun TestEntityManager.persistFixture(entity: Any): Any {
    val merged = this.persist(entity)
    return merged
}
