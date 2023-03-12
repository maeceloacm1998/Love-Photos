package com.br.photoscheme.teste.models

interface FirebaseResponse {
    fun success(result: Any?)
    fun error()
}