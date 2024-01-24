package com.google.codelabs.buildyourfirstmap.classes

import org.bson.Document
import java.io.Serializable

open class GameItem(val name: String, val description: String, val dangerLevel: EventLevel) : Serializable{
    open fun toDocument(): Document {
        return Document("name", name)
            .append("description", description)
            .append("dangerLevel", dangerLevel.name)
            .append("type", this.javaClass.simpleName)
    }
}

class GameItemHeal(name: String, description: String, dangerLevel: EventLevel, val diceList: List<Dice>) : GameItem(name, description, dangerLevel) {
    fun heal(): Int {
        var totalHeal = 0
        for (dice in diceList) {
            totalHeal += dice.roll()
        }
        return totalHeal
    }

    override fun toDocument(): Document {
        return super.toDocument().append("diceList", diceList.map { it.toDocument() })
    }

}

class GameItemArmor(name: String, description: String, dangerLevel: EventLevel, val defense: Int) : GameItem(name, description, dangerLevel) {
    fun getArmorDefense(): Int {
        return defense
    }

    override fun toDocument(): Document {
        return super.toDocument().append("defense", defense)
    }
}

class GameItemWeapon(name: String, description: String, dangerLevel: EventLevel, val damage: List<Dice>) : GameItem(name, description, dangerLevel){
    fun getWeaponDamage(): List<Dice> {
        return damage
    }

    override fun toDocument(): Document {
        return super.toDocument().append("damage", damage.map { it.toDocument() })
    }
}
