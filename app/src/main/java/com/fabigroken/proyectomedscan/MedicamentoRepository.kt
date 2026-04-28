package com.fabigroken.proyectomedscan

import com.google.firebase.firestore.FirebaseFirestore

class MedicamentoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("medicamentos")

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
