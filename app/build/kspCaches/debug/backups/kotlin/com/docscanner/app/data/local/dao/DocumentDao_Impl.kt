package com.docscanner.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.docscanner.app.`data`.local.entity.DocumentEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class DocumentDao_Impl(
  __db: RoomDatabase,
) : DocumentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDocumentEntity: EntityInsertAdapter<DocumentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDocumentEntity = object : EntityInsertAdapter<DocumentEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `documents` (`id`,`title`,`folderId`,`pageCount`,`thumbnailPath`,`ocrText`,`cloudPdfUrl`,`syncStatus`,`isEncrypted`,`isTrashed`,`trashedAt`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DocumentEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        val _tmpFolderId: String? = entity.folderId
        if (_tmpFolderId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpFolderId)
        }
        statement.bindLong(4, entity.pageCount.toLong())
        statement.bindText(5, entity.thumbnailPath)
        val _tmpOcrText: String? = entity.ocrText
        if (_tmpOcrText == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpOcrText)
        }
        val _tmpCloudPdfUrl: String? = entity.cloudPdfUrl
        if (_tmpCloudPdfUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpCloudPdfUrl)
        }
        statement.bindText(8, entity.syncStatus)
        val _tmp: Int = if (entity.isEncrypted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        val _tmp_1: Int = if (entity.isTrashed) 1 else 0
        statement.bindLong(10, _tmp_1.toLong())
        val _tmpTrashedAt: Long? = entity.trashedAt
        if (_tmpTrashedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpTrashedAt)
        }
        statement.bindLong(12, entity.createdAt)
        statement.bindLong(13, entity.updatedAt)
      }
    }
  }

  public override suspend fun upsert(document: DocumentEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfDocumentEntity.insert(_connection, document)
  }

  public override fun getAllDocuments(): Flow<List<DocumentEntity>> {
    val _sql: String =
        "SELECT `documents`.`id` AS `id`, `documents`.`title` AS `title`, `documents`.`folderId` AS `folderId`, `documents`.`pageCount` AS `pageCount`, `documents`.`thumbnailPath` AS `thumbnailPath`, `documents`.`ocrText` AS `ocrText`, `documents`.`cloudPdfUrl` AS `cloudPdfUrl`, `documents`.`syncStatus` AS `syncStatus`, `documents`.`isEncrypted` AS `isEncrypted`, `documents`.`isTrashed` AS `isTrashed`, `documents`.`trashedAt` AS `trashedAt`, `documents`.`createdAt` AS `createdAt`, `documents`.`updatedAt` AS `updatedAt` FROM documents WHERE isTrashed = 0 ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("documents")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfTitle: Int = 1
        val _columnIndexOfFolderId: Int = 2
        val _columnIndexOfPageCount: Int = 3
        val _columnIndexOfThumbnailPath: Int = 4
        val _columnIndexOfOcrText: Int = 5
        val _columnIndexOfCloudPdfUrl: Int = 6
        val _columnIndexOfSyncStatus: Int = 7
        val _columnIndexOfIsEncrypted: Int = 8
        val _columnIndexOfIsTrashed: Int = 9
        val _columnIndexOfTrashedAt: Int = 10
        val _columnIndexOfCreatedAt: Int = 11
        val _columnIndexOfUpdatedAt: Int = 12
        val _result: MutableList<DocumentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DocumentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpCloudPdfUrl: String?
          if (_stmt.isNull(_columnIndexOfCloudPdfUrl)) {
            _tmpCloudPdfUrl = null
          } else {
            _tmpCloudPdfUrl = _stmt.getText(_columnIndexOfCloudPdfUrl)
          }
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsEncrypted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEncrypted).toInt()
          _tmpIsEncrypted = _tmp != 0
          val _tmpIsTrashed: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsTrashed).toInt()
          _tmpIsTrashed = _tmp_1 != 0
          val _tmpTrashedAt: Long?
          if (_stmt.isNull(_columnIndexOfTrashedAt)) {
            _tmpTrashedAt = null
          } else {
            _tmpTrashedAt = _stmt.getLong(_columnIndexOfTrashedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              DocumentEntity(_tmpId,_tmpTitle,_tmpFolderId,_tmpPageCount,_tmpThumbnailPath,_tmpOcrText,_tmpCloudPdfUrl,_tmpSyncStatus,_tmpIsEncrypted,_tmpIsTrashed,_tmpTrashedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDocumentById(id: String): Flow<DocumentEntity?> {
    val _sql: String = "SELECT * FROM documents WHERE id = ?"
    return createFlow(__db, false, arrayOf("documents")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFolderId: Int = getColumnIndexOrThrow(_stmt, "folderId")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfThumbnailPath: Int = getColumnIndexOrThrow(_stmt, "thumbnailPath")
        val _columnIndexOfOcrText: Int = getColumnIndexOrThrow(_stmt, "ocrText")
        val _columnIndexOfCloudPdfUrl: Int = getColumnIndexOrThrow(_stmt, "cloudPdfUrl")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfIsEncrypted: Int = getColumnIndexOrThrow(_stmt, "isEncrypted")
        val _columnIndexOfIsTrashed: Int = getColumnIndexOrThrow(_stmt, "isTrashed")
        val _columnIndexOfTrashedAt: Int = getColumnIndexOrThrow(_stmt, "trashedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: DocumentEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpCloudPdfUrl: String?
          if (_stmt.isNull(_columnIndexOfCloudPdfUrl)) {
            _tmpCloudPdfUrl = null
          } else {
            _tmpCloudPdfUrl = _stmt.getText(_columnIndexOfCloudPdfUrl)
          }
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsEncrypted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEncrypted).toInt()
          _tmpIsEncrypted = _tmp != 0
          val _tmpIsTrashed: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsTrashed).toInt()
          _tmpIsTrashed = _tmp_1 != 0
          val _tmpTrashedAt: Long?
          if (_stmt.isNull(_columnIndexOfTrashedAt)) {
            _tmpTrashedAt = null
          } else {
            _tmpTrashedAt = _stmt.getLong(_columnIndexOfTrashedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              DocumentEntity(_tmpId,_tmpTitle,_tmpFolderId,_tmpPageCount,_tmpThumbnailPath,_tmpOcrText,_tmpCloudPdfUrl,_tmpSyncStatus,_tmpIsEncrypted,_tmpIsTrashed,_tmpTrashedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDocumentByIdSync(id: String): DocumentEntity? {
    val _sql: String = "SELECT * FROM documents WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFolderId: Int = getColumnIndexOrThrow(_stmt, "folderId")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfThumbnailPath: Int = getColumnIndexOrThrow(_stmt, "thumbnailPath")
        val _columnIndexOfOcrText: Int = getColumnIndexOrThrow(_stmt, "ocrText")
        val _columnIndexOfCloudPdfUrl: Int = getColumnIndexOrThrow(_stmt, "cloudPdfUrl")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfIsEncrypted: Int = getColumnIndexOrThrow(_stmt, "isEncrypted")
        val _columnIndexOfIsTrashed: Int = getColumnIndexOrThrow(_stmt, "isTrashed")
        val _columnIndexOfTrashedAt: Int = getColumnIndexOrThrow(_stmt, "trashedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: DocumentEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpCloudPdfUrl: String?
          if (_stmt.isNull(_columnIndexOfCloudPdfUrl)) {
            _tmpCloudPdfUrl = null
          } else {
            _tmpCloudPdfUrl = _stmt.getText(_columnIndexOfCloudPdfUrl)
          }
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsEncrypted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEncrypted).toInt()
          _tmpIsEncrypted = _tmp != 0
          val _tmpIsTrashed: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsTrashed).toInt()
          _tmpIsTrashed = _tmp_1 != 0
          val _tmpTrashedAt: Long?
          if (_stmt.isNull(_columnIndexOfTrashedAt)) {
            _tmpTrashedAt = null
          } else {
            _tmpTrashedAt = _stmt.getLong(_columnIndexOfTrashedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              DocumentEntity(_tmpId,_tmpTitle,_tmpFolderId,_tmpPageCount,_tmpThumbnailPath,_tmpOcrText,_tmpCloudPdfUrl,_tmpSyncStatus,_tmpIsEncrypted,_tmpIsTrashed,_tmpTrashedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDocumentsByFolder(folderId: String): Flow<List<DocumentEntity>> {
    val _sql: String =
        "SELECT * FROM documents WHERE folderId = ? AND isTrashed = 0 ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("documents")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, folderId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFolderId: Int = getColumnIndexOrThrow(_stmt, "folderId")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfThumbnailPath: Int = getColumnIndexOrThrow(_stmt, "thumbnailPath")
        val _columnIndexOfOcrText: Int = getColumnIndexOrThrow(_stmt, "ocrText")
        val _columnIndexOfCloudPdfUrl: Int = getColumnIndexOrThrow(_stmt, "cloudPdfUrl")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfIsEncrypted: Int = getColumnIndexOrThrow(_stmt, "isEncrypted")
        val _columnIndexOfIsTrashed: Int = getColumnIndexOrThrow(_stmt, "isTrashed")
        val _columnIndexOfTrashedAt: Int = getColumnIndexOrThrow(_stmt, "trashedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<DocumentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DocumentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpCloudPdfUrl: String?
          if (_stmt.isNull(_columnIndexOfCloudPdfUrl)) {
            _tmpCloudPdfUrl = null
          } else {
            _tmpCloudPdfUrl = _stmt.getText(_columnIndexOfCloudPdfUrl)
          }
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsEncrypted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEncrypted).toInt()
          _tmpIsEncrypted = _tmp != 0
          val _tmpIsTrashed: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsTrashed).toInt()
          _tmpIsTrashed = _tmp_1 != 0
          val _tmpTrashedAt: Long?
          if (_stmt.isNull(_columnIndexOfTrashedAt)) {
            _tmpTrashedAt = null
          } else {
            _tmpTrashedAt = _stmt.getLong(_columnIndexOfTrashedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              DocumentEntity(_tmpId,_tmpTitle,_tmpFolderId,_tmpPageCount,_tmpThumbnailPath,_tmpOcrText,_tmpCloudPdfUrl,_tmpSyncStatus,_tmpIsEncrypted,_tmpIsTrashed,_tmpTrashedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchDocuments(query: String): Flow<List<DocumentEntity>> {
    val _sql: String =
        "SELECT * FROM documents WHERE isTrashed = 0 AND (title LIKE '%' || ? || '%' OR ocrText LIKE '%' || ? || '%') ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("documents")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFolderId: Int = getColumnIndexOrThrow(_stmt, "folderId")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfThumbnailPath: Int = getColumnIndexOrThrow(_stmt, "thumbnailPath")
        val _columnIndexOfOcrText: Int = getColumnIndexOrThrow(_stmt, "ocrText")
        val _columnIndexOfCloudPdfUrl: Int = getColumnIndexOrThrow(_stmt, "cloudPdfUrl")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfIsEncrypted: Int = getColumnIndexOrThrow(_stmt, "isEncrypted")
        val _columnIndexOfIsTrashed: Int = getColumnIndexOrThrow(_stmt, "isTrashed")
        val _columnIndexOfTrashedAt: Int = getColumnIndexOrThrow(_stmt, "trashedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<DocumentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DocumentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpCloudPdfUrl: String?
          if (_stmt.isNull(_columnIndexOfCloudPdfUrl)) {
            _tmpCloudPdfUrl = null
          } else {
            _tmpCloudPdfUrl = _stmt.getText(_columnIndexOfCloudPdfUrl)
          }
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsEncrypted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEncrypted).toInt()
          _tmpIsEncrypted = _tmp != 0
          val _tmpIsTrashed: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsTrashed).toInt()
          _tmpIsTrashed = _tmp_1 != 0
          val _tmpTrashedAt: Long?
          if (_stmt.isNull(_columnIndexOfTrashedAt)) {
            _tmpTrashedAt = null
          } else {
            _tmpTrashedAt = _stmt.getLong(_columnIndexOfTrashedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              DocumentEntity(_tmpId,_tmpTitle,_tmpFolderId,_tmpPageCount,_tmpThumbnailPath,_tmpOcrText,_tmpCloudPdfUrl,_tmpSyncStatus,_tmpIsEncrypted,_tmpIsTrashed,_tmpTrashedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTrashedDocuments(): Flow<List<DocumentEntity>> {
    val _sql: String =
        "SELECT `documents`.`id` AS `id`, `documents`.`title` AS `title`, `documents`.`folderId` AS `folderId`, `documents`.`pageCount` AS `pageCount`, `documents`.`thumbnailPath` AS `thumbnailPath`, `documents`.`ocrText` AS `ocrText`, `documents`.`cloudPdfUrl` AS `cloudPdfUrl`, `documents`.`syncStatus` AS `syncStatus`, `documents`.`isEncrypted` AS `isEncrypted`, `documents`.`isTrashed` AS `isTrashed`, `documents`.`trashedAt` AS `trashedAt`, `documents`.`createdAt` AS `createdAt`, `documents`.`updatedAt` AS `updatedAt` FROM documents WHERE isTrashed = 1 ORDER BY trashedAt DESC"
    return createFlow(__db, false, arrayOf("documents")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfTitle: Int = 1
        val _columnIndexOfFolderId: Int = 2
        val _columnIndexOfPageCount: Int = 3
        val _columnIndexOfThumbnailPath: Int = 4
        val _columnIndexOfOcrText: Int = 5
        val _columnIndexOfCloudPdfUrl: Int = 6
        val _columnIndexOfSyncStatus: Int = 7
        val _columnIndexOfIsEncrypted: Int = 8
        val _columnIndexOfIsTrashed: Int = 9
        val _columnIndexOfTrashedAt: Int = 10
        val _columnIndexOfCreatedAt: Int = 11
        val _columnIndexOfUpdatedAt: Int = 12
        val _result: MutableList<DocumentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DocumentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpCloudPdfUrl: String?
          if (_stmt.isNull(_columnIndexOfCloudPdfUrl)) {
            _tmpCloudPdfUrl = null
          } else {
            _tmpCloudPdfUrl = _stmt.getText(_columnIndexOfCloudPdfUrl)
          }
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsEncrypted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEncrypted).toInt()
          _tmpIsEncrypted = _tmp != 0
          val _tmpIsTrashed: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsTrashed).toInt()
          _tmpIsTrashed = _tmp_1 != 0
          val _tmpTrashedAt: Long?
          if (_stmt.isNull(_columnIndexOfTrashedAt)) {
            _tmpTrashedAt = null
          } else {
            _tmpTrashedAt = _stmt.getLong(_columnIndexOfTrashedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              DocumentEntity(_tmpId,_tmpTitle,_tmpFolderId,_tmpPageCount,_tmpThumbnailPath,_tmpOcrText,_tmpCloudPdfUrl,_tmpSyncStatus,_tmpIsEncrypted,_tmpIsTrashed,_tmpTrashedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUnsyncedDocuments(): List<DocumentEntity> {
    val _sql: String =
        "SELECT `documents`.`id` AS `id`, `documents`.`title` AS `title`, `documents`.`folderId` AS `folderId`, `documents`.`pageCount` AS `pageCount`, `documents`.`thumbnailPath` AS `thumbnailPath`, `documents`.`ocrText` AS `ocrText`, `documents`.`cloudPdfUrl` AS `cloudPdfUrl`, `documents`.`syncStatus` AS `syncStatus`, `documents`.`isEncrypted` AS `isEncrypted`, `documents`.`isTrashed` AS `isTrashed`, `documents`.`trashedAt` AS `trashedAt`, `documents`.`createdAt` AS `createdAt`, `documents`.`updatedAt` AS `updatedAt` FROM documents WHERE syncStatus != 'SYNCED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfTitle: Int = 1
        val _columnIndexOfFolderId: Int = 2
        val _columnIndexOfPageCount: Int = 3
        val _columnIndexOfThumbnailPath: Int = 4
        val _columnIndexOfOcrText: Int = 5
        val _columnIndexOfCloudPdfUrl: Int = 6
        val _columnIndexOfSyncStatus: Int = 7
        val _columnIndexOfIsEncrypted: Int = 8
        val _columnIndexOfIsTrashed: Int = 9
        val _columnIndexOfTrashedAt: Int = 10
        val _columnIndexOfCreatedAt: Int = 11
        val _columnIndexOfUpdatedAt: Int = 12
        val _result: MutableList<DocumentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DocumentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpCloudPdfUrl: String?
          if (_stmt.isNull(_columnIndexOfCloudPdfUrl)) {
            _tmpCloudPdfUrl = null
          } else {
            _tmpCloudPdfUrl = _stmt.getText(_columnIndexOfCloudPdfUrl)
          }
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpIsEncrypted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEncrypted).toInt()
          _tmpIsEncrypted = _tmp != 0
          val _tmpIsTrashed: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsTrashed).toInt()
          _tmpIsTrashed = _tmp_1 != 0
          val _tmpTrashedAt: Long?
          if (_stmt.isNull(_columnIndexOfTrashedAt)) {
            _tmpTrashedAt = null
          } else {
            _tmpTrashedAt = _stmt.getLong(_columnIndexOfTrashedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              DocumentEntity(_tmpId,_tmpTitle,_tmpFolderId,_tmpPageCount,_tmpThumbnailPath,_tmpOcrText,_tmpCloudPdfUrl,_tmpSyncStatus,_tmpIsEncrypted,_tmpIsTrashed,_tmpTrashedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDocumentCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM documents WHERE isTrashed = 0"
    return createFlow(__db, false, arrayOf("documents")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun updateTitle(docId: String, title: String) {
    val _sql: String = "UPDATE documents SET title = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, title)
        _argIndex = 2
        _stmt.bindText(_argIndex, docId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateFolder(docId: String, folderId: String?) {
    val _sql: String = "UPDATE documents SET folderId = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (folderId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, folderId)
        }
        _argIndex = 2
        _stmt.bindText(_argIndex, docId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateSyncStatus(docId: String, status: String) {
    val _sql: String = "UPDATE documents SET syncStatus = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindText(_argIndex, docId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun moveToTrash(docId: String, trashedAt: Long) {
    val _sql: String = "UPDATE documents SET isTrashed = 1, trashedAt = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, trashedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, docId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun restoreFromTrash(docId: String) {
    val _sql: String = "UPDATE documents SET isTrashed = 0, trashedAt = NULL WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, docId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(docId: String) {
    val _sql: String = "DELETE FROM documents WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, docId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeOldTrash(cutoff: Long) {
    val _sql: String = "DELETE FROM documents WHERE isTrashed = 1 AND trashedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cutoff)
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
