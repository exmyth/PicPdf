/**
 * 版权所有@: 杭州沃朴物联科技有限公司
 * 创建时间: 2015年10月15日下午4:59:00
 * 注意：本内容仅限于杭州沃朴物联科技有限公司内部使用，禁止外泄以及用于其他的商业目的
 * CopyRight@: 2015 Hangzhou wopuwulian Technology Co.,Ltd.
 * All Rights Reserved.
 * Note:Just limited to use by wopuwulian Technology Co.,Ltd. Others are forbidden. 
 * Created on: 2015年10月15日下午4:59:00
 */
package com.exmyth.pic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author 姜建成
 * @version ：1.0 Version
 * description:
 * create time：2015年10月15日 
 * 抗菌水圆形标签(图片生成pdf)
 *
 */
public class BarPic2Pdf_OuLin {

	/**
	 * @authoer：jason
	 * @param args
	 * description:
	 * create time： 2015年10月15日
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			if( args.length <3) {
				System.out.println("使用方式: 命令  图片目录  PDF目录  图片总数");
				System.out.println("举例: BarPic2Pdf d:\\PicOuLin d:\\PdfOuLin 18000");
				return;
			}
			
			String picPath = args[0];
			String pdfPath = args[1];
			String picNum  = args[2];
			
			File saveDir = new File(pdfPath);
			if(!saveDir.exists()){
				saveDir.mkdirs();
			}
			
			//需要调整的坐标阈值
			float left = 65.3f;		//第一列的左边距
			float top = 641.4f;		//第一行的上边距
			int pageSize = 9;		//打印一张的标签数量
			int rowSize = 3;		//一张纸的标签行数
		 	int colSize = 3;		//一张纸的标签列数
			float step = 205.3f;		//第二列距第一列行的间隔(step值越大,left越大,越向右)
			float vStep = 280.7f;	//第二行距第一行的间隔(vStep值越大,top越小,越向下)
			
			
			BufferedReader br = new BufferedReader(new FileReader(new File(picPath,"BarCode2Pic.txt")));
			int pageCount = (Integer.parseInt(picNum))/pageSize;
			int num = (Integer.parseInt(picNum))%pageSize;
			if(num >0){
				pageCount = pageCount + 1;
			}
			for(int k = 1; k <= pageCount; k++) {
				Rectangle rect = new Rectangle(609,842);
				Document document = new Document(rect);
				String pdfFileName = String.format("%06d", k) + ".pdf";
				PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream( pdfPath + "\\" + pdfFileName));

				document.open();
				
				//底下增加标签ID
				PdfContentByte serialText = pdfWriter.getDirectContent();
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
//				BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
				serialText.setFontAndSize(bf, 6);
				serialText.beginText();
				for(int i = 1; i <= rowSize; i++) {
					for(int j = 1; j <=colSize; j++) {
						if(((k-1)*pageSize+(i-1)*colSize + j)>Integer.parseInt(picNum)){
							break;
						}
						Image png = Image.getInstance(picPath+"\\"+ ((k-1)*pageSize+(i-1)*colSize + j) + "_bar.png");
						float leftPos,toPos;
						leftPos = 0.0f;
						toPos = 0.0f;
						//--------------------行图标top调整(第三个开始需要调整偏差)-----------------------
						if( i == 1) {
							toPos = top;
						}
						if( i == 2) {
							toPos = top -vStep;
						}
						if( i == 3) {
							toPos = top -vStep * 2f + 0.4f;	
						}
						//--------------------列图标left调整(第三个开始需要调整偏差)-----------------------
						if( j == 1 ) {
							leftPos = left;
						}
						if( j == 2) {
							leftPos = left + step + 0.2f;
						}
						if( j == 3 ) {
							leftPos = left + step * 2 + 0.5f;
						}
						png.setAbsolutePosition(leftPos,toPos);//leftPos越大,越靠上;toPos越小,越靠左
						png.scalePercent(55);
						document.add(png);
						serialText.showTextAligned(PdfContentByte.ALIGN_CENTER, br.readLine(),leftPos + 34.4f, toPos-6.1f , 0);
					}
				}
				
				//一页PDF完成，保存文件
				serialText.endText();
				document.close();
				System.out.println(pdfFileName+"文件生成Ok");
			}
			br.close();
			Runtime.getRuntime().exec("cmd.exe /c start " + pdfPath);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
