package io.renren.common.utils;



public class SendKeys {
	
	/**
	 * 摁tab键
	 * @throws InterruptedException 
	 */
	public static void sendTab() throws InterruptedException{
		SendKeyImpl.sendTab();
	}
	
	/**
	 * 
	 * @param str
	 * @throws InterruptedException
	 */
	public static void sendStr(String str) throws InterruptedException{

		SendKeyImpl.sendStr(str);
	}
	
	/**
	 * 先定位，再输入字符串。在(x,y)坐标位置输入str字符串
	 * @param x x坐标
	 * @param y y坐标
	 * @param str  要输入的字符串
	 * @throws InterruptedException
	 */
	public static void sendStr(int x, int y,String str) throws InterruptedException{
		SendKeyImpl.sendStr(x, y, str);
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
		SendKeyImpl.sendStr(x, y, waitTime, str);
	}
	
	/**
	 * 
	 * @param waitTime 键与键之间的时间间隔
	 * @param str
	 * @throws InterruptedException 
	 */
	public static void sendStr(long waitTime,String str) throws InterruptedException{
		SendKeyImpl.sendStr(waitTime, str);
	}
	
	/**
	 * 绝对移动
	 * @param x
	 * @param y
	 * @return
	 */
	public static int move(int x,int y){
		return  SendKeyImpl.move(x, y);
	}
	
	/**
	 * 相对移动
	 * @param x
	 * @param y
	 * @return
	 */
	public static int moveR(int x,int y){
		return  SendKeyImpl.moveR(x, y);
	}
	
	/**
	 * 点击鼠标
	 * @param x
	 * @return
	 */
	public static int button(int btn){
		return  SendKeyImpl.button(btn);
	}
	
	/**
	 * 按键
	 * @param ddcode
	 * @param flag
	 * @return
	 */
	public static int key(int ddcode, int flag){
		return SendKeyImpl.key(ddcode, flag);
	}
	
	
	
}
