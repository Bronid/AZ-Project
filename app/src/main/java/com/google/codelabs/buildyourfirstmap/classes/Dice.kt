package com.google.codelabs.buildyourfirstmap.classes

import org.bson.Document
import java.io.Serializable
import kotlin.random.Random

class Dice(val type: DiceType) : Serializable {

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

    override fun toString(): String {
        return when (type) {
            DiceType.D4 -> "D4"
            DiceType.D6 -> "D6"
            DiceType.D8 -> "D8"
            DiceType.D10 -> "D10"
            DiceType.D12 -> "D12"
            DiceType.D20 -> "D20"
        }
    }

    fun toDocument(): Document {
        return Document("type", type.name)
    }

    fun copy(): Dice {
        return Dice(type)
    }
}