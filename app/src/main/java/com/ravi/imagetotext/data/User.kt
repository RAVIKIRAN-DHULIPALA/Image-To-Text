package com.ravi.imagetotext.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_store")
data class User(
   @PrimaryKey
   val file_id:Long,
   val file_name:String,
   val file_content:String,
   val file_ext:String,
   val create_date:String,
   val file_path:String,
   val is_saved:Boolean,
   val modified_date:String,
   @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
   val byteArray: ByteArray,
   val file_icon :Int
) {
   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as User

      if (file_id != other.file_id) return false
      if (file_name != other.file_name) return false
      if (file_content != other.file_content) return false
      if (file_ext != other.file_ext) return false
      if (create_date != other.create_date) return false
      if (file_path != other.file_path) return false
      if (is_saved != other.is_saved) return false
      if (modified_date != other.modified_date) return false
      if (!byteArray.contentEquals(other.byteArray)) return false
      if (file_icon != other.file_icon) return false

      return true
   }

   override fun hashCode(): Int {
      var result = file_id.hashCode()
      result = 31 * result + file_name.hashCode()
      result = 31 * result + file_content.hashCode()
      result = 31 * result + file_ext.hashCode()
      result = 31 * result + create_date.hashCode()
      result = 31 * result + file_path.hashCode()
      result = 31 * result + is_saved.hashCode()
      result = 31 * result + modified_date.hashCode()
      result = 31 * result + byteArray.contentHashCode()
      result = 31 * result + file_icon
      return result
   }
}