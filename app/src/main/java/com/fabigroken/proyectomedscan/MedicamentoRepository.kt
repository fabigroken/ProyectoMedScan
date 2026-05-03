package com.fabigroken.proyectomedscan

import com.google.firebase.firestore.FirebaseFirestore

class MedicamentoRepository {

    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Colección donde guardamos los medicamentos
    private val collection = db.collection("medicamentos")

    // Guarda un medicamento usando su ID como documento
    fun guardarMedicamento(
        medicamento: Medicamento,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        collection.document(medicamento.id)
            .set(medicamento)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // Guarda un medicamento solo si no existe otro con el mismo NOMBRE normalizado
    fun guardarSinDuplicar(
        medicamento: Medicamento,
        onSuccess: () -> Unit,
        onDuplicate: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Normalizamos el nombre para evitar duplicados
        val nombreNormalizado = normalizarNombre(medicamento.nombre)

        collection
            .get()
            .addOnSuccessListener { result ->

                // Revisamos si ya existe un medicamento con el mismo nombre normalizado
                val yaExiste = result.documents.any { doc ->
                    val existente = doc.toObject(Medicamento::class.java)
                    existente != null &&
                            normalizarNombre(existente.nombre)
                                .equals(nombreNormalizado, ignoreCase = true)
                }

                if (yaExiste) {
                    onDuplicate()
                    return@addOnSuccessListener
                }

                // Si no existe, lo guardamos normalmente
                collection.document(medicamento.id)
                    .set(medicamento)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it) }
            }
            .addOnFailureListener { onError(it) }
    }

    // Normaliza el NOMBRE para evitar duplicados
    private fun normalizarNombre(nombre: String): String {
        return nombre
            .lowercase()
            .replace(Regex("\\d+"), "") // elimina números (20mg, 10mg, etc.)
            .replace(Regex("[^a-zA-Záéíóúñ ]"), "") // elimina símbolos
            .replace(Regex("\\s+"), " ") // limpia espacios
            .trim()
    }

    // Normaliza el principio del nombre para evitar duplicados
    private fun normalizarPrincipio(principio: String): String {
        return principio
            .lowercase()
            .replace(Regex("\\d+\\s*mg"), "") // elimina dosis
            .replace(Regex("\\d+\\s*g"), "")  // elimina gramos
            .replace(Regex("\\s+"), " ")      // limpia espacios
            .trim()
    }

    // Obtiene la lista completa de medicamentos ordenados por fecha
    fun obtenerHistorial(
        onSuccess: (List<Medicamento>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        collection
            .orderBy("fechaEscaneo")
            .get()
            .addOnSuccessListener { result ->

                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Medicamento::class.java)
                }.reversed()

                onSuccess(lista)
            }
            .addOnFailureListener { onError(it) }
    }

    // Obtiene un medicamento por su ID
    fun obtenerMedicamentoPorId(
        id: String,
        onSuccess: (Medicamento?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        collection.document(id)
            .get()
            .addOnSuccessListener { doc ->
                onSuccess(doc.toObject(Medicamento::class.java))
            }
            .addOnFailureListener { onError(it) }
    }

    // Actualiza un medicamento completo
    fun actualizarMedicamento(
        medicamento: Medicamento,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        collection.document(medicamento.id)
            .set(medicamento)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // Elimina un medicamento por ID
    fun eliminarMedicamento(
        id: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        collection.document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
