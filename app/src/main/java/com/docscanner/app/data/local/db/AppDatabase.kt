package com.docscanner.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.docscanner.app.data.local.converter.Converters
import com.docscanner.app.data.local.dao.DocumentDao
import com.docscanner.app.data.local.dao.FolderDao
import com.docscanner.app.data.local.dao.PageDao
import com.docscanner.app.data.local.entity.DocumentEntity
import com.docscanner.app.data.local.entity.FolderEntity
import com.docscanner.app.data.local.entity.PageEntity

@Database(
    entities = [
        DocumentEntity::class,
        PageEntity::class,
        FolderEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun pageDao(): PageDao
    abstract fun folderDao(): FolderDao
}
