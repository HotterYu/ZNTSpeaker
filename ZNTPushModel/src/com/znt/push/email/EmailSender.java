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
		this.properties.put("mail.smtp.host",host);
		this.properties.put("mail.smtp.post",post);
		this.properties.put("mail.smtp.auth",true);
		this.session=Session.getInstance(properties);
		this.message = new MimeMessage(session);
		this.multipart = new MimeMultipart("mixed");
	}
	public void setReceiver(String[] receiver) throws MessagingException{
		Address[] address = new InternetAddress[receiver.length];
		for(int i=0;i<receiver.length;i++){
			address[i] = new InternetAddress(receiver[i]);
		}
		this.message.setRecipients(Message.RecipientType.TO, address);
	}
	public void setMessage(String from,String title,String content) throws AddressException, MessagingException{
		this.message.setFrom(new InternetAddress(from));
		this.message.setSubject(title);
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(content,"text/html;charset=gbk");
		this.multipart.addBodyPart(textBody);
	}
	public void addAttachment(String filePath) throws MessagingException{
		FileDataSource fileDataSource = new FileDataSource(new File(filePath));
		DataHandler dataHandler = new DataHandler(fileDataSource);
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setDataHandler(dataHandler);
		mimeBodyPart.setFileName(fileDataSource.getName());
		this.multipart.addBodyPart(mimeBodyPart);
	}
	public void sendEmail(String host,String account,String pwd) throws MessagingException{
		this.message.setSentDate(new Date());
		this.message.setContent(this.multipart);
		this.message.saveChanges();
		Transport transport=session.getTransport("smtp");
		transport.connect(host,account,pwd);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
}