package ru.tashkent.currencyapp.ui.common

import arrow.core.Either

typealias VoidEither<A> = Either<A, Unit>

fun <A, B> Either<A, B>.getErrorOrNull() = when (this) {
    is Either.Left -> value
    is Either.Right -> null
}

fun Either.Companion.void() = Either.Right(Unit)
