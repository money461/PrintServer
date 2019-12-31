package com.bondex.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码工具类
 */
@SuppressWarnings("restriction")
public class QRCodeUtils {
	private static final int width = 300;//二维码宽度(默认)
	private static final int height = 300;//二维码高度(默认)
	private static final String format = "png";//二维码文件格式
	private static final Map<EncodeHintType, Object> hints = new HashMap();//二维码参数

	static {
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//字符编码
		// 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//容错等级 H为最高
		hints.put(EncodeHintType.MARGIN, 2);//边距
	}

	/**
	 * 返回一个 BufferedImage 对象
	 * @param content 二维码内容
	 */
	public static BufferedImage toBufferedImage(String content) throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		return MatrixToImageWriter.toBufferedImage(bitMatrix);
	}

	/**
	 * 返回一个 BufferedImage 对象
	 * @param content 二维码内容
	 * @param width   宽
	 * @param height  高
	 */
	public static BufferedImage toBufferedImage(String content, int width, int height) throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		return MatrixToImageWriter.toBufferedImage(bitMatrix);
	}

	/**
	 * 将二维码图片输出到一个流中
	 * @param content 二维码内容
	 * @param stream  输出流
	 */
	public static void writeToStream(String content, OutputStream stream) throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
	}

	/**
	 * 将二维码图片输出到一个流中
	 * @param content 二维码内容
	 * @param stream  输出流
	 * @param width   宽
	 * @param height  高
	 */
	public static void writeToStream(String content, OutputStream stream, int width, int height) throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
	}

	/**
	 * 生成二维码图片
	 * @param content 二维码内容
	 * @param path    文件保存路径
	 */
	public static void createQRCode(String content, String path) throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToPath(bitMatrix, format, new File(path).toPath());
	}

	/**
	 * 生成二维码图片
	 * @param content 二维码内容
	 * @param path    文件保存路径
	 * @param width   宽
	 * @param height  高
	 */
	public static void createQRCode(String content, String path, int width, int height) throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToPath(bitMatrix, format, new File(path).toPath());
	}
	
	
	/**
	 * 生成带有logo的二维码图片
	 * @param content //内容
	 * @param qROutputStream //生成的二维码输出流
	 * @param logoin //logo输入流
	 * @param width 宽
	 * @param height 高
	 * @throws WriterException
	 * @throws IOException
	 */
	public static void createQRCodewithLogo(String content,OutputStream qROutputStream,InputStream logoin, int width, int height) throws WriterException, IOException {
		//BufferedImage bufferImage =ImageIO.read(image) ;
		 BufferedImage bufferedImage = toBufferedImage(content, width, height);
		// bufferedImage.setRGB(width, height, BufferedImage.TYPE_INT_RGB);
//		 Image image2 = ImageIO.read() ;
//         int width = image2.getWidth(null) ;
//         int height = image2.getHeight(null) ;
//         BufferedImage bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB) ;
         Graphics2D g2 = bufferedImage.createGraphics();
         g2.drawImage(bufferedImage, 0, 0, width, height, null) ;
         int matrixWidth = bufferedImage.getWidth();
         int matrixHeigh = bufferedImage.getHeight();
		
        //读取Logo图片
        BufferedImage logo= ImageIO.read(logoin);
        //开始绘制图片
        g2.drawImage(logo,matrixWidth/5*2,matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5, null);//绘制
        BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth/5*2, matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5,20,20);
        g2.setColor(Color.white);
        g2.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth/5*2+2, matrixHeigh/5*2+2, matrixWidth/5-4, matrixHeigh/5-4,20,20);
        g2.setColor(new Color(128,128,128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        
        bufferedImage.flush() ;
        
//        JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(qROutputStream) ;
//        en.encode(bufferedImage) ;
        ImageIO.write(bufferedImage, format, qROutputStream);
	}
	
	/**
	 * 生成带有Logo二维码图片
	 * @param content 二维码内容
	 * @param path    文件保存路径
	 * @param logoin  logo输入流
	 */
	public static void createQRCodewithLogo(String content, String path,InputStream logoin) {
		try {
			File qrFile  = new File(path);
			FileOutputStream  qROutputStream = new FileOutputStream(qrFile) ;
			createQRCodewithLogo(content, qROutputStream, logoin, width, height);
			qROutputStream.close();
			
		} catch (Exception e) {
		}
		
	}
	
	
	public static void main(String[] args) {
		try {
//		   String realUploadPath = request.getServletContext().getRealPath("/images");
//	        System.out.println("realUploadPath:"+realUploadPath);
			String path = QRCodeUtils.class.getClassLoader().getClass().getResource("/static/img").getPath();
			File logofile = new File(path,"logo.png");
			FileInputStream logoin = new FileInputStream(logofile);
			
			File uploadDir = new File(path);
			if(!uploadDir.exists()) {
				uploadDir.mkdirs();
			}
			
			String imageName = String.format("%s_qr.png", "bondex");
			File qrFile = new File(uploadDir, imageName);
			System.out.println("生成的二维码文件:"+qrFile.getAbsolutePath());
			String qrpath = qrFile.getPath();
			createQRCodewithLogo("123456789", qrpath, logoin);
			logoin.close();
			
		} catch (Exception e) {
			
		}finally {
			
		}

	}
}