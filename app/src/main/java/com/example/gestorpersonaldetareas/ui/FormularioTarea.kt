package com.example.gestorpersonaldetareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.data.local.Tarea
import com.example.gestorpersonaldetareas.model.Prioridad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTarea(
    tareaAEditar: Tarea? = null,
    onGuardar: (Tarea) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Variables de estado con rememberSaveable para sobrevivir a rotaciones de pantalla
    var titulo by rememberSaveable { mutableStateOf(tareaAEditar?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(tareaAEditar?.descripcion ?: "") }
    var prioridad by rememberSaveable { mutableStateOf(tareaAEditar?.prioridad ?: Prioridad.MEDIA) }
    var errorTitulo by rememberSaveable { mutableStateOf<String?>(null) }

    // Estados efímeros de interfaz
    var menuDesplegado by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título de cabecera
        Text(
            text = if (tareaAEditar == null) "Nueva tarea" else "Editar tarea",
            style = MaterialTheme.typography.headlineSmall
        )

        // Campo Título
        OutlinedTextField(
            value = titulo,
            onValueChange = {
                titulo = it
                if (errorTitulo != null) errorTitulo = null
            },
            label = { Text("Título") },
            isError = errorTitulo != null,
            supportingText = {
                if (errorTitulo != null) {
                    Text(text = errorTitulo!!)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción (opcional)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        // Selector de Prioridad (ExposedDropdownMenuBox)
        ExposedDropdownMenuBox(
            expanded = menuDesplegado,
            onExpandedChange = { menuDesplegado = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = prioridad.etiqueta,
                onValueChange = {},
                readOnly = true,
                label = { Text("Prioridad") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuDesplegado)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuDesplegado,
                onDismissRequest = { menuDesplegado = false }
            ) {
                Prioridad.entries.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion.etiqueta) },
                        onClick = {
                            prioridad = opcion
                            menuDesplegado = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Fila de botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onCancelar() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                enabled = !guardando,
                onClick = {
                    val tituloLimpio = titulo.trim()
                    if (tituloLimpio.length < 3) {
                        errorTitulo = "El título debe tener al menos 3 caracteres"
                        return@Button
                    }
                    if (prioridad !in Prioridad.entries) return@Button

                    guardando = true

                    val tareaFinal = if (tareaAEditar == null) {
                        Tarea(
                            titulo = tituloLimpio,
                            descripcion = descripcion.trim(),
                            prioridad = prioridad
                        )
                    } else {
                        tareaAEditar.copy(
                            titulo = tituloLimpio,
                            descripcion = descripcion.trim(),
                            prioridad = prioridad
                        )
                    }

                    onGuardar(tareaFinal)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}
