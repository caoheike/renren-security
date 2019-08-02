package io.renren.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendKeyImpl {
	
	private static Logger logger= LoggerFactory.getLogger(SendKeyImpl.class);
	
	public static SendKey sendKey = null;
	
	static{
		try {
//			Document doc = Jsoup.parse(new URL("http://blog.sina.com.cn/s/blog_1688effdf0102zlcy.html"),10000);
//			String text = doc.text();
//			if(text.contains("HoomSunSendKey-Yes")){
				sendKey = SendKey.INSTANCE;
//				sendKey.DD_btn(0);
				Thread.sleep(1000);
			/*	sendKey.DD_key(605, 1);
				sendKey.DD_key(605, 2);*/
//				Thread.sleep(1000);
				logger.warn("sendKey初始化成功");
//			}else{
//				logger.warn("sendKey初始化失败");
//			}
		} catch (Exception e) {
			logger.error("sendKey初始化失败",e);
		}
	}
	
	/**
	 * 摁tab键
	 * @throws InterruptedException 
	 */
	public static void sendTab() throws InterruptedException{
		Thread.sleep(500);
		sendKey.DD_key(300, 1);
		sendKey.DD_key(300, 2);
		Thread.sleep(500);
	}
	
	/**
	 * 
	 * @param str			
	 * @throws InterruptedException
	 */
	public static void sendStr(String str) throws InterruptedException{
		sendKey = SendKey.INSTANCE;
		System.out.println(sendKey);
		Thread.sleep(500);
		sendKey.DD_str(str); 
	}
	
	/**
	 * 先定位，再输入字符串。在(x,y)坐标位置输入str字符串
	 * @param x x坐标
	 * @param y y坐标
	 * @param str  要输入的字符串
	 * @throws InterruptedException
	 */
	public static void sendStr(int x, int y,String str) throws InterruptedException{
		Thread.sleep(500);
		sendKey.DD_mov(x, y);
		sendKey.DD_btn(1);
		sendKey.DD_btn(2);
		Thread.sleep(500);
		sendKey.DD_str(str); 
	}	
	
	/**
	 * 
	 * @param x   x坐标
	 * @param y	  y坐标
	 * @param waitTime 键与键之间的时间间隔
	 * @param str
	 * @throws InterruptedException 
	 */
	public static void sendStr(int x,int y,long waitTime,String str) throws InterruptedException{
		char[] array = str.toCharArray();
		for (char c : array) {
			Thread.sleep(waitTime);
			sendKey.DD_str(c+"");
		}
	}
	
	/**
	 * 
	 * @param waitTime 键与键之间的时间间隔
	 * @param str
	 * @throws InterruptedException 
	 */
	public static void sendStr(long waitTime,String str) throws InterruptedException{
		char[] array = str.toCharArray();
		for (char c : array) {
			Thread.sleep(waitTime);
			sendKey.DD_str(c+"");
		}
	}
	
	/**
	 * 绝对移动
	 * @param x
	 * @param y
	 * @return
	 */
	public static int move(int x,int y){
		return  sendKey.DD_mov(x, y);
	}
	
	/**
	 * 相对移动
	 * @param x
	 * @param y
	 * @return
	 */
	public static int moveR(int x,int y){
		return  sendKey.DD_movR(x, y);
	}
	
	/**
	 * 点击鼠标
	 * @param x
	 * @return
	 */
	public static int button(int btn){
		return  sendKey.DD_btn(btn);
	}
	
	/**
	 * 按键
	 * @param ddcode
	 * @param flag
	 * @return
	 */
	public static int key(int ddcode, int flag){
		return sendKey.DD_key(ddcode, flag);
	}
	
}
