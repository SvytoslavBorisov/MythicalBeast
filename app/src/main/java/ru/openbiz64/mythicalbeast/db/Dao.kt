package ru.openbiz64.mythicalbeast.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.openbiz64.mythicalbeast.dataclass.BeastDataClass

@Dao
interface Dao {
    //===========================================
    @Query("SELECT * FROM beast_list")
    fun getAllBeasts(): Flow<List<BeastDataClass>>



    @Query("SELECT * FROM beast_list WHERE isFavorite=1")
    fun getFavoriteBeasts(): Flow<List<BeastDataClass>>

    @Query("DELETE FROM beast_list WHERE id IS :id")
    suspend fun deleteBeast(id: Int): Int

    @Query("SELECT * FROM beast_list WHERE title LIKE '%' || :name ||'%' AND mythology LIKE '%' || :myth ||'%'")
    suspend fun findBeastByFilter(name: String = "", myth: String = ""): List<BeastDataClass>

    @Query("SELECT * FROM beast_list WHERE title LIKE '%' || :name ||'%'")
    suspend fun findBeastByName(name: String = ""): List<BeastDataClass>

    @Query("SELECT * FROM beast_list WHERE title LIKE '%' || :name ||'%' AND isFavorite=1")
    suspend fun findFavoriteBeastByName(name: String = ""): List<BeastDataClass>

    @Query("SELECT * FROM beast_list WHERE mythology LIKE '%' || :myth ||'%'")
    suspend fun findBeastByMythology(myth: String = ""): List<BeastDataClass>

    @Query("SELECT DISTINCT mythology FROM beast_list ORDER BY mythology ASC")
    suspend fun getMythologyList(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBeast(mineral: BeastDataClass)

    @Update
    suspend fun updateBeast(mineral: BeastDataClass)
}

