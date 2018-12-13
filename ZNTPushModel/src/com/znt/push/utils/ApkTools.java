
package com.znt.push.utils; 

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

/** 
 * @ClassName: AokTools 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-5-8 æ¶å©å´?11:36:18  
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
            /** é«æ°³ç¹éå¯î¸éåæ«é¾å³°ç·±é¸å§ç¾éå­æéå­æç»æ§æé¨å«å¯æ·âä¼ **/
            packageInfo = manager.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
            /******* é«æ°³ç¹æ©æ¿æ´é¨å«å¯æ·âä¼é¾å³°ç·±ç»æ§æéæ®ç²? *******/
            signatures = packageInfo.signatures;
            /******* å¯°îå¹é¬å¶å·»ç»æ§æéæ®ç²é·å¼å¸´æ´ææ¤ç»æ§æ? *******/
            for (Signature signature : signatures) 
            {
               builder.append(signature.toCharsString());
            }
            /************** å¯°æ¥åæ´ææ¤ç»æ§æ? **************/
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
     * æµ å¶¢PKæ¶î¡î°éæ «î·éï¿½
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
     * éçºæµç»æ§æ 
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
     * çåî·éå¶æµé´æ¯æµé´æ¬å½²çä½¸ç§ç»ï¸¿è¦ 
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
 
