package com.google.codelabs.buildyourfirstmap.classes

object HostileRepository {
    private val allHostiles: List<HostileCharacter> = listOf(
        HostileCharacter("Rat", 4, "Rat is going to kill you!", listOf(Dice(Dice.DiceType.D4)), EventLevel.SAFE, 5, listOf(GameItem("Rat's head", "The head of dead rat", EventLevel.SAFE)))
    )

    fun getRandomHostileByEventLevel(eventLevel: EventLevel): HostileCharacter? {
        val hostilesByLevel = allHostiles.filter { it.dangerLevel == eventLevel }.toMutableList()

        if (hostilesByLevel.isNotEmpty()) {
            hostilesByLevel.shuffle()
            return hostilesByLevel.random()
        }

        return null
    }
}

object ItemRepository {
    private val allItems: List<GameItem> = listOf(
        GameItem("Rat's head", "The head of dead rat", EventLevel.SAFE),
        GameItemHeal("Bandage", "Uses for stop bleeding", EventLevel.SAFE, listOf(Dice(Dice.DiceType.D4))),
        GameItemHeal("AI-2", "Basic medicine in the Zone", EventLevel.SAFE, listOf(Dice(Dice.DiceType.D6))),
        GameItemHeal("Army Medkit", "Army medkit", EventLevel.NEUTRAL, listOf(Dice(Dice.DiceType.D8))),
        GameItemHeal("Professional Salewa", "Basic medicine in the Zone", EventLevel.DANGER, listOf(Dice(Dice.DiceType.D10))),
    )

    fun getRandomItemByEventLevel(eventLevel: EventLevel): GameItem? {
        val itemsByLevel = allItems.filter { it.dangerLevel == eventLevel }.toMutableList()

        if (itemsByLevel.isNotEmpty()) {
            itemsByLevel.shuffle()
            return itemsByLevel.random()
        }

        return null
    }
}