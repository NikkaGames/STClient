package ge.nikka.stclient

import android.util.Base64
import java.io.UnsupportedEncodingException

object Base64Utils {
    @JvmStatic
    fun Decrypt(encoded: String): String {
        var valueDecoded: ByteArray? = ByteArray(0)
        try {
            valueDecoded = Base64.decode(encoded.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        } catch (e: UnsupportedEncodingException) {
        }
        return String(valueDecoded!!)
    }

    fun Encrypt(decoded: String): String {
        var valueEncoded: ByteArray? = ByteArray(0)
        try {
            valueEncoded = Base64.encode(decoded.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        } catch (e: UnsupportedEncodingException) {
        }
        return String(valueEncoded!!)
    }
}
