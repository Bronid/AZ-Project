package com.google.codelabs.buildyourfirstmap.classes

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
    var inventory: MutableList<GameItem>,
    var armor: GameItemArmor?,
    var weapon: GameItemWeapon?,
    var skillPoints: Int,
    var strength: Int, // модификатор урона
    var agility: Int, // повышает защиту
    var constitution: Int, // здоровье
) : Serializable {
    private var health = getMaxHealth()
    private var isKnocked = false
    private var level = LevelManager.calculateLevel(currentExperience)
    private var damage: Stack<Dice> = Stack()

    init {
        health = getMaxHealth()
        level = LevelManager.calculateLevel(currentExperience)
        updateDamage()
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

    fun isKnocked(): Boolean {
        return isKnocked
    }

    fun changeHealth(num: Int) {
        if (health + num <= 0){
            health = 0
            isKnocked = true
            return
        }
        else if(health + num > getMaxHealth()){
            health = getMaxHealth()
            return
        }
        else {
            health += num
        }
    }

    fun getCurrentHealth(): Int{
        return health
    }

    fun getMaxHealth(): Int {
        return ((5 + constitution) + (level * 2))
    }

    fun attack(): Int {
        var totalDamage = 0
        for (dice in damage) {
            totalDamage += dice.roll()
        }
        return totalDamage
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