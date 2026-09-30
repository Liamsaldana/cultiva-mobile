package mx.riverstar.cultiva.mobile.dominio

/** Para qué es una meta. Se guarda en la base por su [name]. */
enum class Categoria(val etiqueta: String) {
    AHORRO("Ahorro"),
    DEUDA("Salir de una deuda"),
    EMERGENCIA("Fondo de emergencia"),
    COMPRA("Una compra"),
    EDUCACION("Educación"),
    OTRA("Otra");

    companion object {
        /** Lee una categoría guardada; si no la reconoce, devuelve [OTRA] en lugar de fallar. */
        fun desde(nombre: String): Categoria = entries.firstOrNull { it.name == nombre } ?: OTRA
    }
}
