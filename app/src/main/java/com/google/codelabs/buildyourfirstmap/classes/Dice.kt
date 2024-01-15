package com.google.codelabs.buildyourfirstmap.classes

import kotlin.random.Random

class Dice(val type: DiceType) {

    enum class DiceType {
        D4, D6, D8, D10, D12, D20
    }

    fun roll(): Int {
        return when (type) {
            DiceType.D4 -> Random.nextInt(1, 5)
            DiceType.D6 -> Random.nextInt(1, 7)
            DiceType.D8 -> Random.nextInt(1, 9)
            DiceType.D10 -> Random.nextInt(1, 11)
            DiceType.D12 -> Random.nextInt(1, 13)
            DiceType.D20 -> Random.nextInt(1, 21)
        }
    }

    fun copy(): Dice {
        return Dice(type)
    }
}