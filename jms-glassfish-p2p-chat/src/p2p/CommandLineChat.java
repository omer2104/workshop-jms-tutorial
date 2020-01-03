package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CommandLineChat implements MessageListener {
	public static void main(String[] args) throws JMSException, NamingException, IOException {
//		if (args.length != 3) {
//			System.out.println("usage: username subscribe-to-queue-name publish-to-queue-name");
//		} else {
//		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("insert username:");
		String username = bufferedReader.readLine();
		System.out.println("insert queue to subscribe to:");
		String subQueueName = bufferedReader.readLine();
		System.out.println("insert queue to publish to:");
		String pubQueueName = bufferedReader.readLine();
		
		System.out.println("username: " + username + 
				" | Subscribe to queue: " + subQueueName +
				" | Publish to queue: " + pubQueueName);
		Context initialContext = CommandLineChat.getInitialContext();
		CommandLineChat commandLineChat = new CommandLineChat();
		
		Queue queue01 = (Queue) initialContext.lookup(subQueueName);
		Queue queue02 = (Queue) initialContext.lookup(pubQueueName);
		
		JMSContext jmsContext = ((ConnectionFactory) initialContext.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
		jmsContext.createConsumer(queue01).setMessageListener(commandLineChat);
		
		JMSProducer jmsProducer = jmsContext.createProducer();
		
		String messageToSend = null;
		while (true) {
			messageToSend = bufferedReader.readLine();
			
			if (messageToSend.equalsIgnoreCase("exit")) {
				jmsContext.close();
				System.exit(0);
			} else {
				jmsProducer.send(queue02, "[" + username + ": " + messageToSend + "]");
			}
		}
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			System.out.println(message.getBody(String.class));
		} catch (JMSException e) {
			e.printStackTrace();
		}
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
