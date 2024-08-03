package com.minibox.minideveloper.Uitil;

import java.io.IOException;

public class JavaRc4 {
    public static void main(String[] strings) throws IOException {
        String content = "hallo world";
        String key = "ABCDEFGHIJKLNMOP";

        String ms = AESUtil.encrypt(content, key);
        String d  = AESUtil.decrypt(ms,key);
        System.out.println("密文"+ms);
        System.out.println("明文"+d);



    }

}
