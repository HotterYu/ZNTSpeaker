package com.znt.push.email;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender 
{
	private Properties properties;
	private Session session;
	private Message message;
	private MimeMultipart multipart;

	public EmailSender() {
		super();
		this.properties = new Properties();
	}
	public void setProperties(String host,String post){
		//锟斤拷址
		this.properties.put("mail.smtp.host",host);
		//锟剿口猴�?
		this.properties.put("mail.smtp.post",post);
		//锟角凤拷锟斤拷证
		this.properties.put("mail.smtp.auth",true);
		this.session=Session.getInstance(properties);
		this.message = new MimeMessage(session);
		this.multipart = new MimeMultipart("mixed");
	}
	/**
	 * 锟斤拷锟斤拷锟秸硷拷锟斤�?
	 * @param receiver
	 * @throws MessagingException
	 */
	public void setReceiver(String[] receiver) throws MessagingException{
		Address[] address = new InternetAddress[receiver.length];
		for(int i=0;i<receiver.length;i++){
			address[i] = new InternetAddress(receiver[i]);
		}
		this.message.setRecipients(Message.RecipientType.TO, address);
	}
	/**
	 * 锟斤拷锟斤拷锟绞硷拷
	 * @param from 锟斤拷源
	 * @param title 锟斤拷锟斤拷
	 * @param content 锟斤拷锟斤拷
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setMessage(String from,String title,String content) throws AddressException, MessagingException{
		this.message.setFrom(new InternetAddress(from));
		this.message.setSubject(title);
		//锟斤拷锟侥憋拷锟侥伙拷锟斤拷setText()锟斤拷锟叫ｏ拷锟斤拷锟斤拷锟叫革拷锟斤拷锟斤拷锟斤拷示锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤�?
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(content,"text/html;charset=gbk");
		this.multipart.addBodyPart(textBody);
	}
	/**
	 * 锟斤拷痈锟斤拷锟�?
	 * @param filePath 锟侥硷拷路锟斤拷
	 * @throws MessagingException
	 */
	public void addAttachment(String filePath) throws MessagingException{
		FileDataSource fileDataSource = new FileDataSource(new File(filePath));
		DataHandler dataHandler = new DataHandler(fileDataSource);
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setDataHandler(dataHandler);
		mimeBodyPart.setFileName(fileDataSource.getName());
		this.multipart.addBodyPart(mimeBodyPart);
	}
	/**
	 * 锟斤拷锟斤拷锟绞硷拷
	 * @param host 锟斤拷址
	 * @param account 锟剿伙拷锟斤�?
	 * @param pwd 锟斤拷锟斤拷
	 * @throws MessagingException
	 */
	public void sendEmail(String host,String account,String pwd) throws MessagingException{
		//锟斤拷锟斤拷时锟斤拷
		this.message.setSentDate(new Date());
		//锟斤拷锟酵碉拷锟斤拷锟捷ｏ拷锟侥憋拷锟酵革拷锟斤�?
		this.message.setContent(this.multipart);
		this.message.saveChanges();
		//锟斤拷锟斤拷锟绞硷拷锟斤拷锟酵讹拷锟襟，诧拷指锟斤拷锟斤拷使锟斤拷SMTP协锟介发锟斤拷锟绞硷�?  
		Transport transport=session.getTransport("smtp");  
		//锟斤拷录锟斤拷锟斤拷  
		transport.connect(host,account,pwd);  
		//锟斤拷锟斤拷锟绞硷拷
		transport.sendMessage(message, message.getAllRecipients());
		//锟截憋拷锟斤拷锟斤拷
		transport.close();
	}
}