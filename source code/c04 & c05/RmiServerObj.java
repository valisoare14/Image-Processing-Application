package jmsclientservermi;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.imageio.ImageIO;

public class RmiServerObj extends UnicastRemoteObject implements RmiServerObjInterface {
	
	protected RmiServerObj() throws RemoteException {
		super();
	}

	public byte[] zoom(byte[] portion, double zoomPercentage, String zoomOption) throws RemoteException {
		try {
            // Convert byte[] to BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(portion);
            BufferedImage originalPortion = ImageIO.read(bais);

            double newWidth;
            double newHeight;
            // Calculate new dimensions
            if (zoomOption.equals("Zoom Out")) {
                newWidth = originalPortion.getWidth() * 100 / zoomPercentage;
                newHeight = originalPortion.getHeight() * 100 / zoomPercentage;
            } else {
                newWidth = originalPortion.getWidth() / zoomPercentage;
                newHeight = originalPortion.getHeight() / zoomPercentage;
            }
            
            BufferedImage zoomedImage = null;
            if (zoomOption.equals("Zoom Out")) {
                zoomedImage = new BufferedImage((int)newWidth, (int)newHeight, originalPortion.getType());
                Graphics2D g2d = zoomedImage.createGraphics();
                g2d.drawImage(originalPortion.getScaledInstance((int)newWidth, (int)newHeight, Image.SCALE_SMOOTH), 0, 0, null);
                g2d.dispose();
            } else {
            	zoomedImage = originalPortion.getSubimage(
            			(int) (originalPortion.getWidth() / 2 - newWidth / 2),
            			(int) (originalPortion.getHeight() / 2 - newHeight / 2),
            			(int) newWidth,
            			(int) newHeight
            			);
            }
            
            // Convert zoomed BufferedImage back to byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(zoomedImage, "png", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error while zooming image portion: " + e.getMessage(), e);
        }
	}
}