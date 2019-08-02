package io.renren.common.utils;

import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class TaoBaoutil {


    public static String updateAndSaveHtmlFile(String html, HttpServletRequest request){
        long time=0;
        try {
            //classpath:
            File path = new File(ResourceUtils.getURL("classpath:templates").getPath());
            String paths = request.getSession().getServletContext().getRealPath("/");
            System.out.println(path+"----"+paths);
            if (!path.exists())
                path = new File("");
            time = System.currentTimeMillis();
            String fileName = time + ".html";
            BufferedWriter bw = new BufferedWriter (new OutputStreamWriter(new FileOutputStream(paths + "/" + fileName), "UTF-8"));
            //logger.warn("path:" + path.getAbsolutePath());
            bw.write(html.toCharArray());
            bw.close();
        } catch (Exception e) {
            //logger.warn("------------html文件保存失败-----------");
            e.printStackTrace();
        }
        return time + "";
    }



    public static String updateAndSaveHtmlFile(String html, HttpServletRequest request,String type){
        long time=0;
        try {
            //classpath:
            File path = new File(ResourceUtils.getURL("classpath:templates").getPath());
            String paths = request.getSession().getServletContext().getRealPath("/");
            System.out.println(path+"----"+paths);
            if (!path.exists())
                path = new File("");
            time = System.currentTimeMillis();
            String fileName = time + ".html";
            BufferedWriter bw = new BufferedWriter (new OutputStreamWriter(new FileOutputStream(paths + "/" + fileName), "GBK"));
            //logger.warn("path:" + path.getAbsolutePath());
            bw.write(html.toCharArray());
            bw.close();
        } catch (Exception e) {
            //logger.warn("------------html文件保存失败-----------");
            e.printStackTrace();
        }
        return time + "";
    }

}
