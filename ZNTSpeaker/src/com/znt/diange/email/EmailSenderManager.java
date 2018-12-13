package com.znt.diange.email; 

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/** 
 * @ClassName: EmailSenderManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-4-20 涓嬪崍2:37:28  
 */
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
    				
    				/*EmailSender sender = new EmailSender();
    				//璁剧疆鏈嶅姟鍣ㄥ湴鍧�鍜岀鍙ｏ紝缃戜笂鎼滅殑鍒�
    				sender.setProperties("mail.neldtv.org", "25");
    				//鍒嗗埆璁剧疆鍙戜欢浜猴紝閭欢鏍囬鍜屾枃鏈唴瀹�
    				sender.setMessage("photocloud@neldtv.org", "搴偊寮傚父_" + DateUtils.getStringDate(), emailContent);
    				//璁剧疆鏀朵欢浜�
    				sender.setReceiver(new String[]{"yyu@neldtv.org"});
    				//娣诲姞闄勪欢
    				//杩欎釜闄勪欢鐨勮矾寰勬槸鎴戞墜鏈洪噷鐨勫晩锛岃鍙戜綘寰楁崲鎴愪綘鎵嬫満閲屾纭殑璺緞
//    				sender.addAttachment("/sdcard/DCIM/Camera/asd.jpg");
    				//鍙戦�侀偖浠�
    				sender.sendEmail("mail.neldtv.org", "photocloud@neldtv.org", "tbtad0918");*/
    				
    				EmailSender sender = new EmailSender();
    				//璁剧疆鏈嶅姟鍣ㄥ湴鍧�鍜岀鍙ｏ紝缃戜笂鎼滅殑鍒�
    				sender.setProperties("smtp.sina.com", "25");
    				//鍒嗗埆璁剧疆鍙戜欢浜猴紝閭欢鏍囬鍜屾枃鏈唴瀹�
    				sender.setMessage("yuyan19850204@sina.com", title, emailContent);
    				//璁剧疆鏀朵欢浜�
    				sender.setReceiver(new String[]{"yuyan@zhunit.com"});
    				//娣诲姞闄勪欢
    				//杩欎釜闄勪欢鐨勮矾寰勬槸鎴戞墜鏈洪噷鐨勫晩锛岃鍙戜綘寰楁崲鎴愪綘鎵嬫満閲屾纭殑璺緞
//    				sender.addAttachment("/sdcard/DCIM/Camera/asd.jpg");
    				//鍙戦�侀偖浠�
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
 
