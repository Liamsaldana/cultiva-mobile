package mx.riverstar.cultiva.mobile.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Una cuenta local. La contraseña no se guarda: sólo su hash y la sal. */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)],
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    @ColumnInfo(name = "hash_contrasena") val hashContrasena: String,
    val sal: String,
    @ColumnInfo(name = "creado_en") val creadoEn: Long,
)
