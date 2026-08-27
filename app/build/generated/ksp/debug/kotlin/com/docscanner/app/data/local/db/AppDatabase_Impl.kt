package com.docscanner.app.`data`.local.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.docscanner.app.`data`.local.dao.DocumentDao
import com.docscanner.app.`data`.local.dao.DocumentDao_Impl
import com.docscanner.app.`data`.local.dao.FolderDao
import com.docscanner.app.`data`.local.dao.FolderDao_Impl
import com.docscanner.app.`data`.local.dao.PageDao
import com.docscanner.app.`data`.local.dao.PageDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _documentDao: Lazy<DocumentDao> = lazy {
    DocumentDao_Impl(this)
  }

  private val _pageDao: Lazy<PageDao> = lazy {
    PageDao_Impl(this)
  }

  private val _folderDao: Lazy<FolderDao> = lazy {
    FolderDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "f620a2cb80bbce6d4ec2592e151d3e2c", "71ffa7e03339a542424d3e7d1755d906") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `documents` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `folderId` TEXT, `pageCount` INTEGER NOT NULL, `thumbnailPath` TEXT NOT NULL, `ocrText` TEXT, `cloudPdfUrl` TEXT, `syncStatus` TEXT NOT NULL, `isEncrypted` INTEGER NOT NULL, `isTrashed` INTEGER NOT NULL, `trashedAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_folderId` ON `documents` (`folderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_title` ON `documents` (`title`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_createdAt` ON `documents` (`createdAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_isTrashed` ON `documents` (`isTrashed`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_syncStatus` ON `documents` (`syncStatus`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `pages` (`id` TEXT NOT NULL, `documentId` TEXT NOT NULL, `pageNumber` INTEGER NOT NULL, `originalImagePath` TEXT NOT NULL, `processedImagePath` TEXT NOT NULL, `thumbnailPath` TEXT NOT NULL, `width` INTEGER NOT NULL, `height` INTEGER NOT NULL, `rotation` INTEGER NOT NULL, `filter` TEXT NOT NULL, `brightness` REAL NOT NULL, `contrast` REAL NOT NULL, `ocrText` TEXT, `ocrConfidence` REAL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`documentId`) REFERENCES `documents`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_pages_documentId` ON `pages` (`documentId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `folders` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `color` INTEGER NOT NULL, `documentCount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f620a2cb80bbce6d4ec2592e151d3e2c')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `documents`")
        connection.execSQL("DROP TABLE IF EXISTS `pages`")
        connection.execSQL("DROP TABLE IF EXISTS `folders`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsDocuments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDocuments.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("folderId", TableInfo.Column("folderId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("pageCount", TableInfo.Column("pageCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("thumbnailPath", TableInfo.Column("thumbnailPath", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("ocrText", TableInfo.Column("ocrText", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("cloudPdfUrl", TableInfo.Column("cloudPdfUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("syncStatus", TableInfo.Column("syncStatus", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("isEncrypted", TableInfo.Column("isEncrypted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("isTrashed", TableInfo.Column("isTrashed", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("trashedAt", TableInfo.Column("trashedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDocuments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDocuments: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDocuments.add(TableInfo.Index("index_documents_folderId", false, listOf("folderId"),
            listOf("ASC")))
        _indicesDocuments.add(TableInfo.Index("index_documents_title", false, listOf("title"),
            listOf("ASC")))
        _indicesDocuments.add(TableInfo.Index("index_documents_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        _indicesDocuments.add(TableInfo.Index("index_documents_isTrashed", false,
            listOf("isTrashed"), listOf("ASC")))
        _indicesDocuments.add(TableInfo.Index("index_documents_syncStatus", false,
            listOf("syncStatus"), listOf("ASC")))
        val _infoDocuments: TableInfo = TableInfo("documents", _columnsDocuments,
            _foreignKeysDocuments, _indicesDocuments)
        val _existingDocuments: TableInfo = read(connection, "documents")
        if (!_infoDocuments.equals(_existingDocuments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |documents(com.docscanner.app.data.local.entity.DocumentEntity).
              | Expected:
              |""".trimMargin() + _infoDocuments + """
              |
              | Found:
              |""".trimMargin() + _existingDocuments)
        }
        val _columnsPages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPages.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("documentId", TableInfo.Column("documentId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("pageNumber", TableInfo.Column("pageNumber", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("originalImagePath", TableInfo.Column("originalImagePath", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("processedImagePath", TableInfo.Column("processedImagePath", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("thumbnailPath", TableInfo.Column("thumbnailPath", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("width", TableInfo.Column("width", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("height", TableInfo.Column("height", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("rotation", TableInfo.Column("rotation", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("filter", TableInfo.Column("filter", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("brightness", TableInfo.Column("brightness", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("contrast", TableInfo.Column("contrast", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("ocrText", TableInfo.Column("ocrText", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("ocrConfidence", TableInfo.Column("ocrConfidence", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPages.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysPages.add(TableInfo.ForeignKey("documents", "CASCADE", "NO ACTION",
            listOf("documentId"), listOf("id")))
        val _indicesPages: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPages.add(TableInfo.Index("index_pages_documentId", false, listOf("documentId"),
            listOf("ASC")))
        val _infoPages: TableInfo = TableInfo("pages", _columnsPages, _foreignKeysPages,
            _indicesPages)
        val _existingPages: TableInfo = read(connection, "pages")
        if (!_infoPages.equals(_existingPages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |pages(com.docscanner.app.data.local.entity.PageEntity).
              | Expected:
              |""".trimMargin() + _infoPages + """
              |
              | Found:
              |""".trimMargin() + _existingPages)
        }
        val _columnsFolders: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFolders.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFolders.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFolders.put("color", TableInfo.Column("color", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFolders.put("documentCount", TableInfo.Column("documentCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFolders.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFolders: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFolders: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFolders: TableInfo = TableInfo("folders", _columnsFolders, _foreignKeysFolders,
            _indicesFolders)
        val _existingFolders: TableInfo = read(connection, "folders")
        if (!_infoFolders.equals(_existingFolders)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |folders(com.docscanner.app.data.local.entity.FolderEntity).
              | Expected:
              |""".trimMargin() + _infoFolders + """
              |
              | Found:
              |""".trimMargin() + _existingFolders)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "documents", "pages", "folders")
  }

  public override fun clearAllTables() {
    super.performClear(true, "documents", "pages", "folders")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(DocumentDao::class, DocumentDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PageDao::class, PageDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FolderDao::class, FolderDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun documentDao(): DocumentDao = _documentDao.value

  public override fun pageDao(): PageDao = _pageDao.value

  public override fun folderDao(): FolderDao = _folderDao.value
}
