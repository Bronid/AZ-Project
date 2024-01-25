package com.google.codelabs.buildyourfirstmap.classes

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.util.Stack

class LevelManager {
    companion object {
        private val experienceThresholds = mapOf(
            1 to 0,
            2 to 200,
            3 to 400,
            4 to 800,
            5 to 1600,
            6 to 3200,
            7 to 6400,
            8 to 12800,
            9 to 25600,
            10 to 51200
        )

        fun calculateLevel(currentExperience: Int): Int {
            var level = 1
            while (experienceThresholds[level + 1] != null && currentExperience >= experienceThresholds[level + 1]!!) {
                level++
            }
            return level
        }
    }
}

data class PlayerCharacter(
    var userLogin: String,
    var nickname: String,
    var description: String,
    var currentExperience: Int,
    var currentHealth: Int,
    var isKnocked: Boolean,
    var inventory: MutableList<GameItem>,
    var armor: GameItemArmor?,
    var weapon: GameItemWeapon?,
    var skillPoints: Int,
    var strength: Int, // модификатор урона
    var perception: Int, // повышает обзор
    var constitution: Int, // здоровье
) : Serializable {
    var level = LevelManager.calculateLevel(currentExperience)
    var damage: Stack<Dice> = Stack()

    init {
        level = LevelManager.calculateLevel(currentExperience)
        updateDamage()
    }

    fun getFov(): Float {
        return 17 - (perception.toFloat() * 0.2F)
    }

    fun updateDamage(){
        damage.clear()
        if (weapon == null){
            damage.push(Dice(Dice.DiceType.D4))
        }
        else {
            for (dice in weapon!!.getWeaponDamage()) {
                damage.push(dice)
            }
        }
    }

    fun updateLevel(){
        level = LevelManager.calculateLevel(currentExperience)
    }

    fun changeHealth(num: Int) {
        if (currentHealth + num <= 0){
            currentHealth = 0
            isKnocked = true
            inventory.clear()
            return
        }
        else if(currentHealth + num > getMaxHealth()){
            currentHealth = getMaxHealth()
            return
        }
        else {
            currentHealth += num
        }
    }

    fun getMaxHealth(): Int {
        return ((5 + constitution) + (level * 2))
    }

    fun attack(): Int {
        var totalDamage = 0
        for (dice in damage) {
            totalDamage += dice.roll()
        }
        return totalDamage + strength
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun damageToString(): String {
        val diceCountMap = mutableMapOf<Dice.DiceType, Int>()

        // Подсчет количества каждого типа кубика
        damage.forEach { dice ->
            diceCountMap[dice.type] = diceCountMap.getOrDefault(dice.type, 0) + 1
        }

        // Формирование строки на основе подсчета
        val resultStringBuilder = StringBuilder()

        diceCountMap.forEach { (diceType, count) ->
            if (resultStringBuilder.isNotEmpty()) {
                resultStringBuilder.append("+")
            }
            resultStringBuilder.append("$count${diceType.toString()}")
        }

        return resultStringBuilder.toString()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("User Login: ${userLogin ?: "null"}\n")
        stringBuilder.append("Nickname: ${nickname ?: "null"}\n")
        stringBuilder.append("Description: ${description ?: "null"}\n")
        stringBuilder.append("Current Experience: ${currentExperience}\n")
        stringBuilder.append("Armor: ${armor ?: "null"}\n")
        stringBuilder.append("Weapon: ${weapon ?: "null"}\n")
        stringBuilder.append("Skill Points: ${skillPoints}\n")
        stringBuilder.append("Strength: ${strength}\n")
        stringBuilder.append("Perception: ${perception}\n")
        stringBuilder.append("Constitution: ${constitution}\n")
        stringBuilder.append("Health: ${currentHealth}\n")
        stringBuilder.append("Is Knocked: ${isKnocked}\n")
        stringBuilder.append("Level: ${level}\n")
        stringBuilder.append("Damage: ${damage.map { it.roll() }}\n")

        return stringBuilder.toString()
    }
}

class HostileCharacter(
    val name: String,
    var health: Int,
    val appearanceText: String,
    val diceList: List<Dice>,
    val dangerLevel: EventLevel,
    val exp: Int,
    val loot: List<GameItem>
) {
    fun copy(): HostileCharacter {
        return HostileCharacter(name, health, appearanceText, diceList.map { it.copy() }, dangerLevel, exp, loot.toMutableList())
    }

    fun attack(): Int {
        var totalDamage = 0
        for (dice in diceList) {
            totalDamage += dice.roll()
        }
        return totalDamage
    }
}