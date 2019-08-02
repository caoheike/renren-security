package io.renren.common.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.util.ResourceUtils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class  DriverUtil{

	public static String unicodetoString(String unicode){
        if(unicode==null||"".equals(unicode)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        while((i=unicode.indexOf("\\u", pos)) != -1){
            sb.append(unicode.substring(pos, i));
            if(i+5 < unicode.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(unicode.substring(i+2, i+6), 16));
            }
        }
        return sb.toString();
    }




    private static void upLoadByCommonPost(String uploadUrl) throws IOException {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        URL url = new URL(uploadUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K  应该按照文件大小来定义
        // 允许输入输出流
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        // 使用POST方法
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        httpURLConnection.setRequestProperty("Content-Type",
                "multipart/form-data;boundary=" + boundary);

        DataOutputStream dos = new DataOutputStream(
                httpURLConnection.getOutputStream());
        dos.writeBytes(twoHyphens + boundary + end);
        dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\"1.jpg\";" + end);
        dos.writeBytes("Content-Type: image/jpeg" + end);
        dos.writeBytes(end);

        FileInputStream fis = new FileInputStream("d:\\test.jpg");
        byte[] buffer = new byte[1024*100]; // 100k
        int count = 0;
        // 读取文件
        while ((count = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, count);
        }
        fis.close();
        dos.writeBytes(end);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
        dos.flush();
        InputStream is = httpURLConnection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String result;
        while ((result=br.readLine()) != null){
            System.out.println(result);
        }
        dos.close();
        is.close();
    }



	/**
	 * WebDriver切换到当前页面
	 */
	public static void switchToCurrentPage(WebDriver driver) {
		String handle = driver.getWindowHandle();
		for (String tempHandle : driver.getWindowHandles()) {
			if(!tempHandle.equals(handle)) {
				driver.switchTo().window(tempHandle);
			}
		}
	}

	/**
	 * 切换frame
	 * @param locator
	 * @return    这个驱动程序切换到给定的frame
	 */
	public static void switchToFrame(By locator,WebDriver driver) {
		driver.switchTo().frame(driver.findElement(locator));
	}
	
	/**
	 * 判断是否有弹窗，如有弹窗则返回弹窗内的text，否则返回空
	 */
	public static String alertFlag(WebDriver driver){
		String str = "";
		try {
			 Alert alt = driver.switchTo().alert();
			 str = alt.getText();
			 System.out.println(str);
			 alt.accept();
			
		} catch (Exception e) {
			//不做任何处理
		}
		return str;
	}
	
	
	
	/**
	 * 关闭所有进程(针对64位的)，仅支持同步
	 * @param driver
	 * @throws IOException 
	 */
	public static void close(WebDriver driver,String exec) throws IOException{
		if(driver != null){
			driver.close();
			Runtime.getRuntime().exec(exec);
		}
	}
	
	
	/**
	 * 关闭所有进程(针对32位的)
	 * @param driver
	 */
	public static void close(WebDriver driver){
		if(driver != null){
			driver.quit();
		}
	}
	
	
	/**
	 * 
	 * @param type ie 或 chrome
	 * @return
	 */
	public static WebDriver getDriverInstance(String type){
		WebDriver driver = null;
		if(type.equals("ie")){
			System.setProperty("webdriver.ie.driver", "C:/ie/IEDriverServer.exe");
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
			ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			ieCapabilities.setCapability(InternetExplorerDriver.BROWSER_ATTACH_TIMEOUT,15000);
			driver = new InternetExplorerDriver(ieCapabilities);
			driver.manage().window().maximize();
		}
		if (type.equals("chrome")){
			ChromeOptions options = new ChromeOptions();
			options.addArguments("disable-infobars");// 设置chrome浏览器的参数，使其不弹框提示（chrome正在受自动测试软件的控制）
			options.setBinary("C:/Users/Administrator/AppData/Local/Google/Chrome/Application/chrome.exe");
			System.setProperty("webdriver.chrome.driver", "C:/chrome/chromedriver.exe");
			driver = new ChromeDriver(options);
			driver.manage().window().maximize();
		}
		return driver;
	}

	/**
	 * 使用代理ip
	 * @param type ie 或 chrome
	 * @return
	 */
	public static WebDriver getDriverInstance(String type,String ip,int port){
		WebDriver driver = null;
		if(type.equals("ie")){
			System.setProperty("webdriver.ie.driver", "C:/ie/IEDriverServer.exe");
			
			String proxyIpAndPort = ip+ ":" + port;
			Proxy proxy=new Proxy();
			proxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort);
			
			DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
			//代理
			cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
			cap.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
			System.setProperty("http.nonProxyHosts", ip);
			cap.setCapability(CapabilityType.PROXY, proxy);
			
			cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			cap.setCapability(InternetExplorerDriver.BROWSER_ATTACH_TIMEOUT,15000);
			driver = new InternetExplorerDriver(cap);
			driver.manage().window().maximize();
		}
		return driver;
	}
	
	
	/**
	 * 获取cookie
	 * @param driver
	 * @return
	 */
	public static String getCookie(WebDriver driver)		{
		  //获得cookie用于发包
		Set<org.openqa.selenium.Cookie> cookies = driver.manage().getCookies();  
	    StringBuffer tmpcookies = new StringBuffer();

	   	for (org.openqa.selenium.Cookie cookie : cookies) {
	   		String name = cookie.getName();
	   		String value = cookie.getValue();
			tmpcookies.append(name + "="+ value + ";");
		}
	   	String str = tmpcookies.toString();
	   	if(!str.isEmpty()){
	   		str = str.substring(0,str.lastIndexOf(";"));
	   	}
		return str; 	
	}
	
	/**
	 * 获取cookie
	 * @param driver
	 * @param jsession
	 * @return
	 */
	public static String getCookie(WebDriver driver,String jsession)		{
		//获得cookie用于发包
		Set<org.openqa.selenium.Cookie> cookies = driver.manage().getCookies();  
		StringBuffer tmpcookies = new StringBuffer();
		
		for (org.openqa.selenium.Cookie cookie : cookies) {
			String name = cookie.getName();
			String value = cookie.getValue();
			tmpcookies.append(name + "="+ value + ";");
		}
		tmpcookies.append("JSESSIONID");
		tmpcookies.append("=");
		tmpcookies.append(jsession);
		String str = tmpcookies.toString();
		return str; 	
	}

	/**
	 * 获取cookie 招商
	 * @param driver
	 * @return
	 */
	public static String getCookieCmd(WebDriver driver)		{
		//获得cookie用于发包
		Set<org.openqa.selenium.Cookie> cookies = driver.manage().getCookies();
		StringBuffer tmpcookies = new StringBuffer();

		for (org.openqa.selenium.Cookie cookie : cookies) {
			String name = cookie.getName();
			String value = cookie.getValue();
			if (name.equals("ProVersion")){
				tmpcookies.append(name + "=;");
			}else {
				tmpcookies.append(name + "="+ value + ";");
			}
		}
		String str = tmpcookies.toString();
		if(!str.isEmpty()){
			str = str.substring(0,str.lastIndexOf(";"));
		}
		return str;
	}

	/**
	 * 根据节点位置，对节点进行裁剪，获得截图
	 * @param driver
	 * @param webElement
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage createElementImage(WebDriver driver, WebElement webElement) throws IOException {
		// 获得webElement的位置和大小。
		Point location = webElement.getLocation();
		Dimension size = webElement.getSize();
		// 创建全屏截图。
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(takeScreenshot(driver)));
		// 截取webElement所在位置的子图。
		BufferedImage croppedImage = originalImage.getSubimage(location.getX(), location.getY(), size.getWidth(),size.getHeight());
		return croppedImage;
	}

	/**
	 * 保存截图文件
	 * @param bi
	 * @throws IOException
	 */
	public static void writeImageFile(BufferedImage bi,String imgPath) throws IOException {
		File outputfile = new File(imgPath);
		ImageIO.write(bi, "jpg", outputfile);
	}

//    public StringBuffer connection(Map<String,String> map, String strURL) {
//        // start
//        HttpClient httpClient = new HttpClient();
//
//        httpClient.getHostConfiguration().setProxy("10.192.10.101", 8080); //
//        httpClient.getParams().setAuthenticationPreemptive(true);
//
//        HttpConnectionManagerParams managerParams = httpClient
//                .getHttpConnectionManager().getParams();
//        // 设置连接超时时间(单位毫秒)
//        managerParams.setConnectionTimeout(30000);
//        // 设置读数据超时时间(单位毫秒)
//        managerParams.setSoTimeout(120000);
//
//        PostMethod postMethod = new PostMethod(strURL);
//        // 将请求参数XML的值放入postMethod中
//        String strResponse = null;
//        StringBuffer buffer = new StringBuffer();
//        // end
//        try {
//            //设置参数到请求对象中，重点是map中有几个参数NameValuePair数组也必须设置成几，不然
//            //会空指针异常
//            NameValuePair[] nvps = new NameValuePair[4];
//            int index = 0;
//            for(String key : map.keySet()){
//                nvps[index++]=new NameValuePair(key, map.get(key));
//            }
//            postMethod.setRequestBody(nvps);
//            int statusCode = httpClient.executeMethod(postMethod);
//            if (statusCode != HttpStatus.SC_OK) {
//                throw new IllegalStateException("Method failed: "
//                        + postMethod.getStatusLine());
//            }
//            BufferedReader reader = null;
//            reader = new BufferedReader(new InputStreamReader(
//                    postMethod.getResponseBodyAsStream(), "UTF-8"));
//            while ((strResponse = reader.readLine()) != null) {
//                buffer.append(strResponse);
//            }
//        } catch (Exception ex) {
//            throw new IllegalStateException(ex.toString());
//        } finally {
//            // 释放连接
//            postMethod.releaseConnection();
//        }
//        return buffer;
//    }
	  public static byte[] takeScreenshot(WebDriver driver) throws IOException {
	        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
	        return takesScreenshot.getScreenshotAs(OutputType.BYTES);
	    }
	  /**
	   * 根据传入包名 在templates包下创建此包,并返回该路径
	   * @param packageName
	   * @return
	   * @throws FileNotFoundException
	   */
	  public static String getTemplatesPath(String packageName) throws FileNotFoundException {
	    String path = ResourceUtils.getURL("classpath:templates").getPath() + "/" + packageName;
	    path = path.substring(path.indexOf(":")-1,path.length());
	    File file = new File(path);
	    if (!file.exists()){
	      file.mkdirs();
	    }
	    return path;
	  }
	  
	  public static void main(String[] args) {
		do {
			System.out.println(111);
		} while (1==2);
	}

	public static void ieClose()  {
	  	try {
			Runtime run = Runtime.getRuntime();
			run.exec("taskkill /im iexplore.exe /F");
		}catch (Exception e ) {


		}


	}

	public static String getTime() throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间

	}
}
