package com.mindray.cis.provider;

import com.mindray.egateway.PropertiesUtil;
import joptsimple.internal.Strings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class HttpClientProvider implements IPushProvider {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientProvider.class);
    private String url;

    @PostConstruct
    public void init(){
        this.url = PropertiesUtil.getIns().getProperty("datasaveurl");
    }

    protected String postWithJson(String sendData){
        if(Strings.isNullOrEmpty(this.url)){
            logger.info("[info] url may not be empty.");
            return "";
        }
        String body = "";
        try {
            logger.info("[info]request url:{},data:{}",this.url,sendData);
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(this.url);
            // 装填参数
            StringEntity s = new StringEntity(sendData, "UTF-8");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            // 设置参数到请求对象中
            httpPost.setEntity(s);
            // 执行请求操作，并拿到结果（同步阻塞）
            HttpResponse response = client.execute(httpPost);
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
            //logger.info("JSON数据发送失败，异常：{}", e.getMessage());
            logger.error("JSON数据发送失败,异常：{}", e.getMessage());
        }
        logger.info("[info]response is: " + body);
        return body;
    }

    @Override
    public String sendJson(String json) {
        return this.postWithJson(json);
    }
}
