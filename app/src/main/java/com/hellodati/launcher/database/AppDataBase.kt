package com.hellodati.launcher.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hellodati.launcher.dao.BasketDao
import com.hellodati.launcher.model.Basket


@Database(entities = [Basket::class], version = 5, exportSchema = false)
abstract class AppDataBase : RoomDatabase(){

    private var instance: AppDataBase? = null
    abstract fun basketDao(): BasketDao
    fun getAppDatabase(context: Context): AppDataBase? {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "hellodati_db"
            )

                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
        return instance
    }

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDataBase::class.java,
                            "hellodati_db"
                        )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return instance
        }
    }
}




