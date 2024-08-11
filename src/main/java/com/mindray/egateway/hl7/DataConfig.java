package com.mindray.egateway.hl7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class DataConfig {

	private static final Logger logger = LoggerFactory.getLogger(DataConfig.class);

	private HashMap<String, String> unit_name_map = new HashMap<>();
	private HashMap<String, String> para_name_map = new HashMap<>();
	private HashMap<String, String> wave_name_map = new HashMap<>();

	private static DataConfig sInstance = null;

	public static DataConfig getIns() {
		if (sInstance == null) {
			sInstance = new DataConfig();
		}
		return sInstance;
	}

	public String GetParaName(String key) {
		String ret = "";
		if (para_name_map.containsKey(key)) {
			ret = para_name_map.get(key);
		}
		return ret;
	}

	public String GetUnitName(String key) {
		String ret = "";
		if (unit_name_map.containsKey(key)) {
			ret = unit_name_map.get(key);
		}
		return ret;
	}

	public String GetWaveName(String key) {
		String ret = "";
		if (wave_name_map.containsKey(key)) {
			ret = wave_name_map.get(key);
		}
		return ret;
	}

	public void LoadDataConfig(String fileName) throws IOException {

		InputStream input = DataConfig.class.getClassLoader().getResourceAsStream(fileName);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(input);

			NodeList units = doc.getElementsByTagName("Unit");
			for (int i = 0; i < units.getLength(); i++) {

				Element element = (Element) units.item(i);
				String label = element.getAttribute("LABEL");
				String[] codes = new String[] { element.getAttribute("IDEN"), element.getAttribute("TEXT"),
						element.getAttribute("SYSTEM") };

				unit_name_map.put(String.join("^", codes), label);

			}

			NodeList paras = doc.getElementsByTagName("Param");
			for (int i = 0; i < paras.getLength(); i++) {

				Element element = (Element) paras.item(i);
				String label = element.getAttribute("LABEL");

				NodeList sources = element.getElementsByTagName("Source");
				for (int j = 0; j < sources.getLength(); j++) {
					Element element_src = (Element) sources.item(j);
					String id = element_src.getAttribute("SUB_ID");
					para_name_map.put(id, label);
				}
			}

			NodeList waves = doc.getElementsByTagName("Waveform");
			for (int i = 0; i < waves.getLength(); i++) {
				Element element = (Element) waves.item(i);
				wave_name_map.put(element.getAttribute("SUB_ID"), element.getAttribute("LABEL"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error occured when load the dataconfig.xml" + e.getMessage());
		} finally {
			if (input != null) {
				input.close();
			}

		}

	}

}
