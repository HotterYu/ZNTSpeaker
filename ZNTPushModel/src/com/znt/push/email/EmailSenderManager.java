package com.znt.push.email; 

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class EmailSenderManager
{

	
	
	public void sendEmail(final String title, final String emailContent)
	{
        new Thread(new Runnable() 
        {
    		@Override
    		public void run()
    		{
    			try {
    				

    				
    				EmailSender sender = new EmailSender();
    				sender.setProperties("smtp.sina.com", "25");
    				sender.setMessage("yuyan19850204@sina.com", title, emailContent);
    				sender.setReceiver(new String[]{"yuyan@zhunit.com"});
    				sender.sendEmail("smtp.sina.com", "yuyan19850204@sina.com", "ZhuNiKeJi1818");
    				
    			} catch (AddressException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (Exception e) {
    				// TODO Auto-generated catch blockf
    				e.printStackTrace();
    			}
    		}
    	}).start();
	}
}
 
