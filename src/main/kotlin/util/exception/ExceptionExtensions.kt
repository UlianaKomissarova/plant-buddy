package dev.uliana.util.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Any.classLogger(): Logger = LoggerFactory.getLogger(this.javaClass.simpleName)
