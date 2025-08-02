package com.tamersarioglu.easyshare.domain.repository

interface PermissionRepository {
    fun getRequiredPermissions(): List<String>
    fun hasPermissions(permissions: List<String>): Boolean
    fun getPermissionsToRequest(): List<String>
}