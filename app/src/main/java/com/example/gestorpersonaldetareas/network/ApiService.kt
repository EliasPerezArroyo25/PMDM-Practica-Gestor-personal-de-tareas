package com.example.gestorpersonaldetareas.network

import retrofit2.http.GET

interface ApiService {
    @GET("todos")
    suspend fun obtenerTareasEjemplo(): List<TareaRemotaDto>
}