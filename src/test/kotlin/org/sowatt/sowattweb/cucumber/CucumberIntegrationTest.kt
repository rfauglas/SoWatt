package org.sowatt.sowattweb.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(features = ["src/test/resources/features/integration"])
public  class CucumberIntegrationTest