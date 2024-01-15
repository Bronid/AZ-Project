package com.google.codelabs.buildyourfirstmap.classes

import java.io.Serializable

data class User(
    val login: String,
    val password: String
) : Serializable