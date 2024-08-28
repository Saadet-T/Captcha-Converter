import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageClass {
//This is the class for rendering image 
		public void imageSetter(String base64Data, JLabel jLabelImage, JPanel CaptchaPanel) throws IOException {
			byte[] imageByte = org.apache.commons.codec.binary.Base64.decodeBase64(base64Data);//Get the Base64Data of image
			ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByte); //ImageIO need ByteArrayInputStream so we are converting the type
			BufferedImage bufImage = null;
			bufImage = ImageIO.read(inputStream);//Basically making the image
			ImageIcon icon = new ImageIcon(bufImage);//Basically making the image
			Image image = icon.getImage();//Basically making the image 
			Image newimg = image.getScaledInstance(292, 104, java.awt.Image.SCALE_SMOOTH);//Making image specific pixels because we don't want it to be too big
			icon = new ImageIcon(newimg);
			jLabelImage.setIcon(icon);//setting image on JLABEL
			jLabelImage.setHorizontalAlignment(JLabel.CENTER);//We put image in the center of JLABEL
			jLabelImage.setVerticalAlignment(JLabel.CENTER);//We put image in the center of JLABEL
			CaptchaPanel.add(jLabelImage);//Adding JLabel to JPanel
		}
		
		//I am adding this part because if there is a picture that I cannot render, I added it because I want to edit code
		public void errorSetter(JLabel jLabelError, JPanel CaptchaPanel) throws IOException {
			String Error = " Something wrong with magic bytes or image :( please contact @in/saadet-elif";//Adding text to JLabel 
			jLabelError.setFont(new Font("Serif", Font.PLAIN, 17));
			jLabelError.setOpaque(true);
			jLabelError.setBackground(Color.PINK);
			jLabelError.setText(Error);
			CaptchaPanel.add(jLabelError);

		}
		
		
	
}
