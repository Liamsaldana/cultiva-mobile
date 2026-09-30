package mx.riverstar.cultiva.mobile.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mx.riverstar.cultiva.mobile.data.db.MetaDao
import mx.riverstar.cultiva.mobile.data.db.MetaEntity
import mx.riverstar.cultiva.mobile.dominio.Categoria

/** Lo que ven las pantallas: una meta con su categoría ya tipada. */
data class Meta(
    val id: Long,
    val nombre: String,
    val categoria: Categoria,
    val montoObjetivo: Long,
    val montoAhorrado: Long,
    val nota: String,
) {
    /** Avance entre 0 y 1. */
    val avance: Float
        get() = if (montoObjetivo <= 0) 0f else (montoAhorrado.toFloat() / montoObjetivo).coerceIn(0f, 1f)
}

data class DatosMeta(
    val nombre: String,
    val categoria: Categoria,
    val montoObjetivo: Long,
    val montoAhorrado: Long,
    val nota: String,
)

class MetaRepository(private val dao: MetaDao) {

    fun buscar(usuarioId: Long, consulta: String, categoria: Categoria?): Flow<List<Meta>> =
        dao.buscar(usuarioId, escaparLike(consulta.trim()), categoria?.name)
            .map { lista -> lista.map { it.aMeta() } }

    suspend fun obtener(id: Long, usuarioId: Long): Meta? = dao.porId(id, usuarioId)?.aMeta()

    suspend fun crear(usuarioId: Long, datos: DatosMeta): Long {
        val ahora = System.currentTimeMillis()
        return dao.insertar(
            MetaEntity(
                usuarioId = usuarioId,
                nombre = datos.nombre.trim(),
                categoria = datos.categoria.name,
                montoObjetivo = datos.montoObjetivo,
                montoAhorrado = datos.montoAhorrado,
                nota = datos.nota.trim(),
                creadaEn = ahora,
                actualizadaEn = ahora,
            ),
        )
    }

    /** Sólo actualiza si la meta existe y es del usuario. */
    suspend fun actualizar(id: Long, usuarioId: Long, datos: DatosMeta): Boolean {
        val actual = dao.porId(id, usuarioId) ?: return false
        dao.actualizar(
            actual.copy(
                nombre = datos.nombre.trim(),
                categoria = datos.categoria.name,
                montoObjetivo = datos.montoObjetivo,
                montoAhorrado = datos.montoAhorrado,
                nota = datos.nota.trim(),
                actualizadaEn = System.currentTimeMillis(),
            ),
        )
        return true
    }

    suspend fun borrar(id: Long, usuarioId: Long): Boolean {
        val actual = dao.porId(id, usuarioId) ?: return false
        dao.borrar(actual)
        return true
    }

    private fun MetaEntity.aMeta() = Meta(
        id = id,
        nombre = nombre,
        categoria = Categoria.desde(categoria),
        montoObjetivo = montoObjetivo,
        montoAhorrado = montoAhorrado,
        nota = nota,
    )

    companion object {
        /** Escapa % _ y \ para que la persona busque el texto literal, no un comodín. */
        fun escaparLike(texto: String): String =
            texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
    }
}
