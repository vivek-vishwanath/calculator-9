package com.calculator

import java.lang.RuntimeException
import java.lang.StringBuilder
import kotlin.math.*

internal operator fun StringBuilder.plusAssign(char: Char) {
    append(char)
}

internal fun Char.isDigit() = this in '0'..'9' || this == '.'

internal fun String.isNumber() = toDoubleOrNull() != null

internal fun String.isOperator() = this in arrayOf("+", "-", "*", "/", "^")

internal fun String.isOpen() = this == "("

internal fun String.isClose() = this == ")"

internal fun String.isParentheses() = this == "()"

internal fun String.isWord(): Boolean {
    for (c in this) if (c !in 'a'..'z' && c !in 'A'..'Z') return false
    return true
}

internal fun String.isComma() = this == ","

internal fun String.function(n: Double) = when (this) {
    "sin" -> sin(n)
    "cos" -> cos(n)
    "tan" -> tan(n)
    "csc" -> 1.0 / sin(n)
    "sec" -> 1.0 / cos(n)
    "cot" -> cos(n) / sin(n)
    "asin", "arcsin" -> asin(n)
    "acos", "arccos" -> acos(n)
    "atan", "arctan" -> atan(n)
    "acsc", "arccsc" -> asin(1 / n)
    "asec", "arcsec" -> acos(1 / n)
    "accot", "arccot" -> atan(1 / n)
    "sinh" -> sinh(n)
    "cosh" -> cosh(n)
    "tanh" -> tanh(n)
    "asinh", "arcsinh" -> asinh(n)
    "acosh", "arccosh" -> acosh(n)
    "atanh", "arctanh" -> atanh(n)
    else -> throw RuntimeException("internal function not recognized: '$this'")
}

internal fun String.function(values: DoubleArray): Double {
    val size = values.size
    return when (this) {
        "sum" -> values.sum()
        "mean" -> values.sum() / size
        "median" -> {
            values.sort()
            if (size % 2 == 0) {
                (values[size / 2 - 1] + values[size / 2]) / 2
            } else {
                values[size / 2]
            }
        }
        "stdev" -> values.deviation()
        else -> if (values.size == 1) function(values[0])
        else throw RuntimeException("internal function not recognized: '$this'")
    }
}

internal fun DoubleArray.sum(): Double {
    var sum = 0.0
    for (d in this) sum += d
    return sum
}

internal fun DoubleArray.deviation(): Double {
    val mean = sum() / size
    var sum = 0.0
    for (d in this) sum += (d - mean).pow(2)
    return sqrt(sum / (size - 1))
}

internal fun precedence(token: String) = when (token) {
    "+", "-" -> 1
    "*", "/" -> 2
    "^" -> 3
    else -> 0
}
