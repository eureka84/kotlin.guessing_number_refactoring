package fpmax

import arrow.Kind

interface RandomModule<E> {
    val randomNatural: RandomNatural<E>

    interface RandomNatural<E> {
        fun upTo(upper: Int): Kind<E, Int>
    }
}