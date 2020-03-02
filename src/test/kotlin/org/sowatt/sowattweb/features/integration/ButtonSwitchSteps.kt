package org.sowatt.sowattweb.features.integration

import io.cucumber.java8.Fr
import io.cucumber.java8.PendingException


class ButtonSwitchSteps : Fr {
    init {
        Etantdonnéque("mon bouton est associé à une commande d'éclairage") {  throw PendingException() }

        Alors("je peux trouver la commande d'éclairage associé à ce bouton") { throw PendingException() }


    }
}