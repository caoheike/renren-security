package io.renren.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 征信数据解析
 *
 * @author mrlu
 * @date 2018/1/3
 */
public class CreditAnalysis {
    private static Logger logger = LoggerFactory.getLogger(CreditAnalysis.class);

    public static JSONObject analysisCredit(Map<String, Object> dataMap) throws Exception{

        logger.warn("开始解析征信数据...");
        Thread.sleep(5000);
       Object data = dataMap.get("repor");
        String reportHtml = data.toString();
        
        logger.warn("------征信：reportHtml" + reportHtml);
        Elements table = null;
        Document parse = null;
//        String userId = "**************271X";
        try {
//            parse = Jsoup.parse(new File("f://xinzhengxin.html"), "utf-8");
        //    parse = Jsoup.parse(reportHtml);
            parse= Jsoup.connect(reportHtml).get();
            table = parse.getElementsByTag("table");
            logger.warn("此次解析征信页面含有的table数量为:" + table.size());
        } catch (Exception e) {
            logger.error("征信数据解析失败，页面无法转换为正常html页面...",e);
            
        }
        //推送数据包;
        JSONObject resultData = new JSONObject();
        //数据集合
        JSONObject resultObj = new JSONObject();
        //征信报告基本信息
        JSONObject creditBasic = new JSONObject();
        //征信报告概要
        JSONObject creditSummary = new JSONObject();
        //征信报告信用卡信息
        JSONArray creditCard = new JSONArray();
        //征信报告贷款信息(住房贷款信息，其他信息)
        JSONArray creditLoan = new JSONArray();
        //征信报告担保信息
        JSONArray creditGuarantee = new JSONArray();
        //机构查询信息
        JSONArray orgQueryData = new JSONArray();
        //机构查询信息
        JSONArray personQueryData = new JSONArray();
        //页面中信息概要是第N个table
        int mesGaiYao=7;
        //页面机构查询是第N个table
        int goverSelect=10;
        //页面个人查询是第N个table
        int proSelect=11;
        //判断页面是否含有特殊表格   正常页面为13个table，特殊页面多一个保证人代偿信息table，需做特殊处理
        if(parse.text().contains("保证人代偿信息")&&parse.text().contains("资产处置信息")){
            mesGaiYao=8;
            goverSelect=11;
            proSelect=12;
        }
        try {
            //读取页面中第二个表格中关于征信报告的基本信息
            Element tbBasic1 = table.get(1);
            Elements trBasic1 = tbBasic1.getElementsByTag("tr");
            String flagReport = "个人信用报告";
            if (flagReport.equals(trBasic1.get(0).text())) {
                Elements tdBasic1 = trBasic1.get(1).getElementsByTag("td");
                //报告编号
                creditBasic.put("orderId", tdBasic1.get(0).text().substring(5).replace(" ", ""));
                //查询时间
                creditBasic.put("selecttime", tdBasic1.get(1).text().substring(5));
                creditSummary.put("queryDate", tdBasic1.get(1).text().substring(5));
                //报告时间
                creditBasic.put("reporttime", tdBasic1.get(2).text().substring(5));
            } else {
                logger.error ("页面第2个表格非个人信用报告...");
            }
            //获取征信报告姓名、证件类型、证件号码
            Element tbBasic2 = table.get(2);
            Elements trBasic2 = tbBasic2.getElementsByTag("tr");
            String nameFlag = "姓名：";
            Elements tdBasic2 = trBasic2.get(0).getElementsByTag("td");
            if (tdBasic2.get(0).text().contains(nameFlag)) {
                //报告姓名
                creditBasic.put("creditName", tdBasic2.get(0).text().substring(3).trim());
//                creditBasic.put("name", "胡献根");
                //证件类型
                creditBasic.put("creditType", tdBasic2.get(1).text().substring(5));
                //证件号码
                creditBasic.put("idCard", tdBasic2.get(2).text().substring(5));
//                creditBasic.put("idnumber", "612425199102131454");
                //证件空白
                creditBasic.put("kongbai", "");
            } else {
                logger.warn("页面第3个表格非个人信用报告...");
            }
            resultObj.put("credit_basic", creditBasic);
        } catch (Exception e) {
            logger.error("获取个人信用报告失败", e);
        }

        try {
            Element summaryTable = table.get(mesGaiYao);
            //获取征信报告概要
            Elements trSummary = summaryTable.getElementsByTag("tr");
            String tdTitle = trSummary.get(0).text();
            String keyWord1 = "信用卡";
            String keyWord2 = "购房贷款";
            String keyWord3 = "其他贷款";
            if (tdTitle.contains(keyWord1) && tdTitle.contains(keyWord2) && tdTitle.contains(keyWord3)) {
                //账户数信息
                Elements tdSummary1 = trSummary.get(1).getElementsByTag("td");
                //信用卡账户数
                creditSummary.put("cardAccount", tdSummary1.get(1).text());
                //购房贷款账户数
                creditSummary.put("housingLoanAccount", tdSummary1.get(2).text());
                //其他贷款账户数
                creditSummary.put("otherLoanAccount", tdSummary1.get(3).text());

                //未结清/未销户账户数
                Elements tdSummary2 = trSummary.get(2).getElementsByTag("td");
                //信用卡未结清/未销户账户数
                creditSummary.put("cardNotsettled", tdSummary2.get(1).text());
                //购房贷款未结清/未销户账户数
                creditSummary.put("housingLoanNotsettled", tdSummary2.get(2).text());
                //其他贷款未结清/未销户账户数
                creditSummary.put("otherLoanNotsettled", tdSummary2.get(3).text());

                //发生过逾期的账户数
                Elements tdSummary3 = trSummary.get(3).getElementsByTag("td");
                //信用卡发生过逾期的账户数
                creditSummary.put("cardOverdue", tdSummary3.get(1).text());
                //购房贷款发生过逾期的账户数
                creditSummary.put("housingLoanOverdue", tdSummary3.get(2).text());
                //其他贷款发生过逾期的账户数
                creditSummary.put("otherLoanOverdue", tdSummary3.get(3).text());

                // 发生过90天以上逾期的账户数
                Elements tdSummary4 = trSummary.get(4).getElementsByTag("td");
                //信用卡发生过90天以上逾期的账户数
                creditSummary.put("card90overdue", tdSummary4.get(1).text());
                //购房贷款发生过90天以上逾期的账户数 
                creditSummary.put("housingLoan90overdue", tdSummary4.get(2).text());
                //其他贷款发生过90天以上逾期的账户数
                creditSummary.put("otherLoan90overdue", tdSummary4.get(3).text());

                // 为他人担保笔数
                Elements tdSummary5 = trSummary.get(5).getElementsByTag("td");
                //信用卡为他人担保笔数
                creditSummary.put("cardGuaranty", tdSummary5.get(1).text());
                //购房贷款为他人担保笔数
                creditSummary.put("housingLoanGuaranty", tdSummary5.get(2).text());
                //其他贷款为他人担保笔数
                creditSummary.put("otherLoanGuaranty", tdSummary5.get(3).text());
            } else {
                logger.warn("没有获取到正确的征信报告概要");
                //信用卡账户数
                creditSummary.put("cardAccount","0");
                //购房贷款账户数
                creditSummary.put("housingLoanAccount","0");
                //其他贷款账户数
                creditSummary.put("otherLoanAccount", "0");

                //未结清/未销户账户数
                //信用卡未结清/未销户账户数
                creditSummary.put("cardNotsettled","0");
                //购房贷款未结清/未销户账户数
                creditSummary.put("housingLoanNotsettled", "0");
                //其他贷款未结清/未销户账户数
                creditSummary.put("otherLoanNotsettled", "0");

                //发生过逾期的账户数
                //信用卡发生过逾期的账户数
                creditSummary.put("cardOverdue", "0");
                //购房贷款发生过逾期的账户数
                creditSummary.put("housingLoanOverdue", "0");
                //其他贷款发生过逾期的账户数
                creditSummary.put("otherLoanOverdue", "0");

                // 发生过90天以上逾期的账户数
                //信用卡发生过90天以上逾期的账户数
                creditSummary.put("card90overdue", "0");
                //购房贷款发生过90天以上逾期的账户数 
                creditSummary.put("housingLoan90overdue", "0");
                //其他贷款发生过90天以上逾期的账户数
                creditSummary.put("otherLoan90overdue", "0");

                // 为他人担保笔数
                //信用卡为他人担保笔数
                creditSummary.put("cardGuaranty", "0");
                //购房贷款为他人担保笔数
                creditSummary.put("housingLoanGuaranty", "0");
                //其他贷款为他人担保笔数
                creditSummary.put("otherLoanGuaranty","0");
                
            }
            resultObj.put("credit_summary", creditSummary);
        } catch (Exception e) {
            logger.error("征信概要解析失败", e);
        }

        try {
            Elements ol = parse.getElementsByTag("span");
            for (Element el : ol) {
                //获取信用卡明细
                if ("信用卡".equals(el.text())) {
                    List<String> cardList = new ArrayList<>();
                    Element element = el.nextElementSibling();
                    Elements li = element.getElementsByTag("li");
                    for (Element item : li) {
                        cardList.add(item.text());
                    }
                    creditCard = getCreditCardList(cardList);
                }
                //获取其他贷款明细
                //获取购房贷款明细
                if ("其他贷款".equals(el.text()) || "购房贷款".equals(el.text())) {
                    List<String> otherLoan = new ArrayList<>();
                    Element element = el.nextElementSibling();
                    Elements li = element.getElementsByTag("li");
                    for (Element item : li) {
                        otherLoan.add(item.text());
                    }
                    String reportTime = "";
                    String reportTimeNew="";
                    //已结清贷款需要报告时间这个字段
                    if(creditBasic.isEmpty()&&creditBasic!=null&&creditBasic.size()!=0){
                    	
                    	reportTime=creditBasic.getString("reportTime");
                        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                        Date parse1 = simpleDateFormat.parse(reportTime);
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy年MM月dd日");
                        reportTimeNew = simpleDateFormat1.format(parse1);
                    }else{
                    	reportTimeNew="无";
                    }
           
                
              
                    //解析贷款明细
                    creditLoan = getOtherLoanList(otherLoan, el.text(), creditLoan,reportTimeNew);
                }

                //为他人担保信息
                if ("为他人担保信息".equals(el.text())) {
                    List<String> otherGuarantee = new ArrayList<>();
                    Element element = el.nextElementSibling();
                    Elements li = element.getElementsByTag("li");
                    for (Element item : li) {
                        otherGuarantee.add(item.text());
                    }
                    creditGuarantee = getCreditGuarantee(otherGuarantee);
                }
            }
        } catch (Exception e) {
            logger.warn("数据明细获取失败", e);
        }
        resultObj.put("credit_card", creditCard);
        resultObj.put("credit_loan", creditLoan);
        resultObj.put("credit_guarantee", creditGuarantee);
        //公共记录
        JSONArray publicData = new JSONArray();
        JSONObject onePbData = new JSONObject();
        onePbData.put("publicRecord", "公共记录");
        publicData.add(onePbData);
        resultObj.put("credit_ggjl", publicData);


        try {
            //机构查询明细
            Element orgElement = table.get(goverSelect);
            orgQueryData = analysisOrgQuery(orgElement, "org");
            //个人查询明细
            Element personElement = table.get(proSelect);
            personQueryData = analysisOrgQuery(personElement, "per");
        } catch (Exception e) {
            logger.error("数据明细获取失败", e);
        }
        resultObj.put("credit_chaxun1", orgQueryData);
        resultObj.put("credit_chaxun2", personQueryData);
//        resultData.put("data", resultObj);
        resultObj.put("html", parse.html());
        
        return resultObj;
    }

    /**
     * 解析信用卡账单列表
     *
     * @param cardList
     * @return
     */
    public static JSONArray getCreditCardList(List<String> cardList) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (String cardRecord : cardList) {
                if (cardRecord != null && !cardRecord.isEmpty()) {
                    JSONObject cardJson = new JSONObject();
                    cardJson.put("overdue", "0");
                    cardJson.put("fiveYearsOverdue", "0");
                    cardJson.put("ninetyDaysOverdue", "0");
                    cardJson.put("overdueMoney", "0");
                    //卡类型/
                    String a = cardRecord.substring(cardRecord.indexOf("发放的") + 3);
                    String purpose = a.substring(0,a.indexOf("（"));
                    cardJson.put("accountCategory", purpose);

                    if ("贷记卡".equals(purpose)) {
                        //判断是否有逾期
                        if (cardRecord.contains("逾期状态")) {
                            cardJson.put("overdue", "1");
                            //读取五年内逾期月数
                            if (cardRecord.contains("年内有") && cardRecord.contains("个月处于逾期状态")) {
                                String counts = cardRecord.substring(cardRecord.indexOf("年内有") + 3, cardRecord.indexOf("个月处于逾期状态"));
                                cardJson.put("fiveYearsOverdue", counts);
                            }
                            //逾期超过90天次数
                            if (cardRecord.contains("逾期超过90天")) {
                                String yuQi90Count = cardRecord.substring(cardRecord.indexOf("其中") + 2, cardRecord.indexOf("个月逾期"));
                                cardJson.put("ninetyDaysOverdue", yuQi90Count);
                            }
                            //当前逾期金额
                            if (cardRecord.contains("逾期金额")) {
                                String dueMoney = cardRecord.substring(cardRecord.indexOf("逾期金额") + 4);
                                dueMoney = dueMoney.substring(0, dueMoney.indexOf("。")).replace(",", "").trim();
                                cardJson.put("overdueMoney", dueMoney);
                            }
                        }
                    } else if ("准贷记卡".equals(purpose)) {
                        //判断是否有透支超过60天记录（逾期）
                        if (cardRecord.contains("透支超过60天")) {
                            cardJson.put("overdue", "1");
                            //读取五年内逾期月数
                            if (cardRecord.contains("年内有") && cardRecord.contains("个月透支超过60天")) {
                                String counts = cardRecord.substring(cardRecord.indexOf("年内有") + 3, cardRecord.indexOf("个月透支超过60天"));
                                cardJson.put("fiveYearsOverdue", counts);
                            }
                            //逾期超过90天次数
                            if (cardRecord.contains("透支超过90天")) {
                                String yuQi90Count = cardRecord.substring(cardRecord.indexOf("其中") + 2, cardRecord.indexOf("个月透支超过90天"));
                                cardJson.put("ninetyDaysOverdue", yuQi90Count);
                            }
                        }
                    } else {
                        //系统暂无匹配类型的信用卡
                    }

                    // 正常、逾期、呆账、未激活、销户
                    //帐户状态：未激活,销户, 正常，呆账
                    if (cardRecord.indexOf("未激活") > 0) {
                        cardJson.put("accountState", "未激活");
                    } else if (cardRecord.indexOf("销户") > 0) {
                        cardJson.put("accountState", "销户");
                    } else if (cardRecord.indexOf("呆账") > 0) {
                        cardJson.put("accountState", "呆账");
                    } else if (cardRecord.indexOf("逾期金额") > 0) {
                        cardJson.put("accountState", "逾期");
                    } else {
                        cardJson.put("accountState", "正常");
                    }

                    //发卡日期
                    String cardTime = cardRecord.substring(0, cardRecord.indexOf("日") + 1);
                    cardJson.put("grantDate", cardTime);
                    //发卡行
                    String bankName = cardRecord.substring(cardRecord.indexOf("日") + 1, cardRecord.indexOf("发放的"));
                    cardJson.put("bank", bankName);
                    //账户类型
                    String accountType = cardRecord.substring(cardRecord.indexOf("（") + 1, cardRecord.indexOf("）"));
                    cardJson.put("accountType", accountType);
                    //截止日期
                    String expireDateStr = cardRecord.substring(cardRecord.indexOf("截至") + 2);
                    expireDateStr = expireDateStr.substring(0, expireDateStr.indexOf("月") + 1);
                    cardJson.put("queryDate", expireDateStr);
                    //信用额度
                    String creditLine = "0";
                    if (cardRecord.contains("信用额度")) {
                        if (cardRecord.contains("折合人民币")) {
                            creditLine = cardRecord.substring(cardRecord.indexOf("折合人民币") + 5);
                        } else {
                            creditLine = cardRecord.substring(cardRecord.indexOf("信用额度") + 4);
                        }
                        
                        int index = creditLine.indexOf("，");
                        int index1 = creditLine.indexOf("。");
                        
                        if(index > index1) {
                        	index = index1;
                        }
                        
                        creditLine = creditLine.substring(0, index).replace(",", "").trim();
                        cardJson.put("creditLine", creditLine);
                    } else {
                        cardJson.put("creditLine", creditLine);
                    }

                    //已使用额度
                    if (cardRecord.contains("已使用额度") || cardRecord.contains("透支余额")) {
                        String usedMoney = null;
                        if (cardRecord.contains("已使用额度")) {
                            usedMoney = cardRecord.substring(cardRecord.indexOf("已使用额度") + 5);
                            if (usedMoney.contains("逾期金额")) {
                                usedMoney = usedMoney.substring(0, usedMoney.indexOf("，")).replace(",", "").trim();
                            } else {
                                usedMoney = usedMoney.substring(0, usedMoney.indexOf("。")).replace(",", "").trim();
                            }
                        } else if (cardRecord.contains("透支余额")) {
                            usedMoney = cardRecord.substring(cardRecord.indexOf("透支余额") + 4);
                            usedMoney = usedMoney.substring(0, usedMoney.indexOf("。")).replace(",", "").trim();
                            if ("0".equals(creditLine)) {
                                int i = Integer.parseInt(creditLine);
                                int j = Integer.parseInt(usedMoney);
                                if (j - i > 0) {
                                    cardJson.put("accountState", "逾期");
                                    cardJson.put("overdueMoney", j - i);
                                }
                            }
                        }
                        cardJson.put("usedLine", usedMoney);
                    } else {
                        cardJson.put("usedLine", "0");
                    }
                    jsonArray.add(cardJson);
                }
            }
            return jsonArray;
        } catch (Exception e) {
        	logger.error("信用卡账单解析失败",e);
        	return jsonArray;
        }
    }


    /**
     * 解析其他贷款信息和购房贷款信息
     *
     * @param otherLoan
     * @return
     */
    public static JSONArray getOtherLoanList(List<String> otherLoan, String loanType, JSONArray otherLoanList,String reportTimeNew) {
        try {
            for (String loadRecord : otherLoan) {
                JSONObject oneLoanRecord = new JSONObject();
                if (loadRecord != null && !loadRecord.isEmpty()) {
                    //贷款类型
                    oneLoanRecord.put("loanType", loanType);
                    //发放日期
                    String cardTime = loadRecord.substring(0, loadRecord.indexOf("日") + 1);
                    oneLoanRecord.put("grantDate", cardTime);
                    //发放机构
                    String bankName = loadRecord.substring(loadRecord.indexOf("日") + 1, loadRecord.indexOf("发放的"));
                    oneLoanRecord.put("institution", bankName);
                    //发放金额
                    String a = loadRecord.substring(loadRecord.indexOf("发放的") + 3);
                    String creditLine = a.substring( 0, a.indexOf("（") - 1).replace(",", "").trim();
                    oneLoanRecord.put("money", creditLine);
                    //贷款用途
                    String purpose = loadRecord.substring(loadRecord.indexOf("）") + 1, loadRecord.indexOf("，"));
                    oneLoanRecord.put("accountCategoryMain", purpose);
                    //其他贷款备注
                    oneLoanRecord.put("accountCategory", "");
                    //
                    //到期日期
                    if (loadRecord.contains("日到期")) {
                        String expireDate = loadRecord.substring(loadRecord.indexOf("，") + 1, loadRecord.indexOf("日到期") + 1);
                        oneLoanRecord.put("expiredDate", expireDate);
                    } else  {
                        oneLoanRecord.put("expiredDate", "");
                    }
                    //截止日期
                    if (loadRecord.contains("截至")) {
                        String pDateStr = loadRecord.substring(loadRecord.indexOf("截至") + 2, loadRecord.indexOf("余额") - 1);
                        oneLoanRecord.put("queryDate", pDateStr);
                    } else {
                        oneLoanRecord.put("queryDate", "");
                    }
                    //余额
                    if (loadRecord.contains("余额")) {
                        String remainderStr = loadRecord.substring(loadRecord.indexOf("余额") + 2).replace(",", "");
                        if (loadRecord.contains("逾期金额")) {
                            remainderStr = remainderStr.substring(0, remainderStr.indexOf("，")).replaceAll(",", "").trim();
                        } else {
                            remainderStr = remainderStr.substring(0, remainderStr.indexOf("。")).replaceAll(",", "").trim();
                        }
                        oneLoanRecord.put("figure", remainderStr);
                    } else {
                        oneLoanRecord.put("figure", "0");
                    }

                    //贷款状态
                    if (loadRecord.contains("已结清")) {
                        oneLoanRecord.put("loanState", "已结清");
                        String pDateStr = loadRecord.substring(loadRecord.indexOf("，") + 1, loadRecord.indexOf("已结清"));
                        //已结清的贷款到期时间为截止日期
                        oneLoanRecord.put("expiredDate", pDateStr);
                        //已结清贷款截止日期为报告时间
                        oneLoanRecord.put("queryDate", reportTimeNew);
                    } else if (loadRecord.contains("已转出")) {
                        oneLoanRecord.put("loanState", "已转出");
                        String pDateStr = loadRecord.substring(loadRecord.indexOf("，") + 1, loadRecord.indexOf("已转出"));
                        //已结清的贷款到期时间为截止日期
                        oneLoanRecord.put("expiredDate", pDateStr);
                        //已结清贷款截止日期为报告时间
                        oneLoanRecord.put("queryDate", reportTimeNew);
                    } else if (loadRecord.contains("逾期金额")) {
                        oneLoanRecord.put("loanState", "逾期");
                    } else if (loadRecord.contains("呆账")) {
                        oneLoanRecord.put("loanState", "呆账");
                    } else {
                        oneLoanRecord.put("loanState", "正常");
                    }

                    //逾期状态
                    oneLoanRecord.put("overdue", "0");
                    oneLoanRecord.put("years5Overdue", "0");
                    oneLoanRecord.put("days90Overdue", "0");
                    oneLoanRecord.put("yuqiMoney", "0");
                    //判断是否有逾期
                    if (loadRecord.contains("逾期状态")) {
                        oneLoanRecord.put("overdue", "1");
                        //读取五年内逾期月数
                        if (loadRecord.contains("年内有") && loadRecord.contains("个月处于逾期状态")) {
                            String counts = loadRecord.substring(loadRecord.indexOf("年内有") + 3, loadRecord.indexOf("个月处于逾期状态"));
                            oneLoanRecord.put("years5Overdue", counts);
                        }
                        //逾期超过90天次数
                        if (loadRecord.contains("逾期超过90天")) {
                            String yuQi90Count = loadRecord.substring(loadRecord.indexOf("其中") + 2, loadRecord.indexOf("个月逾期"));
                            oneLoanRecord.put("days90Overdue", yuQi90Count);
                        }
                        //当前逾期金额
                        if (loadRecord.contains("逾期金额")) {
                            String dueMoney = loadRecord.substring(loadRecord.indexOf("逾期金额") + 4);
                            dueMoney = dueMoney.substring(0, dueMoney.indexOf("。")).replace(",", "").trim();
                            oneLoanRecord.put("yuqiMoney", dueMoney);
                        }
                    }
                }

                otherLoanList.add(oneLoanRecord);
            }
            return otherLoanList;
        } catch (Exception e) {
        	logger.error("-------------贷款信息解析失败1111---",e);
            return otherLoanList;
        }
    }

    /**
     * 解析为他人担保信息
     *
     * @param otherGuarantee
     * @return
     */
    public static JSONArray getCreditGuarantee(List<String> otherGuarantee) {
        JSONArray jsonGuarantee = new JSONArray();
        try {
        	 for (String recorde : otherGuarantee) {
                 JSONObject infoMes = new JSONObject();
                 if (recorde != null && !recorde.isEmpty()) {
                   System.out.println("==recorde"+recorde);
                     //起始日期
                     String startTime = recorde.substring(0, recorde.indexOf("，"));
                     infoMes.put("grantDate", startTime);
                     //被担保人
                     String nameLoan = recorde.substring(recorde.indexOf("为") + 1, recorde.indexOf("（"));
                     infoMes.put("nameLoan", nameLoan);
                     //证件类型
                     String cardType = recorde.substring(recorde.indexOf("证件类型：") + 5);
                     cardType = cardType.substring(0, cardType.indexOf("，"));
                     infoMes.put("cardType", cardType);
                     //证件后四位
                     String cardLoan = recorde.substring(recorde.indexOf("）") - 4, recorde.indexOf("）"));
                     infoMes.put("cardLoan", cardLoan);
                     //贷款发放机构
                     String institution = recorde.substring(recorde.indexOf("）在") + 2, recorde.indexOf("办理的"));
                     infoMes.put("institution", institution);
                     //担保合同金额
                     String dbMoney="0";
                     String dbMoney2="0";
                     if(recorde.indexOf("担保贷款合同金额")>-1){
                     dbMoney = recorde.substring(recorde.indexOf("担保贷款合同金额") + 8);
                     dbMoney = dbMoney.substring(0, dbMoney.indexOf("，")).replace(",", "");
                  
                     //担保金额
                     dbMoney2 = recorde.substring(recorde.indexOf("担保金额") + 4);
                     dbMoney2 = dbMoney2.substring(0, dbMoney2.indexOf("。")).replace(",", "");
                 
                     }
                     else if(recorde.indexOf("授信额度")>-1){
                          dbMoney = recorde.substring(recorde.indexOf("授信额度") + 4);
                            dbMoney = dbMoney.substring(0, dbMoney.indexOf("，")).replace(",", "");
                         
                            //担保金额
                            dbMoney2 = recorde.substring(recorde.indexOf("担保金额") + 4);
                            dbMoney2 = dbMoney2.substring(0, dbMoney2.indexOf("。")).replace(",", "");
                     }
                       
                     infoMes.put("money", dbMoney);
                     infoMes.put("money2", dbMoney2);
                     //截止日期
                     String queryDate = recorde.substring(recorde.indexOf("截至") + 2);
                     queryDate = queryDate.substring(0, queryDate.indexOf("，"));
                     infoMes.put("queryDate", queryDate);
                     //余额
                     String figure="0";
                     if(recorde.indexOf("担保贷款余额")>-1){
                       figure = recorde.substring(recorde.indexOf("担保贷款余额") + 6);
                       figure = figure.substring(0, figure.indexOf("。")).replace(",", "");
                     }
                    
                   
                     infoMes.put("figure", figure);
                     //贷款状态
                     if (figure.equals("0")) {
                         infoMes.put("loanState", "已结清");
                     } else {
                         if (recorde.contains("逾期金额")) {
                             infoMes.put("loanState", "逾期");
                         } else {
                             infoMes.put("loanState", "正常");
                         }
                     }
                     //逾期金额
                     infoMes.put("yuqiMoney", "待确定");
                 }
                 jsonGuarantee.add(infoMes);
             }
             return jsonGuarantee;
        } catch (Exception e) {
        	logger.error("担保信息解析失败", e);
        	return jsonGuarantee;
        }
    }

    /**
     * 解析机构查询表单
     *
     * @param table
     */
    public static JSONArray analysisOrgQuery(Element table, String flag) {
        JSONArray orgQueryData = new JSONArray();
        String signle = "";
        try {
            Elements tr = table.getElementsByTag("tr");
            if (tr.size() > 2) {
                String text = tr.get(0).text();

                if (flag.equals("org")) {
                    signle = "机构查询记录明细";
                } else if (flag.equals("per")) {
                    signle = "个人查询记录明细";
                }
                if (text.equals(signle)) {
                    for (int i = 3; i < tr.size() - 1; i++) {
                        JSONObject jsonData = new JSONObject();
                        Elements td = tr.get(i).getElementsByTag("td");
                        jsonData.put("chaxunDate", td.get(1).text());
                        jsonData.put("caozuoyuan", td.get(2).text());
                        jsonData.put("reason", td.get(3).text());
                        orgQueryData.add(jsonData);
                    }
                } else {
                    System.out.println(signle + "记录表格获取失败");
                }
            }
            return orgQueryData;
        } catch (Exception e) {
        	logger.error(signle + "解析失败", e);
        	return orgQueryData;
        }
    }
    
    
   
}
