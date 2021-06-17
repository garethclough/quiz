package eu.electricocean.quiz

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }
