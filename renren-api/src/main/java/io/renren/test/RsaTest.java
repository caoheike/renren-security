package io.renren.test;

import io.renren.common.utils.RsaUtil;

public class RsaTest {

    public static void main(String[] args)throws Exception {
////        //调用放加密
////        String encodedData = RsaUtil.publicEncrypt("{\"callBackUrl\":\"http://3ezuvs.natappfree.cc/web/user/tb/insert\",\"custId\":\"0ed845e39d5b11e9bad0d9028c015ff8\"}", RsaUtil.getPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLUcXsDz8aryTtkdyw1ePiDGLpK-1ifhcr9O5-F5GL7L5F1h5ObKmbsTgOeFE0xhAA5hKlrxnE-TwFRxRHg-6M0PhPptBjiamWzIA9A7vml_3t5ky70m9T9DQpyli_ry5Sx5Klfxw7ObWHz628qX70sdEpp6zDwEfB5Du6RY07cwIDAQAB"));
////        System.out.println("密文：\r\n" + encodedData);
////
////        //接口方解密
////
//        String decodedData = RsaUtil.privateDecrypt("dj5XWPAIhlXwlvokavhPTx0EaXkNF3kIdyGiFqc9y_ytjfBx-lAibT4aUZ927yf0GcZEyrtEkct0Tlr02QEWdqZDLiCQwun5rUhQAuKUmtsSyM_sDpR3A3nGHvf6asDXZ81LNkvFF_mcHV-bDjZupqA06QAnQzGxkQJlbr7PcNU9EKEe5Ku6DZw1Be0RS1RKVOuuqAazt60kVjqCd1GfLJJeWg6GnFK-X48h3K3mjT1TMBPXigbvXIic4TWjTUfE12lOJrmfLyNdwwMfpc8e9YsrliJ-pHPjBDt3JNtnN9Wzb_qlurJx64rPwAPO3ch_Vmt8eoDEy7TX_sxFXZMLF4M1wAiPuB6Q0adcXCePWnrTBLWpiFGTDsJ1YVW45n4GgV_-6VNgrkxMkiGAS_Mtyc5q_WjOct4E13K9a6_1RZyK7a9SVaoMoxjfPzbuU6HGzi4sZZX7B8AvvOtWB0mkQ5NwYVVKJiGzjT9KZI1RmNzmoW1-Ltvv7W2KgsM_tdu_", RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY));
//        System.out.println("解密后文字: \r\n" + decodedData);
//
        //接口方加密
//        String decodedData =  RsaUtil.privateEncrypt("weizai",RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY));
//        System.out.println(decodedData);
//        //调用方解密
////
   String decodedData1 = RsaUtil.publicDecrypt("dj5XWPAIhlXwlvokavhPTx0EaXkNF3kIdyGiFqc9y_ytjfBx-lAibT4aUZ927yf0GcZEyrtEkct0Tlr02QEWdqZDLiCQwun5rUhQAuKUmtsSyM_sDpR3A3nGHvf6asDXZ81LNkvFF_mcHV-bDjZupqA06QAnQzGxkQJlbr7PcNU9EKEe5Ku6DZw1Be0RS1RKVOuuqAazt60kVjqCd1GfLJJeWg6GnFK-X48h3K3mjT1TMBPXigbvXIic4TWjTUfE12lOJrmfLyNdwwMfpc8e9YsrliJ-pHPjBDt3JNtnN9Wzb_qlurJx64rPwAPO3ch_Vmt8eoDEy7TX_sxFXZMLF4M1wAiPuB6Q0adcXCePWnrTBLWpiFGTDsJ1YVW45n4GgV_-6VNgrkxMkiGAS_Mtyc5q_WjOct4E13K9a6_1RZyK7a9SVaoMoxjfPzbuU6HGzi4sZZX7B8AvvOtWB0mkQ5NwYVVKJiGzjT9KZI1RmNzmoW1-Ltvv7W2KgsM_tdu_",RsaUtil.getPublicKey(RsaUtil.PUBLIC_KEY));
     System.out.println(decodedData1);


//        //调用方加密
//        String data="\n" +
//                "{\n" +
//                "\"userName\":\"/gaga-fangfang/\",\n" +
//                "\"userPwd\":\"FANGFANGgaga21\",\n" +
//                "\"userCode\":\"bruqf81\",\n" +
//                "\"userCard\":\"123123\",\n" +
//                "\"callBackUrl\":\"123\"\n" +
//                "}";
//
//
//        String ss="\n" +
//                "{\n" +
//                "\"userCard\":\"123123\",\n" +
//                "\"userType\":\"征信\",\n" +
//                "}";
//       String s = RsaUtil.publicEncrypt(ss, RsaUtil.getPublicKey(RsaUtil.PUBLIC_KEY));
//       System.out.println(s);
//



    }


    //    public static void main(String[] args) throws Exception {
//        Result result = SendRequest.sendGet("http://http.tiqu.qingjuhe.cn/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack=22044&ts=1&ys=1&cs=1&lb=1&sb=0&pb=4&mr=0&regions=", null, null, "utf-8");
//        JSONObject jsonObject = JSONObject.parseObject(result.getHtmlContent("utf-8"));
//        String data= jsonObject.getString("data");
//        JSONObject data1=JSONObject.parseObject(JSONObject.parseArray(data).get(0).toString());
//
//        WebClient webClient=new WebClient(BrowserVersion.CHROME,data1.get("ip").toString(),Integer.parseInt(data1.get("port").toString()));
//        webClient.getOptions().setCssEnabled(false);
//        webClient.getOptions().setJavaScriptEnabled(false);
//        HtmlPage page = webClient.getPage("https://ipcrs.pbccrc.org.cn/");
//
//        System.out.println(page.getTitleText());
//
//    }


}
