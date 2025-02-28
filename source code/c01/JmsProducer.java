package net.java.image;

import jakarta.jms.Connection;
import jakarta.jms.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.activemq.ActiveMQConnectionFactory;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.MessageProducer;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;

public class JmsProducer {
	public static void sendZoomProperties(String zoomPercentage, String zoomOption, 
			InputStream inputStream) throws IOException {
        String brokerUrl = "tcp://172.17.0.3:61616"; //localhost
        String queueName = "zoom-queue";

        Connection connection = null;
        Session session = null;

        try {
            ConnectionFactory connectionFactory = (ConnectionFactory) new ActiveMQConnectionFactory(brokerUrl);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            byte[] inputStreamData  = convertInputStreamToByteArray(inputStream);
            MapMessage message = session.createMapMessage();
            message.setString("zoomPercentage", zoomPercentage);
            message.setString("zoomOption", zoomOption);
            message.setBytes("inputStream", inputStreamData);
            System.out.println(message);
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
	}
    private static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] temp = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(temp)) != -1) {
                buffer.write(temp, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }
}
