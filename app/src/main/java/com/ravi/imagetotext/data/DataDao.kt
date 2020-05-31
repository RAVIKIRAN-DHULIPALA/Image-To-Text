package com.ravi.imagetotext.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DataDao {

    @Insert
    fun insertData(user: User)

    @Query(value = "SELECT * from data_store ORDER BY modified_date DESC")
    fun getAllFiles(): LiveData<List<User>>

    @Query(value = "SELECT * FROM data_store where file_id = :id")
    fun getAFile(id:Long): User

    @Update
    fun update(user: User)

}