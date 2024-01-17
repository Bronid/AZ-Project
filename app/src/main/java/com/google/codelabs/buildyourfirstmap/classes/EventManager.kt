package com.google.codelabs.buildyourfirstmap.classes

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

enum class EventLevel {
    SAFE,
    NEUTRAL,
    DANGER,
    HARDCORE
}

class GameEventBase(
    val eventType: EventType,
    val eventLevel: EventLevel,
    val eventText: String
) {
    var hostile: HostileCharacter? = null
    var item: GameItem? = null

    enum class EventType {
        POSITIVE,
        NEUTRAL,
        NEGATIVE
    }

    fun generateContent() {
        when (eventType) {
            EventType.POSITIVE -> generatePositiveContent()
            EventType.NEUTRAL -> generateNeutralContent()
            EventType.NEGATIVE -> generateNegativeContent()
        }
    }

    private fun generatePositiveContent() {
        item = ItemRepository.getRandomItemByEventLevel(eventLevel)
    }

    private fun generateNeutralContent() {
        // Implement logic for generating neutral content if needed
    }

    private fun generateNegativeContent() {
        hostile = HostileRepository.getRandomHostileByEventLevel(eventLevel)?.copy()
    }
}
class EventManager(private val character: PlayerCharacter){
    var inBattle = false
    var currentEnemy: HostileCharacter? = null
    var currentPlayerTurn = false
    fun generateRandomEvent() : String {
        if (inBattle) {
            currentEnemy?.let { return turnBattle(it) }
        }
        else {
        val randomEvent = generateRandomGameEvent()
        println("Event: ${randomEvent.eventText}")

            when (randomEvent.eventType) {
                GameEventBase.EventType.POSITIVE -> return handlePositiveEvent(randomEvent)
                GameEventBase.EventType.NEUTRAL -> return handleNeutralEvent(randomEvent)
                GameEventBase.EventType.NEGATIVE -> return handleNegativeEvent(randomEvent)
            }
        }
        return "error"
    }

    private fun handlePositiveEvent(event: GameEventBase) : String {
        val item = event.item
        if (item is GameItemHeal) {
            val text = "You found a item: ${item.name}"
            println(text)
            character.inventory.add(item)
            return text
        }
        return ""
        // Handle other positive events if needed
    }

    private fun handleNeutralEvent(event: GameEventBase) : String {
        return "Just walking"
    }

    private fun handleNegativeEvent(event: GameEventBase) : String {
        val hostile = event.hostile
        if (hostile != null) {
            val text = "A wild ${hostile.name} appeared!"
            println(text)
            startBattle(hostile)
            return text
        }
        return ""
        // Handle other negative events if needed
    }

    private fun startBattle(hostile: HostileCharacter) {
        println("Battle started! You vs ${hostile.name}")

        // Roll initiative for the player and hostile
        val playerInitiative = Dice(Dice.DiceType.D20).roll()
        val hostileInitiative = Dice(Dice.DiceType.D20).roll()

        println("Initiative - You: $playerInitiative, ${hostile.name}: $hostileInitiative")

        // Determine who goes first based on initiative
        currentPlayerTurn = playerInitiative >= hostileInitiative
        currentEnemy = hostile
        inBattle = true
    }

    private fun turnBattle(hostile: HostileCharacter): String {
        var text = ""

        if (!character.isKnocked() && hostile.health > 0) {
            if (currentPlayerTurn) {
                // Player's turn
                val playerDamage = character.attack()
                text = "You dealt $playerDamage damage to ${hostile.name}"
                println(text)
                hostile.health -= playerDamage // Update hostile's health here
            } else {
                // Hostile's turn
                val hostileDamage = hostile.attack()
                text = "${hostile.name} dealt $hostileDamage damage to you"
                println(text)
                character.changeHealth(-hostileDamage)
            }

            // Toggle turn for the next round
            currentPlayerTurn = !currentPlayerTurn

            // Print the current health after each round
            println("Your health: ${character.getCurrentHealth()}/${character.getMaxHealth()}, ${hostile.name}'s health: ${hostile.health}")

            // Check for victory after updating health
            if (hostile.health <= 0) {
                text = "You defeated ${hostile.name}! Victory!"
                println(text)

                // Player gains experience
                character.currentExperience += hostile.exp
                println("You gained ${hostile.exp} experience!")

                // 50% chance to get a random item from the loot
                if (Random.nextBoolean() && hostile.loot.isNotEmpty()) {
                    val randomLoot = hostile.loot.shuffled().first()
                    character.inventory.add(randomLoot)
                    println("You obtained ${randomLoot.name} from the loot!")
                }
                inBattle = false
            }
        }

        if (character.isKnocked()) {
            text = "Game over! You were defeated by ${hostile.name}"
            println(text)
            inBattle = false
        }

        return text
    }



    private fun generateRandomGameEvent(): GameEventBase {
        val activityList = listOf(
            GameEventBase(GameEventBase.EventType.NEUTRAL, EventLevel.SAFE, "Walk"),
            GameEventBase(GameEventBase.EventType.POSITIVE, EventLevel.SAFE, "Found something!"),
            GameEventBase(GameEventBase.EventType.POSITIVE, EventLevel.SAFE, "Trader"),
            GameEventBase(GameEventBase.EventType.NEGATIVE, EventLevel.SAFE, "Fight!")
        )
        val randomIndex = (0 until activityList.size).random()
        val randomEvent = GameEventBase(
            activityList[randomIndex].eventType,
            activityList[randomIndex].eventLevel,
            activityList[randomIndex].eventText
        )

        // generate content for the event
        randomEvent.generateContent()

        return randomEvent
    }
}