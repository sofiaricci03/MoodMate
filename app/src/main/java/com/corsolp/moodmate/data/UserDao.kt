package com.example.moodmate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moodmate.model.User

@Dao
interface UserDao {
    // Inserisce un nuovo utente. Se esiste già, lo sovrascrive.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Cerca un utente per email e password
    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    // Verifica se esiste già un'email
    @Query("SELECT * FROM user_table WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
}