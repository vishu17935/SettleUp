package com.vishal.settleup.utils

fun Double.toCurrency(): String {
    return "₹${this.toInt()}"
}
