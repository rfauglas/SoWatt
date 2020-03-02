package org.sowatt.sowattweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SowattWebApplication

fun main(args: Array<String>) {
	runApplication<SowattWebApplication>(*args)
}
