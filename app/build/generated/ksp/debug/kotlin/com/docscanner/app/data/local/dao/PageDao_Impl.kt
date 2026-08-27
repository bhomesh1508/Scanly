package com.docscanner.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.docscanner.app.`data`.local.entity.PageEntity
import javax.`annotation`.processing.Generated
import kotlin.Float
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
public class PageDao_Impl(
  __db: RoomDatabase,
) : PageDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPageEntity: EntityInsertAdapter<PageEntity>

  private val __updateAdapterOfPageEntity: EntityDeleteOrUpdateAdapter<PageEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPageEntity = object : EntityInsertAdapter<PageEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `pages` (`id`,`documentId`,`pageNumber`,`originalImagePath`,`processedImagePath`,`thumbnailPath`,`width`,`height`,`rotation`,`filter`,`brightness`,`contrast`,`ocrText`,`ocrConfidence`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PageEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.documentId)
        statement.bindLong(3, entity.pageNumber.toLong())
        statement.bindText(4, entity.originalImagePath)
        statement.bindText(5, entity.processedImagePath)
        statement.bindText(6, entity.thumbnailPath)
        statement.bindLong(7, entity.width.toLong())
        statement.bindLong(8, entity.height.toLong())
        statement.bindLong(9, entity.rotation.toLong())
        statement.bindText(10, entity.filter)
        statement.bindDouble(11, entity.brightness.toDouble())
        statement.bindDouble(12, entity.contrast.toDouble())
        val _tmpOcrText: String? = entity.ocrText
        if (_tmpOcrText == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpOcrText)
        }
        val _tmpOcrConfidence: Float? = entity.ocrConfidence
        if (_tmpOcrConfidence == null) {
          statement.bindNull(14)
        } else {
          statement.bindDouble(14, _tmpOcrConfidence.toDouble())
        }
        statement.bindLong(15, entity.createdAt)
      }
    }
    this.__updateAdapterOfPageEntity = object : EntityDeleteOrUpdateAdapter<PageEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `pages` SET `id` = ?,`documentId` = ?,`pageNumber` = ?,`originalImagePath` = ?,`processedImagePath` = ?,`thumbnailPath` = ?,`width` = ?,`height` = ?,`rotation` = ?,`filter` = ?,`brightness` = ?,`contrast` = ?,`ocrText` = ?,`ocrConfidence` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: PageEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.documentId)
        statement.bindLong(3, entity.pageNumber.toLong())
        statement.bindText(4, entity.originalImagePath)
        statement.bindText(5, entity.processedImagePath)
        statement.bindText(6, entity.thumbnailPath)
        statement.bindLong(7, entity.width.toLong())
        statement.bindLong(8, entity.height.toLong())
        statement.bindLong(9, entity.rotation.toLong())
        statement.bindText(10, entity.filter)
        statement.bindDouble(11, entity.brightness.toDouble())
        statement.bindDouble(12, entity.contrast.toDouble())
        val _tmpOcrText: String? = entity.ocrText
        if (_tmpOcrText == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpOcrText)
        }
        val _tmpOcrConfidence: Float? = entity.ocrConfidence
        if (_tmpOcrConfidence == null) {
          statement.bindNull(14)
        } else {
          statement.bindDouble(14, _tmpOcrConfidence.toDouble())
        }
        statement.bindLong(15, entity.createdAt)
        statement.bindText(16, entity.id)
      }
    }
  }

  public override suspend fun insert(page: PageEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __insertAdapterOfPageEntity.insert(_connection, page)
  }

  public override suspend fun insertAll(pages: List<PageEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfPageEntity.insert(_connection, pages)
  }

  public override suspend fun update(page: PageEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __updateAdapterOfPageEntity.handle(_connection, page)
  }

  public override fun getPagesByDocument(documentId: String): Flow<List<PageEntity>> {
    val _sql: String = "SELECT * FROM pages WHERE documentId = ? ORDER BY pageNumber ASC"
    return createFlow(__db, false, arrayOf("pages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, documentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDocumentId: Int = getColumnIndexOrThrow(_stmt, "documentId")
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfOriginalImagePath: Int = getColumnIndexOrThrow(_stmt, "originalImagePath")
        val _columnIndexOfProcessedImagePath: Int = getColumnIndexOrThrow(_stmt,
            "processedImagePath")
        val _columnIndexOfThumbnailPath: Int = getColumnIndexOrThrow(_stmt, "thumbnailPath")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfRotation: Int = getColumnIndexOrThrow(_stmt, "rotation")
        val _columnIndexOfFilter: Int = getColumnIndexOrThrow(_stmt, "filter")
        val _columnIndexOfBrightness: Int = getColumnIndexOrThrow(_stmt, "brightness")
        val _columnIndexOfContrast: Int = getColumnIndexOrThrow(_stmt, "contrast")
        val _columnIndexOfOcrText: Int = getColumnIndexOrThrow(_stmt, "ocrText")
        val _columnIndexOfOcrConfidence: Int = getColumnIndexOrThrow(_stmt, "ocrConfidence")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<PageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PageEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDocumentId: String
          _tmpDocumentId = _stmt.getText(_columnIndexOfDocumentId)
          val _tmpPageNumber: Int
          _tmpPageNumber = _stmt.getLong(_columnIndexOfPageNumber).toInt()
          val _tmpOriginalImagePath: String
          _tmpOriginalImagePath = _stmt.getText(_columnIndexOfOriginalImagePath)
          val _tmpProcessedImagePath: String
          _tmpProcessedImagePath = _stmt.getText(_columnIndexOfProcessedImagePath)
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpWidth: Int
          _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          val _tmpHeight: Int
          _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          val _tmpRotation: Int
          _tmpRotation = _stmt.getLong(_columnIndexOfRotation).toInt()
          val _tmpFilter: String
          _tmpFilter = _stmt.getText(_columnIndexOfFilter)
          val _tmpBrightness: Float
          _tmpBrightness = _stmt.getDouble(_columnIndexOfBrightness).toFloat()
          val _tmpContrast: Float
          _tmpContrast = _stmt.getDouble(_columnIndexOfContrast).toFloat()
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpOcrConfidence: Float?
          if (_stmt.isNull(_columnIndexOfOcrConfidence)) {
            _tmpOcrConfidence = null
          } else {
            _tmpOcrConfidence = _stmt.getDouble(_columnIndexOfOcrConfidence).toFloat()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              PageEntity(_tmpId,_tmpDocumentId,_tmpPageNumber,_tmpOriginalImagePath,_tmpProcessedImagePath,_tmpThumbnailPath,_tmpWidth,_tmpHeight,_tmpRotation,_tmpFilter,_tmpBrightness,_tmpContrast,_tmpOcrText,_tmpOcrConfidence,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPagesForDocumentSync(documentId: String): List<PageEntity> {
    val _sql: String = "SELECT * FROM pages WHERE documentId = ? ORDER BY pageNumber ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, documentId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDocumentId: Int = getColumnIndexOrThrow(_stmt, "documentId")
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfOriginalImagePath: Int = getColumnIndexOrThrow(_stmt, "originalImagePath")
        val _columnIndexOfProcessedImagePath: Int = getColumnIndexOrThrow(_stmt,
            "processedImagePath")
        val _columnIndexOfThumbnailPath: Int = getColumnIndexOrThrow(_stmt, "thumbnailPath")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfRotation: Int = getColumnIndexOrThrow(_stmt, "rotation")
        val _columnIndexOfFilter: Int = getColumnIndexOrThrow(_stmt, "filter")
        val _columnIndexOfBrightness: Int = getColumnIndexOrThrow(_stmt, "brightness")
        val _columnIndexOfContrast: Int = getColumnIndexOrThrow(_stmt, "contrast")
        val _columnIndexOfOcrText: Int = getColumnIndexOrThrow(_stmt, "ocrText")
        val _columnIndexOfOcrConfidence: Int = getColumnIndexOrThrow(_stmt, "ocrConfidence")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<PageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PageEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDocumentId: String
          _tmpDocumentId = _stmt.getText(_columnIndexOfDocumentId)
          val _tmpPageNumber: Int
          _tmpPageNumber = _stmt.getLong(_columnIndexOfPageNumber).toInt()
          val _tmpOriginalImagePath: String
          _tmpOriginalImagePath = _stmt.getText(_columnIndexOfOriginalImagePath)
          val _tmpProcessedImagePath: String
          _tmpProcessedImagePath = _stmt.getText(_columnIndexOfProcessedImagePath)
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpWidth: Int
          _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          val _tmpHeight: Int
          _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          val _tmpRotation: Int
          _tmpRotation = _stmt.getLong(_columnIndexOfRotation).toInt()
          val _tmpFilter: String
          _tmpFilter = _stmt.getText(_columnIndexOfFilter)
          val _tmpBrightness: Float
          _tmpBrightness = _stmt.getDouble(_columnIndexOfBrightness).toFloat()
          val _tmpContrast: Float
          _tmpContrast = _stmt.getDouble(_columnIndexOfContrast).toFloat()
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpOcrConfidence: Float?
          if (_stmt.isNull(_columnIndexOfOcrConfidence)) {
            _tmpOcrConfidence = null
          } else {
            _tmpOcrConfidence = _stmt.getDouble(_columnIndexOfOcrConfidence).toFloat()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              PageEntity(_tmpId,_tmpDocumentId,_tmpPageNumber,_tmpOriginalImagePath,_tmpProcessedImagePath,_tmpThumbnailPath,_tmpWidth,_tmpHeight,_tmpRotation,_tmpFilter,_tmpBrightness,_tmpContrast,_tmpOcrText,_tmpOcrConfidence,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPageById(pageId: String): PageEntity? {
    val _sql: String = "SELECT * FROM pages WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pageId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDocumentId: Int = getColumnIndexOrThrow(_stmt, "documentId")
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfOriginalImagePath: Int = getColumnIndexOrThrow(_stmt, "originalImagePath")
        val _columnIndexOfProcessedImagePath: Int = getColumnIndexOrThrow(_stmt,
            "processedImagePath")
        val _columnIndexOfThumbnailPath: Int = getColumnIndexOrThrow(_stmt, "thumbnailPath")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfRotation: Int = getColumnIndexOrThrow(_stmt, "rotation")
        val _columnIndexOfFilter: Int = getColumnIndexOrThrow(_stmt, "filter")
        val _columnIndexOfBrightness: Int = getColumnIndexOrThrow(_stmt, "brightness")
        val _columnIndexOfContrast: Int = getColumnIndexOrThrow(_stmt, "contrast")
        val _columnIndexOfOcrText: Int = getColumnIndexOrThrow(_stmt, "ocrText")
        val _columnIndexOfOcrConfidence: Int = getColumnIndexOrThrow(_stmt, "ocrConfidence")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: PageEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDocumentId: String
          _tmpDocumentId = _stmt.getText(_columnIndexOfDocumentId)
          val _tmpPageNumber: Int
          _tmpPageNumber = _stmt.getLong(_columnIndexOfPageNumber).toInt()
          val _tmpOriginalImagePath: String
          _tmpOriginalImagePath = _stmt.getText(_columnIndexOfOriginalImagePath)
          val _tmpProcessedImagePath: String
          _tmpProcessedImagePath = _stmt.getText(_columnIndexOfProcessedImagePath)
          val _tmpThumbnailPath: String
          _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath)
          val _tmpWidth: Int
          _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          val _tmpHeight: Int
          _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          val _tmpRotation: Int
          _tmpRotation = _stmt.getLong(_columnIndexOfRotation).toInt()
          val _tmpFilter: String
          _tmpFilter = _stmt.getText(_columnIndexOfFilter)
          val _tmpBrightness: Float
          _tmpBrightness = _stmt.getDouble(_columnIndexOfBrightness).toFloat()
          val _tmpContrast: Float
          _tmpContrast = _stmt.getDouble(_columnIndexOfContrast).toFloat()
          val _tmpOcrText: String?
          if (_stmt.isNull(_columnIndexOfOcrText)) {
            _tmpOcrText = null
          } else {
            _tmpOcrText = _stmt.getText(_columnIndexOfOcrText)
          }
          val _tmpOcrConfidence: Float?
          if (_stmt.isNull(_columnIndexOfOcrConfidence)) {
            _tmpOcrConfidence = null
          } else {
            _tmpOcrConfidence = _stmt.getDouble(_columnIndexOfOcrConfidence).toFloat()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              PageEntity(_tmpId,_tmpDocumentId,_tmpPageNumber,_tmpOriginalImagePath,_tmpProcessedImagePath,_tmpThumbnailPath,_tmpWidth,_tmpHeight,_tmpRotation,_tmpFilter,_tmpBrightness,_tmpContrast,_tmpOcrText,_tmpOcrConfidence,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPageCount(documentId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM pages WHERE documentId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, documentId)
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

  public override suspend fun updateRotation(pageId: String, rotation: Int) {
    val _sql: String = "UPDATE pages SET rotation = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, rotation.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, pageId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateFilter(
    pageId: String,
    filter: String,
    brightness: Float,
    contrast: Float,
  ) {
    val _sql: String = "UPDATE pages SET filter = ?, brightness = ?, contrast = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, filter)
        _argIndex = 2
        _stmt.bindDouble(_argIndex, brightness.toDouble())
        _argIndex = 3
        _stmt.bindDouble(_argIndex, contrast.toDouble())
        _argIndex = 4
        _stmt.bindText(_argIndex, pageId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateOcrText(
    pageId: String,
    ocrText: String,
    confidence: Float,
  ) {
    val _sql: String = "UPDATE pages SET ocrText = ?, ocrConfidence = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ocrText)
        _argIndex = 2
        _stmt.bindDouble(_argIndex, confidence.toDouble())
        _argIndex = 3
        _stmt.bindText(_argIndex, pageId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updatePageNumber(pageId: String, pageNumber: Int) {
    val _sql: String = "UPDATE pages SET pageNumber = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pageNumber.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, pageId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(pageId: String) {
    val _sql: String = "DELETE FROM pages WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pageId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteByDocument(documentId: String) {
    val _sql: String = "DELETE FROM pages WHERE documentId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, documentId)
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
