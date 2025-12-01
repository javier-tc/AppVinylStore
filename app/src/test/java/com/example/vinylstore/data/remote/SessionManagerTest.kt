package com.example.vinylstore.data.remote

import android.content.Context
import android.content.SharedPreferences
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SessionManagerTest {
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var sessionManager: SessionManager
    
    @Before
    fun setup() {
        context = mockk<Context>(relaxed = true)
        sharedPreferences = mockk<SharedPreferences>(relaxed = true)
        editor = mockk<SharedPreferences.Editor>(relaxed = true)
        
        every { context.getSharedPreferences("VinylStorePrefs", Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.apply() } just Runs
        every { editor.clear() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        
        sessionManager = SessionManager(context)
    }
    
    @Test
    fun `saveToken guarda el token correctamente`() {
        every { sharedPreferences.getString("auth_token", null) } returns "test-token-123"
        
        sessionManager.saveToken("test-token-123")
        
        verify { editor.putString("auth_token", "test-token-123") }
        verify { editor.apply() }
        assertEquals("test-token-123", sessionManager.getToken())
    }
    
    @Test
    fun `getToken retorna null cuando no hay token guardado`() {
        every { sharedPreferences.getString("auth_token", null) } returns null
        
        assertNull(sessionManager.getToken())
        verify { sharedPreferences.getString("auth_token", null) }
    }
    
    @Test
    fun `saveUserInfo guarda la información del usuario correctamente`() {
        every { sharedPreferences.getInt("user_id", 0) } returns 1
        every { sharedPreferences.getString("user_email", null) } returns "test@example.com"
        every { sharedPreferences.getString("user_role", null) } returns "cliente"
        
        sessionManager.saveUserInfo(1, "test@example.com", "cliente")
        
        verify { editor.putInt("user_id", 1) }
        verify { editor.putString("user_email", "test@example.com") }
        verify { editor.putString("user_role", "cliente") }
        verify { editor.apply() }
        assertEquals(1, sessionManager.getUserId())
        assertEquals("test@example.com", sessionManager.getUserEmail())
        assertEquals("cliente", sessionManager.getUserRole())
    }
    
    @Test
    fun `getUserId retorna 0 cuando no hay usuario guardado`() {
        every { sharedPreferences.getInt("user_id", 0) } returns 0
        
        assertEquals(0, sessionManager.getUserId())
        verify { sharedPreferences.getInt("user_id", 0) }
    }
    
    @Test
    fun `getUserEmail retorna null cuando no hay email guardado`() {
        every { sharedPreferences.getString("user_email", null) } returns null
        
        assertNull(sessionManager.getUserEmail())
        verify { sharedPreferences.getString("user_email", null) }
    }
    
    @Test
    fun `getUserRole retorna null cuando no hay rol guardado`() {
        every { sharedPreferences.getString("user_role", null) } returns null
        
        assertNull(sessionManager.getUserRole())
        verify { sharedPreferences.getString("user_role", null) }
    }
    
    @Test
    fun `clearSession limpia toda la sesión`() {
        every { sharedPreferences.getString("auth_token", null) } returns null
        every { sharedPreferences.getInt("user_id", 0) } returns 0
        every { sharedPreferences.getString("user_email", null) } returns null
        every { sharedPreferences.getString("user_role", null) } returns null
        
        sessionManager.clearSession()
        
        verify { editor.clear() }
        verify { editor.apply() }
        assertNull(sessionManager.getToken())
        assertEquals(0, sessionManager.getUserId())
        assertNull(sessionManager.getUserEmail())
        assertNull(sessionManager.getUserRole())
    }
    
    @Test
    fun `isLoggedIn retorna true cuando hay token`() {
        every { sharedPreferences.getString("auth_token", null) } returns "test-token"
        
        assertTrue(sessionManager.isLoggedIn())
        verify { sharedPreferences.getString("auth_token", null) }
    }
    
    @Test
    fun `isLoggedIn retorna false cuando no hay token`() {
        every { sharedPreferences.getString("auth_token", null) } returns null
        
        assertFalse(sessionManager.isLoggedIn())
        verify { sharedPreferences.getString("auth_token", null) }
    }
    
    @Test
    fun `isLoggedIn retorna false después de clearSession`() {
        every { sharedPreferences.getString("auth_token", null) } returns null
        
        sessionManager.clearSession()
        
        assertFalse(sessionManager.isLoggedIn())
        verify { editor.clear() }
        verify { editor.apply() }
    }
}

