package com.hellodati.launcher.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "basket")
public class Basket(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "idDb") var idDb: String,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "currency") var currency: String,
    @ColumnInfo(name = "price") var price: Double,
    @ColumnInfo(name = "quantity") var quantity: Int,
    @ColumnInfo(name = "date") var date: String
){}