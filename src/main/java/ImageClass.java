/**
*
* @author Saadet Elif Tokuoglu/ Lzzap S3curity
* @email saadet.elif@lzzapsecurity.com
*/
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;


public class ImageClass {

	 public void imageSetter(String base64Data, JLabel jLabelImage, JPanel CaptchaPanel,MontoyaApi api) throws IOException {
			ByteArray imageByte1 = api.utilities().base64Utils().decode(base64Data);// Montoya API base64 data decode
			byte[] imageByte = imageByte1.getBytes();
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
}
