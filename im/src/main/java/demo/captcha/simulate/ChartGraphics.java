package demo.captcha.simulate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ChartGraphics {
	
	public static void main(String[] args) throws IOException {
		String[][] c = new com.google.gson.Gson().fromJson("[[\"0077\",\"请输入第3到第6位图像校验码\",\"http://moni.oss-cn-hangzhou.aliyuncs.com/yzm2/0077-36.jpg\"]]", String[][].class);
		Item item = new Item(c[0]);
	}
	
	BufferedImage image;
	
	byte[] create() throws IOException{
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		javax.imageio.ImageIO.write(this.image, "JPG", out);
		return out.toByteArray();
	}
	
	public byte[] graphicsGenerate(String value) throws IOException{
		
		int imageWidth = 190;// 图片的宽度
		int imageHeight = 29;// 图片的高度

		image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, imageWidth, imageHeight);
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font("宋体", Font.PLAIN, 17));
		graphics.drawString(value, 0, 20);
		graphics.dispose();

		return create();
	}
}
