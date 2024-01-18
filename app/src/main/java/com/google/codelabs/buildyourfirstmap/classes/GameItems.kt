package com.google.codelabs.buildyourfirstmap.classes

import java.io.Serializable

open class GameItem(val name: String, val description: String, val dangerLevel: EventLevel) : Serializable

class GameItemHeal(name: String, description: String, dangerLevel: EventLevel, val diceList: List<Dice>) : GameItem(name, description, dangerLevel) {

    fun heal(): Int {
        var totalHeal = 0
        for (dice in diceList) {
            totalHeal += dice.roll()
        }
        return totalHeal
    }

}

class GameItemArmor(name: String, description: String, dangerLevel: EventLevel, val defense: Int) : GameItem(name, description, dangerLevel) {
    fun getArmorDefense(): Int {
        return defense
    }
}

class GameItemWeapon(name: String, description: String, dangerLevel: EventLevel, val damage: List<Dice>) : GameItem(name, description, dangerLevel){
    fun getWeaponDamage(): List<Dice> {
        return damage
    }
}
