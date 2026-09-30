package mx.riverstar.cultiva.mobile

import android.app.Application
import mx.riverstar.cultiva.mobile.data.db.CultivaDatabase
import mx.riverstar.cultiva.mobile.data.repo.AuthRepository
import mx.riverstar.cultiva.mobile.data.repo.MetaRepository
import mx.riverstar.cultiva.mobile.data.sesion.SesionRepository

/** Contenedor de dependencias manual: una sola base y un solo repositorio de cada cosa. */
class CultivaApp : Application() {
    val baseDatos by lazy { CultivaDatabase.crear(this) }
    val sesion by lazy { SesionRepository(this) }
    val auth by lazy { AuthRepository(baseDatos.usuarioDao(), sesion) }
    val metas by lazy { MetaRepository(baseDatos.metaDao()) }
}
