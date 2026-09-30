package mx.riverstar.cultiva.mobile.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Una meta financiera de un usuario. Relación 1:N con [UsuarioEntity]:
 * si se borra el usuario, se borran sus metas (CASCADE).
 * Los montos van en centavos enteros.
 */
@Entity(
    tableName = "metas",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("usuario_id")],
)
data class MetaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "usuario_id") val usuarioId: Long,
    val nombre: String,
    val categoria: String,
    @ColumnInfo(name = "monto_objetivo") val montoObjetivo: Long,
    @ColumnInfo(name = "monto_ahorrado") val montoAhorrado: Long,
    val nota: String,
    @ColumnInfo(name = "creada_en") val creadaEn: Long,
    @ColumnInfo(name = "actualizada_en") val actualizadaEn: Long,
)
