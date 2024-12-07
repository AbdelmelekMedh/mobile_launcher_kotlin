package com.hellodati.launcher.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hellodati.launcher.model.Basket


@Dao
interface BasketDao {
    @Query("SELECT * FROM Basket")
    fun getAll(): List<Basket>

    @Query("SELECT * FROM Basket WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Basket>

/*    @Query("SELECT * FROM Basket WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Basket*/

    @Insert
    fun insert(basket: Basket)

    @Delete
    fun delete(basket: Basket)

    @Query("SELECT * FROM Basket WHERE idDb = :id")
    fun getBasketById(id: String): Basket?

    @Update
    fun update(basket: Basket)

    @Query("DELETE FROM Basket")
    fun deleteTable()
}