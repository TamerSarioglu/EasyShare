package com.tamersarioglu.easyshare.domain.model

sealed class UpdateState {
    data object Idle : UpdateState()
    data object Updating : UpdateState()
    data class Success(val message: String) : UpdateState()
    data class Error(val message: String) : UpdateState()
}