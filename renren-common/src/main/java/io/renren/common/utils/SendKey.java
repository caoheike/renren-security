package io.renren.common.utils;



import com.sun.jna.Library;
import com.sun.jna.Native;


public interface SendKey  extends Library {
		
	SendKey INSTANCE = (SendKey)Native.loadLibrary("C://DD94687.64.dll", SendKey.class);


	   //64位JAVA调用*64.dll, 32位调用*32.dll 。与系统本身位数无关。
	   /**
	    * DD_mov(参数x,参数y)
		* 功能： 模拟鼠标结对移动
		* 参数： 参数x , 参数y 以屏幕左上角为原点。
	    * @param x
	    * @param y
	    * @return
	    * 
	    * dd.DD_ini(0)
	    */
	   public int DD_mov(int x, int y);
	   public int DD_ini(int x);
	   
	   /**
	    * 功能： 模拟鼠标相对移动
		* 参数： 参数dx , 参数dy 以当前坐标为原点。
    	* 例子： 把鼠标向左移动10像素,DD_movR(-10,0) ;
	    * @param dx
	    * @param dy
	    * @return
	    */
	   public int DD_movR(int dx, int dy);
	   
	   /**
	    * 功能： 模拟鼠标点击
		* 参数： 1 =左键按下 ，2 =左键放开
		* 4 =右键按下 ，8 =右键放开
		* 16 =中键按下 ，32 =中键放开
		* 64 =4键按下 ，128 =4键放开
		* 256 =5键按下 ，512 =5键放开 
		* 例子：模拟鼠标右键 只需要连写(中间可添加延迟) dd_btn(4); dd_btn(8);
	    * @param btn
	    * @return
	    */
	   public int DD_btn(int btn);
	   
	   /**
	    * 功能: 模拟鼠标滚轮,参数: 1=前 , 2 = 后。例子: 向前滚一格, DD_whl(1)
	    * @param whl
	    * @return
	    */
	   public int DD_whl(int whl);
	   
	   /**
	    * DD_key(参数1，参数2)
		* 功能： 模拟键盘按键
		* 参数： 参数1 ，请查看[DD虚拟键盘码表]。参数2，1=按下，2=放开
		* 例子： 模拟单键WIN，DD_key(601, 1);DD_key(601, 2);
	    * @param ddcode
	    * @param flag
	    * @return
	    */
	   public int DD_key(int ddcode, int flag);
	   
	   /**
	    * 直接输入键盘上可见字符和空格,字符串, (注意，这个参数不是int32 类型)
	    * @param s
	    * @return
	    */
	   public int DD_str(String s);  
}
