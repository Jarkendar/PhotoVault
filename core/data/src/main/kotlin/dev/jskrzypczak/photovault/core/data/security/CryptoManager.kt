package dev.jskrzypczak.photovault.core.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val KEY_ALIAS = "photovault_token_key"
private const val TRANSFORMATION = "AES/GCM/NoPadding"

/** GCM authentication tag length in bits. */
private const val GCM_TAG_LENGTH = 128

/** GCM IV length in bytes (fixed at 12 by the spec). */
private const val GCM_IV_LENGTH = 12

/**
 * Encrypts and decrypts strings using AES-GCM with a key stored in the Android Keystore.
 *
 * The key never leaves the Keystore; only the encrypted ciphertext (IV + ciphertext, Base64)
 * is written to DataStore. This is the modern replacement for the deprecated
 * `EncryptedSharedPreferences` / `EncryptedFile` from androidx.security-crypto.
 */
internal class CryptoManager {

    private val keyStore: KeyStore =
        KeyStore.getInstance(KEYSTORE_PROVIDER).also { it.load(null) }

    private fun getOrCreateKey(): SecretKey {
        val existing = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existing != null) return existing.secretKey
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build(),
                )
            }
            .generateKey()
    }

    /**
     * Encrypts [plaintext] and returns a Base64-encoded string containing the 12-byte GCM IV
     * followed by the ciphertext. The result is safe to store in DataStore.
     */
    fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv                                      // always 12 bytes for GCM
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(iv + ciphertext)
    }

    /**
     * Decrypts a Base64-encoded string previously produced by [encrypt].
     * Throws if the data was tampered with (GCM authentication failure).
     */
    fun decrypt(encoded: String): String {
        val combined = Base64.getDecoder().decode(encoded)
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val ciphertext = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
    }
}
