package com.mindray.egateway;

import com.mindray.adtserver.AdtController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class PropertiesUtil {
	private static PropertiesUtil sPropertiesUtil = null;
	private final Properties prop = new Properties();
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	public static PropertiesUtil getIns(){
		if (sPropertiesUtil == null) {
			sPropertiesUtil = new PropertiesUtil();
			try {
				sPropertiesUtil.init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sPropertiesUtil;
	}

	private PropertiesUtil() {

	}

	public void init() throws IOException {
		String property = System.getProperty("user.dir");
		logger.info("[info] current dir:{}",property);
		Path path = Paths.get(property, "config.properties");
		if(Files.exists(path)){
			logger.info("[info]current dir exists file config.properties");
			InputStream in = new FileInputStream(path.toString());
			assert in != null;
			prop.load(new InputStreamReader(in, StandardCharsets.UTF_8)); //中文乱码
			in.close();
		}else{
			logger.info("[info]current dir not exists file config.properties,read from classpath.");
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.properties");
			assert in != null;
			prop.load(new InputStreamReader(in, StandardCharsets.UTF_8)); //中文乱码
			in.close();
		}
	}

	public String getProperty(String key) {
		return prop.getProperty(key);
	}
}
