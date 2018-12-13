
package com.znt.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @ClassName: AokTools
 * @Description: TODO
 * @author yan.yu
 * @date 2015-5-8 涓婂崿11:36:18
 */
public class ApkTools
{

    public static String getSignature(Context activity)
    {
        PackageInfo packageInfo;
        Signature[] signatures;

        PackageManager manager = activity.getPackageManager();
        StringBuilder builder = new StringBuilder();

        try
        {
            String pkgname = activity.getPackageName();
            /** 閫氳繃鍖呯鐞嗗櫒鑾峰緱鎸囧畾鍖呭悕鍖呭惈绛惧悕鐨勫寘淇℃伅 **/
            packageInfo = manager.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
            /******* 閫氳繃杩斿洖鐨勫寘淇℃伅鑾峰緱绛惧悕鏁扮粿 *******/
            signatures = packageInfo.signatures;
            /******* 寰幆閬嶅巻绛惧悕鏁扮粍鎷兼帴搴旂敤绛惧悿 *******/
            for (Signature signature : signatures)
            {
                builder.append(signature.toCharsString());
            }
            /************** 寰楀埌搴旂敤绛惧悿 **************/
            return builder.toString();
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 浠嶢PK涓鍙栫鍚�
     * @param file
     * @return
     * @throws IOException
     */
    public static List<String> getSignaturesFromApk(File file)
    {

        List<String> signatures=new ArrayList<String>();

        try {
            JarFile jarFile=new JarFile(file);
            JarEntry je=jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer=new byte[8192];
            Certificate[] certs= loadCertificates(jarFile, je, readBuffer);
            if(certs != null) {
                for(Certificate c: certs) {
                    String sig = toCharsString(c.getEncoded());
                    signatures.add(sig);
                }
            }
        } catch(Exception ex) {
        }
        return signatures;
    }

    /**
     * 鍔犺浇绛惧悕 
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is=jarFile.getInputStream(je);
            while(is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch(IOException e) {
        }
        return null;
    }



    /**
     * 灏嗙鍚嶈浆鎴愯浆鎴愬彲瑙佸瓧绗︿覆 
     * @param sigBytes
     * @return
     */
    private static String toCharsString(byte[] sigBytes) {
        byte[] sig=sigBytes;
        final int N=sig.length;
        final int N2=N * 2;
        char[] text=new char[N2];
        for(int j=0; j < N; j++) {
            byte v=sig[j];
            int d=(v >> 4) & 0xf;
            text[j * 2]=(char)(d >= 10 ? ('a' + d - 10) : ('0' + d));
            d=v & 0xf;
            text[j * 2 + 1]=(char)(d >= 10 ? ('a' + d - 10) : ('0' + d));
        }
        return new String(text);
    }

}
 
