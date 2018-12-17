
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
            packageInfo = manager.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
            signatures = packageInfo.signatures;
            for (Signature signature : signatures)
            {
               builder.append(signature.toCharsString());
            }
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
 
