package com.google.codelabs.buildyourfirstmap.database

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

    init {
        val uri = MongoClientURI(CONNECTION_STRING)
        mongoClient = MongoClient(uri)
        database = mongoClient.getDatabase(DATABASE_NAME)
        userCollection = database.getCollection("Users")
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

    fun closeConnection() {
        mongoClient.close()
    }
}