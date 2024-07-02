package eramo.tahoon.util

import javax.inject.Inject

class QuantityUtil @Inject constructor() {

    private var currentQty = 1
    var minQty = 1
    var maxQty = 99

    fun getQty() = currentQty.toString()

    fun setQty(value: Int) {
        this.currentQty = value
    }

    fun increase(step: Int = 1): String {
        currentQty += step
        if (currentQty > maxQty) currentQty = maxQty
        return currentQty.toString()
    }

    fun decrease(step: Int = 1): String {
        currentQty -= step
        if (currentQty < minQty) currentQty = minQty
        return currentQty.toString()
    }

}