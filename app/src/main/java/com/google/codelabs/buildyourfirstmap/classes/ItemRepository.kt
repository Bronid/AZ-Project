package com.google.codelabs.buildyourfirstmap.classes

object HostileRepository {
    private val allHostiles: List<HostileCharacter> = listOf(
        // Hostile for SAFE level
        HostileCharacter("Rat", 3, "Rat is going to kill you!", listOf(Dice(Dice.DiceType.D4)), EventLevel.SAFE, 5, listOf(GameItem("Rat's head", "The head of dead rat", EventLevel.SAFE))),
        HostileCharacter("Toxic Slime", 6, "A toxic slime is oozing towards you!", listOf(Dice(Dice.DiceType.D6)), EventLevel.NEUTRAL, 8, listOf(GameItem("Toxic Slime Residue", "Residue from a toxic slime", EventLevel.NEUTRAL))),

        // Hostile for DANGER level
        HostileCharacter("Mutant Dog", 8, "A mutated dog is attacking!", listOf(Dice(Dice.DiceType.D6)), EventLevel.DANGER, 10, listOf(GameItem("Mutant Dog's Fang", "Sharp fang from a mutated dog", EventLevel.DANGER))),

        // Hostile for HARDCORE level
        HostileCharacter("Giant Spider", 12, "A giant mutated spider is lurking!", listOf(Dice(Dice.DiceType.D8)), EventLevel.HARDCORE, 15, listOf(GameItem("Giant Spider's Silk", "Tough silk from a giant mutated spider", EventLevel.HARDCORE)))
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
        GameItemArmor("TestArmor", "just test armor", EventLevel.SAFE, 5),
        GameItemWeapon("TestWeapon", "just test weapon", EventLevel.SAFE, listOf(Dice(Dice.DiceType.D8), Dice(Dice.DiceType.D8))),
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