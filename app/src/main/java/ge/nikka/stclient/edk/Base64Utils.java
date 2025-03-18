package ge.nikka.edk;

import android.util.Base64;
import java.io.UnsupportedEncodingException;

public class Base64Utils {
    public static String Decrypt(String encoded) {
        byte[] valueDecoded = new byte[0];
        try {
            valueDecoded = Base64.decode(encoded.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {

        }
        return new String(valueDecoded);
    }

    public static String Encrypt(String decoded) {
        byte[] valueEncoded = new byte[0];
        try {
            valueEncoded = Base64.encode(decoded.getBytes("UTF-8"), Base64.DEFAULT);

        } catch (UnsupportedEncodingException e){

        }
        return new String(valueEncoded);
    }
}
