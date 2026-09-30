package mx.riverstar.cultiva.mobile.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UsuarioEntity::class, MetaEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class CultivaDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun metaDao(): MetaDao

    companion object {
        fun crear(context: Context): CultivaDatabase =
            Room.databaseBuilder(context, CultivaDatabase::class.java, "cultiva.db").build()
    }
}
