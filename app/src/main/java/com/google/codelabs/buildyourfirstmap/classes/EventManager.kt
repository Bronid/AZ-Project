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
class EventManager(private val character: PlayerCharacter) {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val interval = 15L

    init {
        startEvents()
    }

    private fun startEvents() {
        val eventTask = Runnable {
            generateRandomEvent()
        }
        scheduler.scheduleAtFixedRate(eventTask, 0, interval, TimeUnit.SECONDS)
    }

    private fun generateRandomEvent() {
        val randomEvent = generateRandomGameEvent()
        println("Event: ${randomEvent.eventText}")

        when (randomEvent.eventType) {
            GameEventBase.EventType.POSITIVE -> handlePositiveEvent(randomEvent)
            GameEventBase.EventType.NEUTRAL -> handleNeutralEvent(randomEvent)
            GameEventBase.EventType.NEGATIVE -> handleNegativeEvent(randomEvent)
        }
    }

    private fun handlePositiveEvent(event: GameEventBase) {
        val item =

            event.item
        if (item is GameItemHeal) {
            println("You found a healing item: ${item.name}")
            character.inventory.add(item)
        }
        // Handle other positive events if needed
    }

    private fun handleNeutralEvent(event: GameEventBase) {
        // Implement logic for handling neutral events if needed
    }

    private fun handleNegativeEvent(event: GameEventBase) {
        val hostile = event.hostile
        if (hostile != null) {
            println("A wild ${hostile.name} appeared!")
            startBattle(hostile)
        }
        // Handle other negative events if needed
    }

    private fun startBattle(hostile: HostileCharacter) {
        println("Battle started! You vs ${hostile.name}")

        // Roll initiative for the player and hostile
        val playerInitiative = Dice(Dice.DiceType.D20).roll()
        val hostileInitiative = Dice(Dice.DiceType.D20).roll()

        println("Initiative - You: $playerInitiative, ${hostile.name}: $hostileInitiative")

        // Determine who goes first based on initiative
        var currentPlayerTurn = playerInitiative >= hostileInitiative

        while (!character.isKnocked() && hostile.health > 0) {
            if (currentPlayerTurn) {
                // Player's turn
                val playerDamage = character.attack()
                println("You dealt $playerDamage damage to ${hostile.name}")
                hostile.health -= playerDamage
            } else {
                // Hostile's turn
                val hostileDamage = hostile.attack()
                println("${hostile.name} dealt $hostileDamage damage to you")
                character.changeHealth(-hostileDamage)
            }

            // Toggle turn for the next round
            currentPlayerTurn = !currentPlayerTurn

            // Introduce a delay between each round of the battle
            TimeUnit.SECONDS.sleep(5L)

            // Print the current health after each round
            println("Your health: ${character.getCurrentHealth()}/${character.getMaxHealth()}, ${hostile.name}'s health: ${hostile.health}")
        }

        if (character.isKnocked()) {
            println("Game over! You were defeated by ${hostile.name}")
        } else {
            println("You defeated ${hostile.name}! Victory!")

            // Player gains experience
            character.currentExperience += hostile.exp
            println("You gained ${hostile.exp} experience!")

            // 50% chance to get a random item from the loot
            if (Random.nextBoolean() && hostile.loot.isNotEmpty()) {
                val randomLoot = hostile.loot.shuffled().first()
                character.inventory.add(randomLoot)
                println("You obtained ${randomLoot.name} from the loot!")
            }
        }
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