package com.mindray.cis.hospital;

import com.alibaba.fastjson.JSONObject;
import com.mindray.cis.third.IClient;
import com.mindray.cis.provider.IPushProvider;
import com.mindray.cis.third.IResult;
import com.mindray.config.annatation.EnableCISClient;
import com.mindray.egateway.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


@EnableCISClient(isEnable = false)
public class SampleClient implements IResult, IClient {

	private static final Logger logger = LoggerFactory.getLogger(SampleClient.class);

	@Resource
	private IPushProvider pushProvider;
     //start 参数常量
	// 多线程方式，需要用ConcurrentHashMap
	// 心率
	ConcurrentHashMap<String, String> hrHashMap = new ConcurrentHashMap<String, String>();
	private final static String HR_PARAID = "1.7.4.147842";
	private final static String HR_PARAID1 = "1.6.3.147842";
	private final static String HR_PARAID2 = "1.33.1.147842";

	// 血氧
	ConcurrentHashMap<String, String> spo2HashMap = new ConcurrentHashMap<String, String>();
	private final static String SPO2_PARAID = "1.3.1.150456";
	private final static String SPO2_PARAID1 = "1.3.2.150456";
	private final static String SPO2_PARAID2 = "1.3.4.150456";
	private final static String SPO2_PARAID3 = "1.13.1.150456";
	private final static String SPO2_PARAID4 = "1.14.1.150456";
	private final static String SPO2_PARAID5 = "1.15.1.150456";
	private final static String SPO2_PARAID6 = "1.33.1.150456";

	// 脉率
	ConcurrentHashMap<String, String> prHashMap = new ConcurrentHashMap<String, String>();
	private final static String PR1_PARAID = "1.3.1.149530";
	private final static String PR2_PARAID = "1.3.2.149530";
	private final static String PR3_PARAID = "1.3.4.149530";
	private final static String PR4_PARAID = "1.6.3.149530";
	private final static String PR5_PARAID = "1.15.1.149530";
	private final static String PR6_PARAID = "1.43.X.149530";
	private final static String PR7_PARAID = "1.1.9.149546";
	private final static String PR8_PARAID = "1.1.14.149546";
	private final static String PR9_PARAID = "1.1.15.149546";
	private final static String PR10_PARAID = "1.1.16.149546";
	private final static String PR11_PARAID = "1.1.17.149546";
	private final static String PR12_PARAID = "1.1.18.149546";
	private final static String PR13_PARAID = "1.1.19.149546";
	private final static String PR14_PARAID = "1.1.20.149546";
	private final static String PR15_PARAID = "1.1.21.149546";
	private final static String PR16_PARAID = "1.1.22.149546";
	private final static String PR17_PARAID = "1.1.23.149546";
	private final static String PR18_PARAID = "1.1.24.149546";
	private final static String PR19_PARAID = "1.1.25.149546";
	private final static String PR20_PARAID = "1.1.27.149546";
	private final static String PR21_PARAID = "1.1.28.149546";
	private final static String PR22_PARAID = "1.13.1.149546";

	// 呼吸
	ConcurrentHashMap<String, String> rrHashMap = new ConcurrentHashMap<String, String>();
	private final static String RR_PARAID = "1.7.1.151578";
	private final static String RR_PARAID1 = "1.11.4.151570";
	private final static String RR_PARAID2 = "1.14.1.151562";
	private final static String RR_PARAID3 = "1.33.1.151562";
	private final static String RR_PARAID4 = "1.43.1.151562";

	// 体温
	ConcurrentHashMap<String, String> tempHashMap = new ConcurrentHashMap<String, String>();
	private final static String T1_TEMP = "1.2.1.150344";
	private final static String T2_TEMP = "1.2.2.150344";
	private final static String T3_TEMP = "1.2.3.150344";
	private final static String T4_TEMP = "1.2.4.150344";
	private final static String T5_TEMP = "1.2.5.150344";
	private final static String T6_TEMP = "1.2.6.150344";
	private final static String T7_TEMP = "1.2.7.150344";
	private final static String T8_TEMP = "1.2.8.150344";
	private final static String T9_TEMP = "1.2.9.150344";
	private final static String T10_TEMP = "1.2.10.150344";
	// 体温-鼓膜
	private final static String T1_TYMP = "1.2.1.150392";
	private final static String T2_TYMP = "1.2.2.150392";
	private final static String T3_TYMP = "1.2.3.150392";
	private final static String T4_TYMP = "1.2.4.150392";
	private final static String T5_TYMP = "1.2.5.150392";
	private final static String T6_TYMP = "1.2.6.150392";
	private final static String T7_TYMP = "1.2.7.150392";
	private final static String T8_TYMP = "1.2.8.150392";
	private final static String T9_TYMP = "1.2.9.150392";
	private final static String T10_TYMP = "1.2.10.150392";
	// 体温-动脉血
	private final static String T1_ART = "1.2.1.150352";
	private final static String T2_ART = "1.2.2.150352";
	private final static String T3_ART = "1.2.3.150352";
	private final static String T4_ART = "1.2.4.150352";
	private final static String T5_ART = "1.2.5.150352";
	private final static String T6_ART = "1.2.6.150352";
	private final static String T7_ART = "1.2.7.150352";
	private final static String T8_ART = "1.2.8.150352";
	private final static String T9_ART = "1.2.9.150352";
	private final static String T10_ART = "1.2.10.150352";
	// 体温-静脉血
	private final static String T1_VEN = "1.2.1.150396";
	private final static String T2_VEN = "1.2.2.150396";
	private final static String T3_VEN = "1.2.3.150396";
	private final static String T4_VEN = "1.2.4.150396";
	private final static String T5_VEN = "1.2.5.150396";
	private final static String T6_VEN = "1.2.6.150396";
	private final static String T7_VEN = "1.2.7.150396";
	private final static String T8_VEN = "1.2.8.150396";
	private final static String T9_VEN = "1.2.9.150396";
	private final static String T10_VEN = "1.2.10.150396";
	// 体温-口腔
	private final static String T1_ORAL = "1.2.1.188424";
	private final static String T2_ORAL = "1.2.2.188424";
	private final static String T3_ORAL = "1.2.3.188424";
	private final static String T5_ORAL = "1.2.5.188424";
	// 体温-皮肤
	private final static String T1_SKIN = "1.2.1.150388";
	private final static String T2_SKIN = "1.2.2.150388";
	private final static String T3_SKIN = "1.2.3.150388";
	private final static String T4_SKIN = "1.2.4.150388";
	private final static String T5_SKIN = "1.2.5.150388";
	private final static String T6_SKIN = "1.2.6.150388";
	private final static String T7_SKIN = "1.2.7.150388";
	private final static String T8_SKIN = "1.2.8.150388";
	private final static String T9_SKIN = "1.2.9.150388";
	private final static String T10_SKIN = "1.2.10.150388";
	// 体温-核心
	private final static String T1_CORE = "1.2.1.150368";
	private final static String T2_CORE = "1.2.2.150368";
	private final static String T3_CORE = "1.2.3.150368";
	private final static String T4_CORE = "1.2.4.150368";
	private final static String T5_CORE = "1.2.5.150368";
	private final static String T6_CORE = "1.2.6.150368";
	private final static String T7_CORE = "1.2.7.150368";
	private final static String T8_CORE = "1.2.8.150368";
	private final static String T9_CORE = "1.2.9.150368";
	private final static String T10_CORE = "1.2.10.150368";
	// 体温-腋下
	private final static String T1_AXIL = "1.2.1.188496";
	private final static String T2_AXIL = "1.2.2.188496";
	private final static String T3_AXIL = "1.2.3.188496";
	private final static String T4_AXIL = "1.2.4.188496";
	private final static String T5_AXIL = "1.2.5.188496";
	private final static String T6_AXIL = "1.2.6.188496";
	private final static String T7_AXIL = "1.2.7.188496";
	private final static String T8_AXIL = "1.2.8.188496";
	private final static String T9_AXIL = "1.2.9.188496";
	private final static String T10_AXIL = "1.2.10.188496";
	// 体温-鼻咽
	private final static String T1_NASOPH = "1.2.1.150380";
	private final static String T2_NASOPH = "1.2.2.150380";
	private final static String T3_NASOPH = "1.2.3.150380";
	private final static String T4_NASOPH = "1.2.4.150380";
	private final static String T5_NASOPH = "1.2.5.150380";
	private final static String T6_NASOPH = "1.2.6.150380";
	private final static String T7_NASOPH = "1.2.7.150380";
	private final static String T8_NASOPH = "1.2.8.150380";
	private final static String T9_NASOPH = "1.2.9.150380";
	private final static String T10_NASOPH = "1.2.10.150380";
	// 体温-食道
	private final static String T1_ESOPH = "1.2.1.150372";
	private final static String T2_ESOPH = "1.2.2.150372";
	private final static String T3_ESOPH = "1.2.3.150372";
	private final static String T4_ESOPH = "1.2.4.150372";
	private final static String T5_ESOPH = "1.2.5.150372";
	private final static String T6_ESOPH = "1.2.6.150372";
	private final static String T7_ESOPH = "1.2.7.150372";
	private final static String T8_ESOPH = "1.2.8.150372";
	private final static String T9_ESOPH = "1.2.9.150372";
	private final static String T10_ESOPH = "1.2.10.150372";
	// 体温-直肠
	private final static String T1_RECT = "1.2.1.188420";
	private final static String T2_RECT = "1.2.2.188420";
	private final static String T3_RECT = "1.2.3.188420";
	private final static String T4_RECT = "1.2.4.188420";
	private final static String T5_RECT = "1.2.5.188420";
	private final static String T6_RECT = "1.2.6.188420";
	private final static String T7_RECT = "1.2.7.188420";
	private final static String T8_RECT = "1.2.8.188420";
	private final static String T9_RECT = "1.2.9.188420";
	private final static String T10_RECT = "1.2.10.188420";

	// 耳温
	private final static String TEMP_EAR1 = "1.2.1.188428";
	private final static String TEMP_EAR2 = "1.2.2.188428";
	private final static String TEMP_EAR3 = "1.2.3.188428";
	private final static String TEMP_EAR4 = "1.2.4.188428";
	private final static String TEMP_EAR5 = "1.2.5.188428";
	private final static String TEMP_EAR6 = "1.2.6.188428";
	private final static String TEMP_EAR7 = "1.2.7.188428";
	private final static String TEMP_EAR8 = "1.2.8.188428";
	private final static String TEMP_EAR9 = "1.2.9.188428";
	private final static String TEMP_EAR10 = "1.2.10.188428";
	// 体温
	private final static String TEMP_TEMPLE1 = "1.2.1.471";
	private final static String TEMP_TEMPLE2 = "1.2.2.471";
	private final static String TEMP_TEMPLE3 = "1.33.1.283";
	private final static String TEMP_TEMPLE4 = "1.26.1.283";

	private final static String TEMP_TEMPLE5 = "1.2.3.471";
	private final static String TEMP_TEMPLE6 = "1.2.5.471";
	private final static String TEMP_TEMPLE7 = "1.2.6.471";
	private final static String TEMP_TEMPLE8 = "1.2.7.471";
	private final static String TEMP_TEMPLE9 = "1.2.8.471";
	private final static String TEMP_TEMPLE10 = "1.2.9.471";
	private final static String TEMP_TEMPLE11 = "1.2.10.471";

	// 无创血压
	ConcurrentHashMap<String, String> nibpSHashMap = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> nibpDHashMap = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> nibpMHashMap = new ConcurrentHashMap<String, String>();
	private final static String NIBPD_PARAID = "1.1.9.150022";
	private final static String NIBPS_PARAID = "1.1.9.150021";
	private final static String NIBPM_PARAID = "1.1.9.150023";
	private final static String NIBPD_PARAID14 = "1.1.14.150022";
	private final static String NIBPS_PARAID14 = "1.1.14.150021";
	private final static String NIBPM_PARAID14 = "1.1.14.150023";
	private final static String NIBPD_PARAID15 = "1.1.15.150022";
	private final static String NIBPS_PARAID15 = "1.1.15.150021";
	private final static String NIBPM_PARAID15 = "1.1.15.150023";
	private final static String NIBPD_PARAID16 = "1.1.16.150022";
	private final static String NIBPS_PARAID16 = "1.1.16.150021";
	private final static String NIBPM_PARAID16 = "1.1.16.150023";
	private final static String NIBPD_PARAID17 = "1.1.17.150022";
	private final static String NIBPS_PARAID17 = "1.1.17.150021";
	private final static String NIBPM_PARAID17 = "1.1.17.150023";
	private final static String NIBPD_PARAID18 = "1.1.18.150022";
	private final static String NIBPS_PARAID18 = "1.1.18.150021";
	private final static String NIBPM_PARAID18 = "1.1.18.150023";
	private final static String NIBPD_PARAID19 = "1.1.19.150022";
	private final static String NIBPS_PARAID19 = "1.1.19.150021";
	private final static String NIBPM_PARAID19 = "1.1.19.150023";
	private final static String NIBPD_PARAID20 = "1.1.20.150022";
	private final static String NIBPS_PARAID20 = "1.1.20.150021";
	private final static String NIBPM_PARAID20 = "1.1.20.150023";
	private final static String NIBPD_PARAID21 = "1.1.21.150022";
	private final static String NIBPS_PARAID21 = "1.1.21.150021";
	private final static String NIBPM_PARAID21 = "1.1.21.150023";
	private final static String NIBPD_PARAID22 = "1.1.22.150022";
	private final static String NIBPS_PARAID22 = "1.1.22.150021";
	private final static String NIBPM_PARAID22 = "1.1.22.150023";
	private final static String NIBPD_PARAID23 = "1.1.23.150022";
	private final static String NIBPS_PARAID23 = "1.1.23.150021";
	private final static String NIBPM_PARAID23 = "1.1.23.150023";
	private final static String NIBPD_PARAID24 = "1.1.24.150022";
	private final static String NIBPS_PARAID24 = "1.1.24.150021";
	private final static String NIBPM_PARAID24 = "1.1.24.150023";
	private final static String NIBPD_PARAID25 = "1.1.25.150022";
	private final static String NIBPS_PARAID25 = "1.1.25.150021";
	private final static String NIBPM_PARAID25 = "1.1.25.150023";
	private final static String NIBPD_PARAID27 = "1.1.27.150022";
	private final static String NIBPS_PARAID27 = "1.1.27.150021";
	private final static String NIBPM_PARAID27 = "1.1.27.150023";
	private final static String NIBPD_PARAID28 = "1.1.28.150022";
	private final static String NIBPS_PARAID28 = "1.1.28.150021";
	private final static String NIBPM_PARAID28 = "1.1.28.150023";
	private final static String NIBPD_PARAID33 = "1.33.1.150022";
	private final static String NIBPS_PARAID33 = "1.33.1.150021";
	private final static String NIBPM_PARAID33 = "1.33.1.150023";
	// 有创血压
	ConcurrentHashMap<String, String> ibpSHashMap = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> ibpDHashMap = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> ibpMHashMap = new ConcurrentHashMap<String, String>();
	private final static String IBPD_PARAID = "1.1.1.150018";
	private final static String IBPS_PARAID = "1.1.1.150017";
	private final static String IBPM_PARAID = "1.1.1.150019";
	private final static String ARTD_PARAID = "1.1.1.150038";
	private final static String ARTS_PARAID = "1.1.1.150037";
	private final static String ARTM_PARAID = "1.1.1.150039";

	private final static String ARTD_PARAID1 = "1.1.1.150682";
	private final static String ARTS_PARAID1 = "1.1.1.150681";
	private final static String ARTM_PARAID1 = "1.1.1.150683";

	// CVP
	ConcurrentHashMap<String, String> cvpSHashMap = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> cvpDHashMap = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> cvpMHashMap = new ConcurrentHashMap<String, String>();
	private final static String CVPS_PRESS = "1.1.1.150085";
	private final static String CVPD_PRESS = "1.1.1.150086";
	private final static String CVPM_PRESS = "1.1.1.150087";
	private final static String CVPS_PRESS1 = "1.1.12.150085";
	private final static String CVPD_PRESS1 = "1.1.12.150086";
	private final static String CVPM_PRESS1 = "1.1.12.150087";
    //end
	private final static String TIMEFORMAT_EGATEWAY_TIME = "yyyyMMddHHmmss.SSSSZ";
	private final static String TIMEFORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	private final static String TIMEFORMAT_DATE = "yyyy-MM-dd";
	private final static String TIMEFORMAT_TIME = "HH:mm";
	private final static String TIMEFORMAT_DATE_NEW = "yyyyMMdd";


	@Override
	public void init() throws Exception {

	}
	// 初始化参数数组
	public void initMap() {

		// RR
		rrHashMap.put("RR_PARAID", RR_PARAID);
		rrHashMap.put("RR_PARAID1", RR_PARAID1);
		rrHashMap.put("RR_PARAID2", RR_PARAID2);
		rrHashMap.put("RR_PARAID3", RR_PARAID3);
		rrHashMap.put("RR_PARAID3", RR_PARAID4);

		// SPO2
		spo2HashMap.put("SPO2_PARAID", SPO2_PARAID);
		spo2HashMap.put("SPO2_PARAID1", SPO2_PARAID1);
		spo2HashMap.put("SPO2_PARAID2", SPO2_PARAID2);
		spo2HashMap.put("SPO2_PARAID3", SPO2_PARAID3);
		spo2HashMap.put("SPO2_PARAID4", SPO2_PARAID4);
		spo2HashMap.put("SPO2_PARAID5", SPO2_PARAID5);
		spo2HashMap.put("SPO2_PARAID6", SPO2_PARAID6);

		// 心率
		hrHashMap.put("HR_PARAID", HR_PARAID);
		hrHashMap.put("HR_PARAID1", HR_PARAID1);
		hrHashMap.put("HR_PARAID2", HR_PARAID2);

		// 脉率
		prHashMap.put("PR1_PARAID", PR1_PARAID);
		prHashMap.put("PR2_PARAID", PR2_PARAID);
		prHashMap.put("PR3_PARAID", PR3_PARAID);
		prHashMap.put("PR4_PARAID", PR4_PARAID);
		prHashMap.put("PR5_PARAID", PR5_PARAID);
		prHashMap.put("PR6_PARAID", PR6_PARAID);
//		prHashMap.put("PR7_PARAID", PR7_PARAID);
//		prHashMap.put("PR8_PARAID", PR8_PARAID);
//		prHashMap.put("PR9_PARAID", PR9_PARAID);
//		prHashMap.put("PR10_PARAID", PR10_PARAID);
//		prHashMap.put("PR11_PARAID", PR11_PARAID);
//		prHashMap.put("PR12_PARAID", PR12_PARAID);
//		prHashMap.put("PR13_PARAID", PR13_PARAID);
//		prHashMap.put("PR14_PARAID", PR14_PARAID);
//		prHashMap.put("PR15_PARAID", PR15_PARAID);
//		prHashMap.put("PR16_PARAID", PR16_PARAID);
//		prHashMap.put("PR17_PARAID", PR17_PARAID);
//		prHashMap.put("PR18_PARAID", PR18_PARAID);
//		prHashMap.put("PR19_PARAID", PR19_PARAID);
//		prHashMap.put("PR20_PARAID", PR20_PARAID);
//		prHashMap.put("PR21_PARAID", PR21_PARAID);
//		prHashMap.put("PR22_PARAID", PR22_PARAID);

//start 体温
		tempHashMap.put("T1_TEMP", T1_TEMP);
		tempHashMap.put("T2_TEMP", T2_TEMP);
		tempHashMap.put("T3_TEMP", T3_TEMP);
		tempHashMap.put("T4_TEMP", T4_TEMP);
		tempHashMap.put("T5_TEMP", T5_TEMP);
		tempHashMap.put("T6_TEMP", T6_TEMP);
		tempHashMap.put("T7_TEMP", T7_TEMP);
		tempHashMap.put("T8_TEMP", T8_TEMP);
		tempHashMap.put("T9_TEMP", T9_TEMP);
		tempHashMap.put("T10_TEMP", T10_TEMP);
		tempHashMap.put("T1_TYMP", T1_TYMP);
		tempHashMap.put("T2_TYMP", T2_TYMP);
		tempHashMap.put("T3_TYMP", T3_TYMP);
		tempHashMap.put("T4_TYMP", T4_TYMP);
		tempHashMap.put("T5_TYMP", T5_TYMP);
		tempHashMap.put("T6_TYMP", T6_TYMP);
		tempHashMap.put("T7_TYMP", T7_TYMP);
		tempHashMap.put("T8_TYMP", T8_TYMP);
		tempHashMap.put("T9_TYMP", T9_TYMP);
		tempHashMap.put("T10_TYMP", T10_TYMP);
		tempHashMap.put("T1_ART", T1_ART);
		tempHashMap.put("T2_ART", T2_ART);
		tempHashMap.put("T3_ART", T3_ART);
		tempHashMap.put("T4_ART", T4_ART);
		tempHashMap.put("T5_ART", T5_ART);
		tempHashMap.put("T6_ART", T6_ART);
		tempHashMap.put("T7_ART", T7_ART);
		tempHashMap.put("T8_ART", T8_ART);
		tempHashMap.put("T9_ART", T9_ART);
		tempHashMap.put("T10_ART", T10_ART);
		tempHashMap.put("T1_VEN", T1_VEN);
		tempHashMap.put("T2_VEN", T2_VEN);
		tempHashMap.put("T3_VEN", T3_VEN);
		tempHashMap.put("T4_VEN", T4_VEN);
		tempHashMap.put("T5_VEN", T5_VEN);
		tempHashMap.put("T6_VEN", T6_VEN);
		tempHashMap.put("T7_VEN", T7_VEN);
		tempHashMap.put("T8_VEN", T8_VEN);
		tempHashMap.put("T9_VEN", T9_VEN);
		tempHashMap.put("T10_VEN", T10_VEN);
		tempHashMap.put("T1_ORAL", T1_ORAL);
		tempHashMap.put("T2_ORAL", T2_ORAL);
		tempHashMap.put("T3_ORAL", T3_ORAL);
		tempHashMap.put("T5_ORAL", T5_ORAL);
		tempHashMap.put("T1_SKIN", T1_SKIN);
		tempHashMap.put("T2_SKIN", T2_SKIN);
		tempHashMap.put("T3_SKIN", T3_SKIN);
		tempHashMap.put("T4_SKIN", T4_SKIN);
		tempHashMap.put("T5_SKIN", T5_SKIN);
		tempHashMap.put("T6_SKIN", T6_SKIN);
		tempHashMap.put("T7_SKIN", T7_SKIN);
		tempHashMap.put("T8_SKIN", T8_SKIN);
		tempHashMap.put("T9_SKIN", T9_SKIN);
		tempHashMap.put("T10_SKIN", T10_SKIN);
		tempHashMap.put("T1_CORE", T1_CORE);
		tempHashMap.put("T2_CORE", T2_CORE);
		tempHashMap.put("T3_CORE", T3_CORE);
		tempHashMap.put("T4_CORE", T4_CORE);
		tempHashMap.put("T5_CORE", T5_CORE);
		tempHashMap.put("T6_CORE", T6_CORE);
		tempHashMap.put("T7_CORE", T7_CORE);
		tempHashMap.put("T8_CORE", T8_CORE);
		tempHashMap.put("T9_CORE", T9_CORE);
		tempHashMap.put("T10_CORE", T10_CORE);
		tempHashMap.put("T1_AXIL", T1_AXIL);
		tempHashMap.put("T2_AXIL", T2_AXIL);
		tempHashMap.put("T3_AXIL", T3_AXIL);
		tempHashMap.put("T4_AXIL", T4_AXIL);
		tempHashMap.put("T5_AXIL", T5_AXIL);
		tempHashMap.put("T6_AXIL", T6_AXIL);
		tempHashMap.put("T7_AXIL", T7_AXIL);
		tempHashMap.put("T8_AXIL", T8_AXIL);
		tempHashMap.put("T9_AXIL", T9_AXIL);
		tempHashMap.put("T10_AXIL", T10_AXIL);
		tempHashMap.put("T1_NASOPH", T1_NASOPH);
		tempHashMap.put("T2_NASOPH", T2_NASOPH);
		tempHashMap.put("T3_NASOPH", T3_NASOPH);
		tempHashMap.put("T4_NASOPH", T4_NASOPH);
		tempHashMap.put("T5_NASOPH", T5_NASOPH);
		tempHashMap.put("T6_NASOPH", T6_NASOPH);
		tempHashMap.put("T7_NASOPH", T7_NASOPH);
		tempHashMap.put("T8_NASOPH", T8_NASOPH);
		tempHashMap.put("T9_NASOPH", T9_NASOPH);
		tempHashMap.put("T10_NASOPH", T10_NASOPH);
		tempHashMap.put("T1_ESOPH", T1_ESOPH);
		tempHashMap.put("T2_ESOPH", T2_ESOPH);
		tempHashMap.put("T3_ESOPH", T3_ESOPH);
		tempHashMap.put("T4_ESOPH", T4_ESOPH);
		tempHashMap.put("T5_ESOPH", T5_ESOPH);
		tempHashMap.put("T6_ESOPH", T6_ESOPH);
		tempHashMap.put("T7_ESOPH", T7_ESOPH);
		tempHashMap.put("T8_ESOPH", T8_ESOPH);
		tempHashMap.put("T9_ESOPH", T9_ESOPH);
		tempHashMap.put("T10_ESOPH", T10_ESOPH);
		tempHashMap.put("T1_RECT", T1_RECT);
		tempHashMap.put("T2_RECT", T2_RECT);
		tempHashMap.put("T3_RECT", T3_RECT);
		tempHashMap.put("T4_RECT", T4_RECT);
		tempHashMap.put("T5_RECT", T5_RECT);
		tempHashMap.put("T6_RECT", T6_RECT);
		tempHashMap.put("T7_RECT", T7_RECT);
		tempHashMap.put("T8_RECT", T8_RECT);
		tempHashMap.put("T9_RECT", T9_RECT);
		tempHashMap.put("T10_RECT", T10_RECT);
		tempHashMap.put("TEMP_EAR1", TEMP_EAR1);
		tempHashMap.put("TEMP_EAR2", TEMP_EAR2);
		tempHashMap.put("TEMP_EAR3", TEMP_EAR3);
		tempHashMap.put("TEMP_EAR4", TEMP_EAR4);
		tempHashMap.put("TEMP_EAR5", TEMP_EAR5);
		tempHashMap.put("TEMP_EAR6", TEMP_EAR6);
		tempHashMap.put("TEMP_EAR7", TEMP_EAR7);
		tempHashMap.put("TEMP_EAR8", TEMP_EAR8);
		tempHashMap.put("TEMP_EAR9", TEMP_EAR9);
		tempHashMap.put("TEMP_EAR10", TEMP_EAR10);
		tempHashMap.put("TEMP_TEMPLE1", TEMP_TEMPLE1);
		tempHashMap.put("TEMP_TEMPLE2", TEMP_TEMPLE2);
		tempHashMap.put("TEMP_TEMPLE3", TEMP_TEMPLE3);
		tempHashMap.put("TEMP_TEMPLE4", TEMP_TEMPLE4);
		tempHashMap.put("TEMP_TEMPLE5", TEMP_TEMPLE5);
		tempHashMap.put("TEMP_TEMPLE6", TEMP_TEMPLE6);
		tempHashMap.put("TEMP_TEMPLE7", TEMP_TEMPLE7);
		tempHashMap.put("TEMP_TEMPLE8", TEMP_TEMPLE8);
		tempHashMap.put("TEMP_TEMPLE9", TEMP_TEMPLE9);
		tempHashMap.put("TEMP_TEMPLE10", TEMP_TEMPLE10);
//end
		// 无创
		nibpSHashMap.put("NIBPS_PARAID", NIBPS_PARAID);
		nibpSHashMap.put("NIBPS_PARAID14", NIBPS_PARAID14);
		nibpSHashMap.put("NIBPS_PARAID15", NIBPS_PARAID15);
		nibpSHashMap.put("NIBPS_PARAID16", NIBPS_PARAID16);
		nibpSHashMap.put("NIBPS_PARAID17", NIBPS_PARAID17);
		nibpSHashMap.put("NIBPS_PARAID18", NIBPS_PARAID18);
		nibpSHashMap.put("NIBPS_PARAID19", NIBPS_PARAID19);
		nibpSHashMap.put("NIBPS_PARAID20", NIBPS_PARAID20);
		nibpSHashMap.put("NIBPS_PARAID21", NIBPS_PARAID21);
		nibpSHashMap.put("NIBPS_PARAID22", NIBPS_PARAID22);
		nibpSHashMap.put("NIBPS_PARAID23", NIBPS_PARAID23);
		nibpSHashMap.put("NIBPS_PARAID24", NIBPS_PARAID24);
		nibpSHashMap.put("NIBPS_PARAID25", NIBPS_PARAID25);
		nibpSHashMap.put("NIBPS_PARAID27", NIBPS_PARAID27);
		nibpSHashMap.put("NIBPS_PARAID28", NIBPS_PARAID28);
		nibpSHashMap.put("NIBPS_PARAID33", NIBPS_PARAID33);

		nibpMHashMap.put("NIBPM_PARAID", NIBPM_PARAID);
		nibpMHashMap.put("NIBPM_PARAID14", NIBPM_PARAID14);
		nibpMHashMap.put("NIBPM_PARAID15", NIBPM_PARAID15);
		nibpMHashMap.put("NIBPM_PARAID16", NIBPM_PARAID16);
		nibpMHashMap.put("NIBPM_PARAID17", NIBPM_PARAID17);
		nibpMHashMap.put("NIBPM_PARAID18", NIBPM_PARAID18);
		nibpMHashMap.put("NIBPM_PARAID19", NIBPM_PARAID19);
		nibpMHashMap.put("NIBPM_PARAID20", NIBPM_PARAID20);
		nibpMHashMap.put("NIBPM_PARAID21", NIBPM_PARAID21);
		nibpMHashMap.put("NIBPM_PARAID22", NIBPM_PARAID22);
		nibpMHashMap.put("NIBPM_PARAID23", NIBPM_PARAID23);
		nibpMHashMap.put("NIBPM_PARAID24", NIBPM_PARAID24);
		nibpMHashMap.put("NIBPM_PARAID25", NIBPM_PARAID25);
		nibpMHashMap.put("NIBPM_PARAID27", NIBPM_PARAID27);
		nibpMHashMap.put("NIBPM_PARAID28", NIBPM_PARAID28);
		nibpMHashMap.put("NIBPM_PARAID33", NIBPM_PARAID33);

		nibpDHashMap.put("NIBPD_PARAID", NIBPD_PARAID);
		nibpDHashMap.put("NIBPD_PARAID14", NIBPD_PARAID14);
		nibpDHashMap.put("NIBPD_PARAID15", NIBPD_PARAID15);
		nibpDHashMap.put("NIBPD_PARAID16", NIBPD_PARAID16);
		nibpDHashMap.put("NIBPD_PARAID17", NIBPD_PARAID17);
		nibpDHashMap.put("NIBPD_PARAID18", NIBPD_PARAID18);
		nibpDHashMap.put("NIBPD_PARAID19", NIBPD_PARAID19);
		nibpDHashMap.put("NIBPD_PARAID20", NIBPD_PARAID20);
		nibpDHashMap.put("NIBPD_PARAID21", NIBPD_PARAID21);
		nibpDHashMap.put("NIBPD_PARAID22", NIBPD_PARAID22);
		nibpDHashMap.put("NIBPD_PARAID23", NIBPD_PARAID23);
		nibpDHashMap.put("NIBPD_PARAID24", NIBPD_PARAID24);
		nibpDHashMap.put("NIBPD_PARAID25", NIBPD_PARAID25);
		nibpDHashMap.put("NIBPD_PARAID27", NIBPD_PARAID27);
		nibpDHashMap.put("NIBPD_PARAID28", NIBPD_PARAID28);
		nibpDHashMap.put("NIBPD_PARAID33", NIBPD_PARAID33);

		// 有创
		ibpSHashMap.put("IBPS_PARAID", IBPS_PARAID);
		ibpSHashMap.put("ARTS_PARAID", ARTS_PARAID);
		ibpSHashMap.put("ARTS_PARAID1", ARTS_PARAID1);

		ibpMHashMap.put("IBPM_PARAID", IBPM_PARAID);
		ibpMHashMap.put("ARTM_PARAID", ARTM_PARAID);
		ibpMHashMap.put("ARTM_PARAID1", ARTM_PARAID1);

		ibpDHashMap.put("IBPD_PARAID", IBPD_PARAID);
		ibpDHashMap.put("ARTD_PARAID", ARTD_PARAID);
		ibpDHashMap.put("ARTD_PARAID1", ARTD_PARAID1);

		// CVP
		cvpSHashMap.put("CVPS_PRESS", CVPS_PRESS);
		cvpSHashMap.put("CVPS_PRESS1", CVPS_PRESS1);

		cvpMHashMap.put("CVPM_PRESS", CVPM_PRESS);
		cvpMHashMap.put("CVPM_PRESS1", CVPM_PRESS1);

		cvpDHashMap.put("CVPD_PRESS", CVPD_PRESS);
		cvpDHashMap.put("CVPD_PRESS1", CVPD_PRESS1);
	}

	// 标准格式
	@Override
	@Async("taskExecutor")
	public void pushRealtimeResult(PatientResult result) throws IOException {
		initMap(); // 初始化参数
		try {
			if (result.getParameterResult() == null) {
				return;
			}
			JSONObject jsonObject = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("pid", result.getPid().getPid() != null ? result.getPid().getPid() : "");
			data.put("vid", result.getPv1().getVisitNumber() != null ? result.getPv1().getVisitNumber() : "");
			String name = "";
			if (result.getPid().getFirstName() != null) {
				name = result.getPid().getFirstName();
			} else if (result.getPid().getMiddleName() != null) {
				name = result.getPid().getMiddleName();
			} else if (result.getPid().getLastName() != null) {
				name = result.getPid().getLastName();
			} else if (result.getPid().getFirstName() != null && result.getPid().getMiddleName() != null && result.getPid().getLastName() != null) {
				name = result.getPid().getFirstName() + result.getPid().getMiddleName() + result.getPid().getLastName();
			}
			data.put("name", name);
			data.put("deptCode", result.getPv1().getDepartment());

			String recordTime = "";// 录入时间
			String planTime = "";// 测量时间
			SimpleDateFormat sdf_egw = new SimpleDateFormat(TIMEFORMAT_EGATEWAY_TIME);
			SimpleDateFormat sdf_his = new SimpleDateFormat(TIMEFORMAT_DATE_TIME);
			Date date = new Date();
			try {
				date = sdf_egw.parse(result.getParameterResult().getObserverTime());
				recordTime = sdf_his.format(date);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
			}
			data.put("dataTime", recordTime);
			data.put("bed", result.getPv1().getBed());
			data.put("equipId", result.getPv1().getMacAddress());

			// 体温
			for (ConcurrentHashMap.Entry<String, String> entry : tempHashMap.entrySet()) {
				String key = entry.getKey().toString();
				String valString = entry.getValue();
				String value = result.getParameterResult().getParameter(valString).getValue();
				if (StringUtils.isNotEmpty(value) && !data.containsKey("Temp")) {
					data.put("Temp", value);
				}
			}
			// PR
			for (ConcurrentHashMap.Entry<String, String> entry : prHashMap.entrySet()) {
				String key = entry.getKey().toString();
				String valString = entry.getValue();
				String value = result.getParameterResult().getParameter(valString).getValue();
				if (StringUtils.isNotEmpty(value) && !data.containsKey("PR")) {
					data.put("PR", value);
				}
			}
			// RR
			for (ConcurrentHashMap.Entry<String, String> entry : rrHashMap.entrySet()) {
				String key = entry.getKey().toString();
				String valString = entry.getValue();
				String value = result.getParameterResult().getParameter(valString).getValue();
				if (StringUtils.isNotEmpty(value) && !data.containsKey("Resp")) {
					data.put("Resp", value);
				}
			}
			// HR
			for (ConcurrentHashMap.Entry<String, String> entry : hrHashMap.entrySet()) {
				String key = entry.getKey().toString();
				String valString = entry.getValue();
				String value = result.getParameterResult().getParameter(valString).getValue();
				if (StringUtils.isNotEmpty(value) && !data.containsKey("HR")) {
					data.put("HR", value);
				}
			}
			// Spo2
			for (ConcurrentHashMap.Entry<String, String> entry : spo2HashMap.entrySet()) {
				String key = entry.getKey().toString();
				String valString = entry.getValue();
				String value = result.getParameterResult().getParameter(valString).getValue();
				if (StringUtils.isNotEmpty(value) && !data.containsKey("SpO2")) {
					data.put("SpO2", value);
				}
			}
			// ABP
			for (ConcurrentHashMap.Entry<String, String> entry : ibpSHashMap.entrySet()) {
				String key = entry.getKey().toString();
				String valString = entry.getValue();
				String value = result.getParameterResult().getParameter(valString).getValue();
				if (StringUtils.isNotEmpty(value) && !data.containsKey("ABPs")) {
					// data.put("ABP", "有创血压");
					String valuemV = "", valuedV = "";
					data.put("ABPs", value);

					for (ConcurrentHashMap.Entry<String, String> entrym : ibpMHashMap.entrySet()) {
						String keym = entrym.getKey().toString();
						String valmString = entrym.getValue();
						String valuem = result.getParameterResult().getParameter(valmString).getValue();
						if (StringUtils.isNotEmpty(valuem) && !data.containsKey("ABPm")) {
							data.put("ABPm", valuem);
							valuemV = valuem;
						}
					}
					for (ConcurrentHashMap.Entry<String, String> entryd : ibpDHashMap.entrySet()) {
						String keyd = entryd.getKey().toString();
						String valdString = entryd.getValue();
						String valued = result.getParameterResult().getParameter(valdString).getValue();
						if (StringUtils.isNotEmpty(valued) && !data.containsKey("ABPd")) {
							data.put("ABPd", valued);
							valuedV = valued;
						}
					}
					data.put("ABP", value + "/" + valuedV + "(" + valuemV + ")");
				}
			}
			// NBP
			for (ConcurrentHashMap.Entry<String, String> entry : nibpSHashMap.entrySet()) {
				String key = entry.getKey().toString();
				String valString = entry.getValue();
				String value = result.getParameterResult().getParameter(valString).getValue();
				if (StringUtils.isNotEmpty(value) && !data.containsKey("NBPs")) {
					// data.put("NBP", "无创血压");
					String valuemV = "", valuedV = "";
					data.put("NBPs", value);
					// data.put("NBPTime", recordTime);

					for (ConcurrentHashMap.Entry<String, String> entrym : nibpMHashMap.entrySet()) {
						String keym = entrym.getKey().toString();
						String valmString = entrym.getValue();
						String valuem = result.getParameterResult().getParameter(valmString).getValue();
						if (StringUtils.isNotEmpty(valuem) && !data.containsKey("NBPm")) {
							data.put("NBPm", valuem);
							valuemV = valuem;
						}
					}
					for (ConcurrentHashMap.Entry<String, String> entryd : nibpDHashMap.entrySet()) {
						String keyd = entryd.getKey().toString();
						String valdString = entryd.getValue();
						String valued = result.getParameterResult().getParameter(valdString).getValue();
						if (StringUtils.isNotEmpty(valued) && !data.containsKey("NBPd")) {
							data.put("NBPd", valued);
							valuedV = valued;
						}
					}
					data.put("NBP", value + "/" + valuedV + "(" + valuemV + ")");
				}
			}

			// CVP
			for (ConcurrentHashMap.Entry<String, String> entry : cvpSHashMap.entrySet()) {
				String key = entry.getKey().toString();
				String valString = entry.getValue();
				String value = result.getParameterResult().getParameter(valString).getValue();
				if (StringUtils.isNotEmpty(value) && !data.containsKey("CVPs")) {

					String valuemV = "", valuedV = "";
					data.put("CVPs", value);

					for (ConcurrentHashMap.Entry<String, String> entrym : cvpMHashMap.entrySet()) {
						String keym = entrym.getKey().toString();
						String valmString = entrym.getValue();
						String valuem = result.getParameterResult().getParameter(valmString).getValue();
						if (StringUtils.isNotEmpty(valuem) && !data.containsKey("CVPm")) {
							data.put("CVPm", valuem);
							valuemV = valuem;
						}
					}
					for (ConcurrentHashMap.Entry<String, String> entryd : cvpDHashMap.entrySet()) {
						String keyd = entryd.getKey().toString();
						String valdString = entryd.getValue();
						String valued = result.getParameterResult().getParameter(valdString).getValue();
						if (StringUtils.isNotEmpty(valued) && !data.containsKey("CVPd")) {
							data.put("CVPd", valued);
							valuedV = valued;
						}
					}
					data.put("CVP", value + "/" + valuedV + "(" + valuemV + ")");
				}
			}

			jsonObject.put("data", data);
			String resultString = jsonObject.toJSONString();

			asynSendResulttoEMR(resultString);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	private void asynSendResulttoEMR(String req) {
		this.pushProvider.sendJson(req);
	}
}
