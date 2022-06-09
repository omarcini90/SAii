package mx.org.fimpes.saii.ejb.mail;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.exceptions.ValidationException;
import mx.org.fimpes.saii.model.mail.Mail;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class MailService {
	private static final Logger log = Logger.getLogger(MailService.class);
	private static final String CONFIG_FILE = "Mail.properties";
	private static Object lock = new Object();
	private Properties properties = new Properties();

	@EJB
	private MailFacade mailFacade;

	@SuppressWarnings("unused")
	private void loadConfigFile() {
		var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);
		synchronized (lock) {
			// when properties is static:
			try {
				log.info("loading configuration file... ");
				properties.load(new InputStreamReader(stream, "UTF-8"));
				stream.close();
				properties.list(System.out);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	@SuppressWarnings("unused")
	private void saveConfigFile() throws IOException, URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(CONFIG_FILE);
		synchronized (lock) {
			// when properties is static:
			log.info("saving configuration file... ");
			log.info("Path: " + url.getPath());
			var output = new FileOutputStream(url.getFile());
			properties.store(output, "");
			properties.list(System.out);
		}
	}

	@PostConstruct
	private void loadConfigFromDb() {
		log.info("loading mail configuration... ");
		var mailConfig = mailFacade.findAll();
		mailConfig.stream().forEach(p -> {
			properties.setProperty(p.getPropiedad(), p.getValor());
		});
		properties.list(System.out);
	}

	private void saveConfigToDb() {
		log.info("saving mail configuration... ");
		properties.entrySet().stream().forEach(e -> {
			var config = mailFacade.findByKey(e.getKey().toString());
			config.setValor(e.getValue().toString());
			mailFacade.edit(config);
		});
		properties.list(System.out);
	}

	public Map<String, String> getProperties() {
		Map<String, String> m = properties.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));

		return m;
	}

	public void updateProperties(Map<String, String> props) throws ValidationException {
		testConnection(props);
		props.forEach((k, v) -> {
			if (properties.containsKey(k)) {
				properties.replace(k, v);
			}
		});
		saveConfigToDb();
	}

	private void testConnection(Map<String, String> props) throws ValidationException {
		try {
			var session = getOffice365Session(props);
			var transport = session.getTransport();
			transport.connect();
			transport.close();
			log.info("Connection succeded!");
		} catch (MessagingException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	/*
	 * //session injection
	 * 
	 * @Resource(name = "java:jboss/mail/fimpes") private Session session;
	 */
	private Session getOffice365Session(Map<String, String> props) {
		var p = new Properties();
		p.put("mail.smtp.starttls.enable", true);
		p.put("mail.transport.protocol", "smtp");
		p.put("mail.smtp.auth", true);
		p.put("mail.smtp.host", properties.getProperty("host"));
		p.put("mail.smtp.port", properties.getProperty("port"));
		p.put("mail.smtp.socketFactory.port", properties.getProperty("port"));
		p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.put("mail.debug", true);
		// p.put("mail.smtp.auth.ntlm.domain", properties.getProperty("domain"));
		// p.put("mail.smtp.auth.mechanisms", "NTLM");
		// p.put("mail.smtp.ehlo", false);

		Authenticator auth;
		if (props != null && !props.isEmpty()) {
			p.replace("mail.smtp.host", props.get("host"));
			p.replace("mail.smtp.port", props.get("port"));
			p.replace("mail.smtp.socketFactory.port", props.get("port"));
			auth = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(props.get("user"), props.get("password"));
				}
			};
		} else {
			auth = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(properties.getProperty("user"),
							properties.getProperty("password"));
				}
			};
		}

		return Session.getInstance(p, auth);
	}

	public void send(String to, String subject, String message) throws Exception {
		var m = new Mail();
		m.setTo(to);
		m.setSubject(subject);
		m.setContent(message);
		send(m);
	}
	
	public void sendToFimpes(Mail message) throws Exception {
		message.setTo(properties.getProperty("fimpesusers"));
		send(message);
	}

	public void send(Mail message) throws Exception {
		try {
			var m = new MimeMessage(getOffice365Session(null));
			m.setFrom(new InternetAddress(properties.getProperty("user"), properties.getProperty("name")));

			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.getTo()));
			log.info("MailTo: " + message.getTo());

			if (message.getCc() != null && !message.getCc().isEmpty()) {
				// CC:
				m.setRecipients(Message.RecipientType.CC, InternetAddress.parse(message.getCc()));
				log.info("MailCC: " + message.getCc());
			}

			m.setSubject(message.getSubject());
			// Body text
			var bodyPart = new MimeBodyPart();
			bodyPart.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
			// bodyPart.setHeader("Content-Transfer-Encoding", "quoted-printable");
			bodyPart.setContent(message.getContent(), "text/html; charset=utf-8"); // m.setText(message);
			// MimeMultipart handles body parts
			var multipart = new MimeMultipart();
			multipart.addBodyPart(bodyPart);

			if (message.getPdf() != null) {
				// Attachment
				var attachPart = new MimeBodyPart();
				ByteArrayDataSource ds = new ByteArrayDataSource(new ByteArrayInputStream(message.getPdf()),
						"application/pdf");
				attachPart.setDataHandler(new DataHandler(ds));
				attachPart.setFileName(message.getSubject().split(":")[0].concat(".pdf"));
				multipart.addBodyPart(attachPart);
			}

			// Put all message parts in the message:
			m.setContent(multipart);
			Transport.send(m);
			log.info("Mails sent!");
		} catch (MessagingException | SecurityException | IOException e) {
			log.error("Couldn't send mail. ", e);
			throw e;
		}
	}
}
