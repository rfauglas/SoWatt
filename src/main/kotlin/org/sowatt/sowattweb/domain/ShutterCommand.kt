package org.sowatt.sowattweb.domain



data class ShutterCommand(var id: Long=0, val openButton: Button, val closeButton: Button, var shutter: ShutterDP) {
}