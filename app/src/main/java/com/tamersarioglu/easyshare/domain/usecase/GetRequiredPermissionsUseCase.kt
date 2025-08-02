package com.tamersarioglu.easyshare.domain.usecase

import com.tamersarioglu.easyshare.domain.repository.PermissionRepository
import javax.inject.Inject

class GetRequiredPermissionsUseCase @Inject constructor(
    private val repository: PermissionRepository
) {
    operator fun invoke(): List<String> {
        return repository.getRequiredPermissions()
    }
}