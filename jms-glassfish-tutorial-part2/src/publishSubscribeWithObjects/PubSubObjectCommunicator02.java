package publishSubscribeWithObjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sun.messaging.jms.Session;

public class PubSubObjectCommunicator02 implements MessageListener {
	public static final String TOPIC01 = "jms/Topic01";
	public static final String TOPIC02 = "jms/Topic02";
	
	public static void main(String[] args) throws JMSException, NamingException, IOException {
		PubSubObjectCommunicator02 communicator2 = new PubSubObjectCommunicator02();
		Context initialContext = PubSubObjectCommunicator02.getInitialContext();
		Topic topic01 = (Topic) initialContext.lookup(PubSubObjectCommunicator02.TOPIC01);
		Topic topic02 = (Topic) initialContext.lookup(PubSubObjectCommunicator02.TOPIC02);
		TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)initialContext.lookup("GFConnectionFactory");
		TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();
		topicConnection.start();
		communicator2.subscribe(topicConnection, topic01);
		communicator2.publish(topicConnection, topic02);
	}
	
	public void subscribe(TopicConnection topicConnection, Topic topic) throws JMSException {
		TopicSession subscribeSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		TopicSubscriber topicSubscriber = subscribeSession.createSubscriber(topic);
		topicSubscriber.setMessageListener(this);
	}
	public void publish(TopicConnection topicConnection, Topic topic) throws JMSException, IOException {
		TopicSession publishSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		TopicPublisher topicPublisher = publishSession.createPublisher(topic);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter your name:");
		String username = reader.readLine();
		String message = null;
		ObjectMessage objectMessage = null;
		while (true) {
			message = reader.readLine();
			if (message.equalsIgnoreCase("exit")) {
				topicConnection.close();
				System.exit(0);
			} else {
				objectMessage = publishSession.createObjectMessage();
				objectMessage.setObject(new CommunicationMessage(username, message));
				topicPublisher.publish(objectMessage);
			}
		}
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			ObjectMessage objectMessage = (ObjectMessage)message;
			CommunicationMessage communicationMessage = (CommunicationMessage) objectMessage.getObject();
			System.out.println("Sender:" + communicationMessage.getName());
			System.out.println(" | Message: " + communicationMessage.getMessage());
		} catch (JMSException e) {e.printStackTrace();}	
	}
	
	public static Context getInitialContext() throws JMSException, NamingException {
		Properties props = new Properties();
		
		props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url", "iiop://localhost:3700");
		
		Context context = new InitialContext(props);
		return context;
	}

}
