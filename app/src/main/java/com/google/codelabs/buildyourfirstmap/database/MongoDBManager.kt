package com.google.codelabs.buildyourfirstmap.database

import com.google.codelabs.buildyourfirstmap.classes.Dice
import com.google.codelabs.buildyourfirstmap.classes.EventLevel
import com.google.codelabs.buildyourfirstmap.classes.GameItem
import com.google.codelabs.buildyourfirstmap.classes.GameItemArmor
import com.google.codelabs.buildyourfirstmap.classes.GameItemWeapon
import com.google.codelabs.buildyourfirstmap.classes.PlayerCharacter
import com.google.codelabs.buildyourfirstmap.classes.User
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

class MongoDBManager {
    private val DATABASE_NAME = "ArthunterDB"
    private val CONNECTION_STRING = "mongodb://192.168.1.2:27017"

    private val mongoClient: MongoClient
    private val database: MongoDatabase
    private val userCollection: MongoCollection<Document>
    private val playerCharacterCollection: MongoCollection<Document>

    init {
        val uri = MongoClientURI(CONNECTION_STRING)
        mongoClient = MongoClient(uri)
        database = mongoClient.getDatabase(DATABASE_NAME)
        userCollection = database.getCollection("Users")
        playerCharacterCollection = database.getCollection("PlayerCharacters")
    }

    fun addOrUpdateUser(user: User) {
        val userDocument = Document("login", user.login)
            .append("password", user.password)

        userCollection.createIndex(Document("login", 1), com.mongodb.client.model.IndexOptions().unique(true))

        userCollection.replaceOne(Document("login", user.login), userDocument, com.mongodb.client.model.ReplaceOptions().upsert(true))
    }

    fun getUserByLoginAndPassword(login: String, password: String): User? {
        val userDocument = userCollection.find(Document("login", login).append("password", password)).first()
        return if (userDocument != null) {
            User(userDocument.getString("login"), userDocument.getString("password"))
        } else {
            null
        }
    }

    fun addOrUpdatePlayerCharacter(playerCharacter: PlayerCharacter) {
        val playerCharacterDocument = Document("userLogin", playerCharacter.userLogin)
            .append("nickname", playerCharacter.nickname)
            .append("description", playerCharacter.description)
            .append("currentExperience", playerCharacter.currentExperience)
            .append("inventory", playerCharacter.inventory.map { item ->
                Document()
                    .append("name", item.name)
                    .append("description", item.description)
                    .append("dangerLevel", item.dangerLevel.name)
            })
            .append("armor", playerCharacter.armor?.let {
                Document()
                    .append("name", it.name)
                    .append("description", it.description)
                    .append("dangerLevel", it.dangerLevel.name)
                    .append("defense", it.defense)
            })
            .append("weapon", playerCharacter.weapon?.let { weapon ->
                Document()
                    .append("name", weapon.name)
                    .append("description", weapon.description)
                    .append("dangerLevel", weapon.dangerLevel.name)
                    .append("damage", weapon.damage.map { dice ->
                        Document().append("type", dice.type.name)
                    })
            })
            .append("skillPoints", playerCharacter.skillPoints)
            .append("strength", playerCharacter.strength)
            .append("agility", playerCharacter.agility)
            .append("constitution", playerCharacter.constitution)

        playerCharacterCollection.createIndex(
            Document("userLogin", 1),
            com.mongodb.client.model.IndexOptions().unique(true)
        )

        playerCharacterCollection.replaceOne(
            Document("userLogin", playerCharacter.userLogin),
            playerCharacterDocument,
            com.mongodb.client.model.ReplaceOptions().upsert(true)
        )
    }

    fun mapToEventLevel(level: String?): EventLevel? {
        return when (level) {
            EventLevel.SAFE.name -> EventLevel.SAFE
            EventLevel.NEUTRAL.name -> EventLevel.NEUTRAL
            EventLevel.DANGER.name -> EventLevel.DANGER
            EventLevel.HARDCORE.name -> EventLevel.HARDCORE
            else -> null
        }
    }

    fun getPlayerCharacterByUserLogin(userLogin: String): PlayerCharacter? {
        val playerCharacterDocument = playerCharacterCollection.find(Document("userLogin", userLogin)).first()
        return if (playerCharacterDocument != null) {
            PlayerCharacter(
                userLogin = playerCharacterDocument.getString("userLogin"),
                nickname = playerCharacterDocument.getString("nickname"),
                description = playerCharacterDocument.getString("description"),
                currentExperience = playerCharacterDocument.getInteger("currentExperience"),
                inventory = (playerCharacterDocument.get("inventory") as? List<Document>)?.map { itemDoc ->
                    when (EventLevel.valueOf(itemDoc.getString("dangerLevel"))) {
                        EventLevel.SAFE -> GameItem(
                            name = itemDoc.getString("name"),
                            description = itemDoc.getString("description"),
                            dangerLevel = EventLevel.SAFE
                        )
                        EventLevel.NEUTRAL -> GameItem(
                            name = itemDoc.getString("name"),
                            description = itemDoc.getString("description"),
                            dangerLevel = EventLevel.NEUTRAL
                        )
                        EventLevel.DANGER -> GameItem(
                            name = itemDoc.getString("name"),
                            description = itemDoc.getString("description"),
                            dangerLevel = EventLevel.DANGER,
                        )
                        EventLevel.HARDCORE -> GameItem(
                            name = itemDoc.getString("name"),
                            description = itemDoc.getString("description"),
                            dangerLevel = EventLevel.HARDCORE
                        )
                    }
                }?.toMutableList() ?: mutableListOf(),
                armor = playerCharacterDocument.get("armor")?.let { armorValue ->
                    val armorDoc = armorValue as Document
                    mapToEventLevel(armorDoc.getString("dangerLevel"))?.let {
                        GameItemArmor(
                            name = armorDoc.getString("name") ?: "",
                            description = armorDoc.getString("description") ?: "",
                            dangerLevel = it,
                            defense = armorDoc.getInteger("defense") ?: 0
                        )
                    }
                },
                weapon = playerCharacterDocument.get("weapon")?.let { weaponValue ->
                    val weaponDoc = weaponValue as Document
                    mapToEventLevel(weaponDoc.getString("dangerLevel"))?.let {
                        GameItemWeapon(
                            name = weaponDoc.getString("name") ?: "",
                            description = weaponDoc.getString("description") ?: "",
                            dangerLevel = it,
                            damage = (weaponDoc.get("damage") as? List<Document>)?.map { diceDoc ->
                                Dice(Dice.DiceType.valueOf(diceDoc.getString("type") ?: Dice.DiceType.D4.name))
                            } ?: emptyList()
                        )
                    }
                },
                skillPoints = playerCharacterDocument.getInteger("skillPoints"),
                strength = playerCharacterDocument.getInteger("strength"),
                agility = playerCharacterDocument.getInteger("agility"),
                constitution = playerCharacterDocument.getInteger("constitution")
            )
        } else {
            null
        }
    }

    fun closeConnection() {
        mongoClient.close()
    }
}