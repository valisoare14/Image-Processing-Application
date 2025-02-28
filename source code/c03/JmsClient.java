package jmsclientservermi;

import jakarta.jms.Connection;
import jakarta.jms.Session;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.activemq.ActiveMQConnectionFactory;
public class JmsClient {
	public static void main(String[] args) throws IOException {
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
            MessageConsumer consumer = session.createConsumer(destination);
            System.out.println("NumberConsumer is waiting for a message on queue: " + queueName);
            while (true) { 
                Message receivedMessage = consumer.receive();
                if (receivedMessage != null && receivedMessage instanceof MapMessage) {
                	MapMessage mapMessage = (MapMessage) receivedMessage;

                    // Extract data from MapMessage
                    String zoomPercentage = mapMessage.getString("zoomPercentage");
                    String zoomOption = mapMessage.getString("zoomOption");
                    byte[] inputStreamData = mapMessage.getBytes("inputStream");

                    // Configure final image file destination
                    String uploadPath = "../"; // your desired path
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    String fileName = "zoomed_image_" + System.currentTimeMillis() + ".png";
                    File imageFile = new File(uploadDir, fileName);
                    
                    // RMI call & Save
                    BufferedImage finalImage = JmsClient.zoom(inputStreamData, Integer.parseInt(zoomPercentage), zoomOption);
                    if (finalImage != null) {
                        ImageIO.write(finalImage, "png", imageFile);
                    } else {
                    	throw new Exception("Failed to split image!");
                    }
                    
                    System.out.println("Consumer zoom successfully!");
                } else {
                    System.out.println("No numeric message received within the timeout.");
                }
            }

        } catch (Exception e) {
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
	
	public static BufferedImage zoom(byte[] image, int zoomPercentage, String zoomOption) {
	  try {
		    Registry registry1 = LocateRegistry.getRegistry("172.17.0.5", 1099);
	        RmiServerObjInterface remoteObject1 = (RmiServerObjInterface) registry1.lookup("rmi_server");
	        Registry registry2 = LocateRegistry.getRegistry("172.17.0.6", 1099);
	        RmiServerObjInterface remoteObject2 = (RmiServerObjInterface) registry2.lookup("rmi_server");

	        ByteArrayInputStream originalImageInputStream = new ByteArrayInputStream(image);
	        BufferedImage originalImage = ImageIO.read(originalImageInputStream);

	        if (zoomOption.equals("Zoom Out")) {
		        int subimages = 2;
		        int width = originalImage.getWidth() / subimages; 
		        int height = originalImage.getHeight();

		        List<byte[]> imageParts = new ArrayList<>();
		        for (int i = 0; i < subimages; i++) {
		            BufferedImage subImage = originalImage.getSubimage(i * width, 0, width, height);
		            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            ImageIO.write(subImage, "png", baos);
		            imageParts.add(baos.toByteArray());
		        }
		        List<byte[]> zoomedParts = new ArrayList<>();
		        for (byte[] part : imageParts) {
		        	if (imageParts.indexOf(part) == 0) {
			            zoomedParts.add(remoteObject1.zoom(part, zoomPercentage, zoomOption));
		        	} else if (imageParts.indexOf(part) == 1) {
		        		zoomedParts.add(remoteObject2.zoom(part, zoomPercentage, zoomOption));
		        	}
		        }

		        // Merge zoomed parts back into a single image
		        BufferedImage zoomedImage1 = ImageIO.read(new ByteArrayInputStream(zoomedParts.get(0)));
		        BufferedImage zoomedImage2 = ImageIO.read(new ByteArrayInputStream(zoomedParts.get(1)));

		        int mergedWidth = zoomedImage1.getWidth() + zoomedImage2.getWidth();
		        int mergedHeight = Math.max(zoomedImage1.getHeight(), zoomedImage2.getHeight());

		        BufferedImage mergedImage = new BufferedImage(mergedWidth, mergedHeight, BufferedImage.TYPE_INT_ARGB);
		        Graphics2D g2d = mergedImage.createGraphics();
		        g2d.drawImage(zoomedImage1, 0, 0, null);
		        g2d.drawImage(zoomedImage2, zoomedImage1.getWidth(), 0, null);
		        g2d.dispose();

		        return mergedImage;
	        } else {
	        	double zoomPercentageD = zoomPercentage / 100 + 1;
	        	double percentageSqrt = Math.sqrt(zoomPercentageD);
	        	byte[] intermediaryPart = remoteObject1.zoom(image, percentageSqrt, zoomOption);
	        	byte[] finalPart = remoteObject2.zoom(intermediaryPart, percentageSqrt, zoomOption);
	        	BufferedImage finalImage = ImageIO.read(new ByteArrayInputStream(finalPart));
	        	return finalImage;
	        }
      } catch (RemoteException exception) {
          System.out.println("Error in lookup: " + exception.toString());
      } catch (java.rmi.NotBoundException exception) {
          System.out.println("NotBound: " + exception.toString());
      } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 return null;
	}
}
