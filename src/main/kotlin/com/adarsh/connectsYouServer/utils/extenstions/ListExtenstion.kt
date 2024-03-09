package com.adarsh.connectsYouServer.utils.extenstions

fun <T> List<T>.safeSubList(
    fromIndex: Int,
    toIndex: Int,
): List<T> = subList(fromIndex.coerceAtLeast(0), toIndex.coerceAtMost(size))

fun <T> List<T>.safeSlice(intRange: IntRange): List<T> = slice(intRange.first.coerceAtLeast(0)..intRange.last.coerceAtMost(size - 1))