package com.mindray.cis.hospital;

import com.alibaba.fastjson.JSONObject;
import com.mindray.config.annatation.EnableCISClient;
import com.mindray.egateway.PropertiesUtil;
import com.mindray.cis.third.IAdtQuery;
import com.mindray.cis.third.IClient;
import com.mindray.egateway.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@EnableCISClient(isEnable = false)
public class SampleAdtQueryClient implements IClient, IAdtQuery {
    private static final Logger logger = LoggerFactory.getLogger(SampleAdtQueryClient.class);
    private URL adtdatasaveUrl;

    @Override
    public List<AdtResponse> querySync(AdtRequest request) throws AdtQueryException {
        List<AdtResponse> list = new ArrayList<>();
        String pidString = "";
        pidString = request.getPid();
        try {
            Pid pid = new Pid();
            Pv1 pv1 = new Pv1();
            // 根据病人唯一号查询住院患者信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pid", pidString);
            // 调用三方接口
            String ret = sendJsonPost(adtdatasaveUrl, jsonObject);
            if ("".equals(ret) || null == ret) {
                logger.info(pidString + "：病历号查询返回空");
                AdtResponse adtResponse = new AdtResponse();
                adtResponse.setPid(pid);
                adtResponse.setPv1(pv1);
                list.add(adtResponse);
            } else {
                JSONObject json = (JSONObject) JSONObject.parse(ret);
                AdtResponse response = new AdtResponse();

                pid.setPid(json.getString("pid"));
                pv1.setVisitNumber(json.getString("vid"));
                pid.setFirstName(json.getString("name"));

                String Sex = json.getString("gender");
                if ("1".equals(Sex)) {
                    pid.setGenderCode("M");
                } else if ("2".equals(Sex)) {
                    pid.setGenderCode("F");
                }
                String DateOfBirth = json.getString("dob");
                try {
                    if (StringUtils.isNotBlank(DateOfBirth) && DateOfBirth.length() >= 8) {
                        DateOfBirth = DateOfBirth.substring(0, 8);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        sdf.setLenient(false);
                        sdf.parse(DateOfBirth);
                        pid.setBirthday(DateOfBirth);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                pv1.setBed(json.getString("bed"));
                pv1.setRoom(json.getString("room"));
                pv1.setDepartment(json.getString("department"));
                pv1.setFacility(json.getString("facility"));
                pv1.setMacAddress(json.getString("deviceid"));
                pv1.setReferringDoctor(json.getString("doctor"));// 管床医生
                response.setPid(pid);
                response.setPv1(pv1);
                response.setHeight(json.getString("height"));
                response.setWeight(json.getString("weight"));
                list.add(response);
            }
        } catch (Exception e) {
            logger.error("查询病人异常：" + e.getMessage());
        }
        return list;
    }

    @Override
    public void init() throws Exception {
        try {
            PropertiesUtil propUtil = PropertiesUtil.getIns();
            propUtil.init();
            if (StringUtils.isNotEmpty(propUtil.getProperty("adtdatasaveurl"))) {
                adtdatasaveUrl = new URL(propUtil.getProperty("adtdatasaveurl"));
            }
        } catch (MalformedURLException e) {
            logger.info(String.format("Can not initialize the default result datasave from {0},{1}", PropertiesUtil.getIns().getProperty("datasaveurl"),
                    PropertiesUtil.getIns().getProperty("adtdatasaveurl")));
        }
    }

    @PostConstruct
    private void initialize() throws Exception {
        this.init();
    }

    public static String sendJsonPost(URL url, JSONObject sendData) {
        String body = "";
        try {
            logger.info("ADT request is:" + sendData.toJSONString());
            CloseableHttpClient client = HttpClients.createDefault();
            // 设置参数
            URIBuilder uriBuilder = new URIBuilder(url.toString());
            URI uri = uriBuilder.setParameter("pid", sendData.getString("pid")).build();
            logger.info("请求完整地址为：" + uri);
            // 创建get方式请求对象
            HttpGet httpGet = new HttpGet(uri);
            // 设置header信息
            httpGet.setHeader("Content-Type", "application/json");

            // 执行请求操作，并拿到结果（同步阻塞）
            HttpResponse response = client.execute(httpGet);
            // 获取结果实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
            // 为防止频繁调用一个接口导致接口爆掉，每次调用完成后停留100毫秒
            Thread.sleep(100);
        } catch (Exception e) {
            logger.info("JSON数据发送失败，异常：{}", e.getMessage());
            logger.error("异常：", e);
        }
        logger.info("ADT response is: {}", body);
        return body;
    }
}
