package com.docscanner.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.docscanner.app.`data`.local.entity.FolderEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FolderDao_Impl(
  __db: RoomDatabase,
) : FolderDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFolderEntity: EntityInsertAdapter<FolderEntity>

  private val __updateAdapterOfFolderEntity: EntityDeleteOrUpdateAdapter<FolderEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFolderEntity = object : EntityInsertAdapter<FolderEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `folders` (`id`,`name`,`color`,`documentCount`,`createdAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FolderEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.color)
        statement.bindLong(4, entity.documentCount.toLong())
        statement.bindLong(5, entity.createdAt)
      }
    }
    this.__updateAdapterOfFolderEntity = object : EntityDeleteOrUpdateAdapter<FolderEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `folders` SET `id` = ?,`name` = ?,`color` = ?,`documentCount` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FolderEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.color)
        statement.bindLong(4, entity.documentCount.toLong())
        statement.bindLong(5, entity.createdAt)
        statement.bindText(6, entity.id)
      }
    }
  }

  public override suspend fun insert(folder: FolderEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfFolderEntity.insert(_connection, folder)
  }

  public override suspend fun update(folder: FolderEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfFolderEntity.handle(_connection, folder)
  }

  public override fun getAllFolders(): Flow<List<FolderEntity>> {
    val _sql: String =
        "SELECT `folders`.`id` AS `id`, `folders`.`name` AS `name`, `folders`.`color` AS `color`, `folders`.`documentCount` AS `documentCount`, `folders`.`createdAt` AS `createdAt` FROM folders ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("folders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfName: Int = 1
        val _columnIndexOfColor: Int = 2
        val _columnIndexOfDocumentCount: Int = 3
        val _columnIndexOfCreatedAt: Int = 4
        val _result: MutableList<FolderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FolderEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpColor: Long
          _tmpColor = _stmt.getLong(_columnIndexOfColor)
          val _tmpDocumentCount: Int
          _tmpDocumentCount = _stmt.getLong(_columnIndexOfDocumentCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = FolderEntity(_tmpId,_tmpName,_tmpColor,_tmpDocumentCount,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFolderById(id: String): Flow<FolderEntity?> {
    val _sql: String = "SELECT * FROM folders WHERE id = ?"
    return createFlow(__db, false, arrayOf("folders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfDocumentCount: Int = getColumnIndexOrThrow(_stmt, "documentCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: FolderEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpColor: Long
          _tmpColor = _stmt.getLong(_columnIndexOfColor)
          val _tmpDocumentCount: Int
          _tmpDocumentCount = _stmt.getLong(_columnIndexOfDocumentCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result = FolderEntity(_tmpId,_tmpName,_tmpColor,_tmpDocumentCount,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDocumentCountForFolder(folderId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM documents WHERE folderId = ? AND isTrashed = 0"
    return createFlow(__db, false, arrayOf("documents")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, folderId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateName(id: String, newName: String) {
    val _sql: String = "UPDATE folders SET name = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, newName)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateColor(id: String, newColor: Long) {
    val _sql: String = "UPDATE folders SET color = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, newColor)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(folderId: String) {
    val _sql: String = "DELETE FROM folders WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, folderId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
