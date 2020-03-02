package org.sowatt.sowattweb.domain

import org.sowatt.sowattweb.domain.types.ButtonAction
import java.util.*

data class ButtonActionEvent(var buttonAction: ButtonAction, var date: Date) {
}