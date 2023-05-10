package com.gip.fsa.utility;

public class SignatureUtility {

    private static MD5Utility md5Utility;
    private static ConstantUtility constantUtility;

    public static String doSignature(String _datetime, String _others) {
        md5Utility      = new MD5Utility();
        constantUtility = new ConstantUtility();

        String _signature = md5Utility.doMD5(md5Utility.doMD5(_datetime) + md5Utility.doMD5(_others) + md5Utility.doMD5(ConstantUtility.KEY_PIN));
        return _signature;
    }
}
