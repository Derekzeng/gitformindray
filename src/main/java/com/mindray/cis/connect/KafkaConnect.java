package com.mindray.cis.connect;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author 50234332 2020年3月28日
 *
 */
// https://blog.csdn.net/feiqinbushizheng/article/details/89184144
public class KafkaConnect implements IConnect {

	/**
	 * 
	 */
	public KafkaConnect() {
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = LoggerFactory.getLogger(KafkaConnect.class);

	public static final String MQ_ADDRESS_COLLECTION = "127.0.0.1:9092"; // kafka地址
	public static final String CONSUMER_TOPIC = "topicDemo"; // 消费者连接的topic
	public static final String PRODUCER_TOPIC = "topicDemo"; // 生产者连接的topic
	public static final String CONSUMER_GROUP_ID = "1"; // groupId，可以分开配置
	public static final String CONSUMER_ENABLE_AUTO_COMMIT = "true"; // 是否自动提交（消费者）
	public static final String CONSUMER_AUTO_COMMIT_INTERVAL_MS = "1000";
	public static final String CONSUMER_SESSION_TIMEOUT_MS = "30000"; // 连接超时时间
	public static final int CONSUMER_MAX_POLL_RECORDS = 10; // 每次拉取数
	public static final Duration CONSUMER_POLL_TIME_OUT = Duration.ofMillis(3000); // 拉去数据超时时间

	private static KafkaConsumer<String, String> consumer;

	/**
	 * 初始化配置
	 */
	private static void initConsumerConfig() {

		// PropertiesUtil propUtil = PropertiesUtil.getIns();
		// String servers = propUtil.getProperty("bootstrap.servers");

		Properties props = new Properties();
		props.put("bootstrap.servers", MQ_ADDRESS_COLLECTION);
		props.put("group.id", CONSUMER_GROUP_ID);
		props.put("enable.auto.commit", CONSUMER_ENABLE_AUTO_COMMIT);
		props.put("auto.commit.interval.ms", CONSUMER_AUTO_COMMIT_INTERVAL_MS);
		props.put("session.timeout.ms", CONSUMER_SESSION_TIMEOUT_MS);
		props.put("max.poll.records", CONSUMER_MAX_POLL_RECORDS);
		props.put("auto.offset.reset", "earliest");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());

		consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList(CONSUMER_TOPIC));

	}

	public static void Consumer() {

		// 初始化消费者
		initConsumerConfig();

		while (true) {

			ConsumerRecords<String, String> records = consumer.poll(CONSUMER_POLL_TIME_OUT);
			records.forEach((ConsumerRecord<String, String> record) -> {
				logger.info("revice: key ===" + record.key() + " value ====" + record.value() + " topic ==="
						+ record.topic());
			});
		}
	}

	private static KafkaProducer<String, String> producer = null;

	/*
	 * 初始化配置
	 */
	private static void initProducerConfig() {
		Properties props = new Properties();
		props.put("bootstrap.servers", MQ_ADDRESS_COLLECTION);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", StringSerializer.class.getName());

		producer = new KafkaProducer<String, String>(props);
	}

//	public static void main(String[] args) throws InterruptedException {
//		// 初始化生产者
//		initProducerConfig();
//
//		// 消息实体
//		ProducerRecord<String, String> record = null;
//		for (int i = 0; i < 100; i++) {
//			record = new ProducerRecord<String, String>(PRODUCER_TOPIC, "value" + i);
//			// 发送消息
//			producer.send(record, new Callback() {
//				@Override
//				public void onCompletion(RecordMetadata recordMetadata, Exception e) {
//					if (null != e) {
//						logger.info("send error" + e.getMessage());
//					} else {
//						logger.info(String.format("offset:%s,partition:%s", recordMetadata.offset(),
//								recordMetadata.partition()));
//					}
//				}
//			});
//		}
//		producer.close();
//	}

}
