package com.david.f1_manager.data

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConfig {
    private const val URL = "jdbc:postgresql://localhost:5432/f1_manager"
    private const val USER = "postgres"
    private const val PASSWORD = "Adminu_DB_1"

    fun getConnection(): Connection {
        return DriverManager.getConnection(URL, USER, PASSWORD)
    }
}