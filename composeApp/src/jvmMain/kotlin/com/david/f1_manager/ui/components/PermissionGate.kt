package com.david.f1_manager.ui.components

import androidx.compose.runtime.Composable
import com.david.f1_manager.UserRole

/**
 * Simple permission gate - shows content only if user is ADMIN
 * ADMIN: Full access to everything
 * VIEWER: Can only view home screen
 */
@Composable
fun RequiresAdmin(
    currentRole: UserRole,
    content: @Composable () -> Unit
) {
    if (currentRole.isAdmin) {
        content()
    }
}