package mx.riverstar.cultiva.mobile.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {
    @Insert
    suspend fun insertar(meta: MetaEntity): Long

    @Update
    suspend fun actualizar(meta: MetaEntity)

    @Delete
    suspend fun borrar(meta: MetaEntity)

    @Query("SELECT * FROM metas WHERE id = :id AND usuario_id = :usuarioId")
    suspend fun porId(id: Long, usuarioId: Long): MetaEntity?

    /**
     * Búsqueda en tiempo real. Room devuelve un Flow que se vuelve a emitir cada
     * vez que cambia la tabla, y ejecuta la consulta fuera del hilo principal.
     *
     * [patron] ya viene con los comodines de LIKE escapados (ver MetaRepository).
     * [categoria] null = todas las categorías.
     */
    @Query(
        """
        SELECT * FROM metas
        WHERE usuario_id = :usuarioId
          AND (:categoria IS NULL OR categoria = :categoria)
          AND (nombre LIKE '%' || :patron || '%' ESCAPE '\'
               OR nota LIKE '%' || :patron || '%' ESCAPE '\')
        ORDER BY actualizada_en DESC
        """,
    )
    fun buscar(usuarioId: Long, patron: String, categoria: String?): Flow<List<MetaEntity>>
}
