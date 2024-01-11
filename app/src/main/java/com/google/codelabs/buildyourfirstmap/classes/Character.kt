package com.google.codelabs.buildyourfirstmap.classes

data class Character(
    val nickname: String,
    val description: String,
    val registrationDate: String,
    val totalExperience: Long,
    val currentExperience: Long,
    val level: Int,
    val currentTask: Task?,
    val currentMoney: Int,
    val inventory: Inventory,
    val globalGoal: String,
    val health: Int,
    val defense: Int,
    val damage: Dice,
    val statistics: CharacterStatistics,
    val skills: List<Skill>
)

data class Task(val name: String, val description: String, val deadline: String)

data class Inventory(val items: List<Item>)

data class Item(val name: String, val description: String, val quantity: Int)

data class Dice(val numberOfDice: Int, val sides: Int)

data class CharacterStatistics(val monstersKilled: Int, val deaths: Int)

data class Skill(val name: String, val description: String)