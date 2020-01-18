import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Properties;

public class JavaMail {
    public static void main(String[] args) throws MessagingException, UnsupportedEncodingException {
        if (args.length != 3) {
            System.out.println("输入格式错误!");
            System.exit(-1);
        }
        String smtp = "smtp.qq.com";
        final String userName = args[0]; //发送邮箱
        final String password = args[1]; //发送邮箱通行码或者密码
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp);
        props.put("mail.smtp.port", "465");
        props.put("mail.transport.protocol", "stmp");
        props.put("mail.smtp.auth", "true");
//        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.ssl.enable", "true");
//        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

//        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userName));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(args[2])); //改为目标邮箱
        message.setSubject("Hello", "UTF-8");
        Multipart multipart = new MimeMultipart();
        BodyPart textPart = new MimeBodyPart();
        textPart.setContent("Hello, Java socket!", "text/plain;charset=utf-8");
        multipart.addBodyPart(textPart);
        BodyPart imagePart = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource("src\\main\\resources\\1.png"));
        imagePart.setDataHandler(dh);

        imagePart.setFileName(MimeUtility.encodeText(dh.getName()));
//        imagePart.setDataHandler(new DataHandler(new ByteArrayDataSource(input, "application/octet-stream")));
        multipart.addBodyPart(imagePart);
        message.setContent(multipart);

        Transport.send(message);
    }
}
