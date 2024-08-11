/**
 * 50234332
 *2020年3月28日
 */
package com.mindray.cis.connect;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * @author 50234332
 *
 */
// reference：https://www.cnblogs.com/lfalex0831/p/8963247.html
//           https://www.rabbitmq.com/getstarted.html

public class RabbitMqConnect implements IConnect {
	private static final Logger logger = LoggerFactory.getLogger(RabbitMqConnect.class);

	private static final String RABBIT_HOST = "localhost";

	private static final String RABBIT_USERNAME = "guest";

	private static final String RABBIT_PASSWORD = "guest";

	private static final String QUEUE_NAME = "test_queue";

	private static Connection mConnection = null;

	/**
	* 
	*/
	public RabbitMqConnect() {
		// TODO Auto-generated constructor stub
	}

	// 创建RabbitMQ
	/**
	 * @return 50234332
	 */
	public static Connection GetConnection() {
		if (mConnection == null) {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost(RABBIT_HOST);
			connectionFactory.setUsername(RABBIT_USERNAME);
			connectionFactory.setPassword(RABBIT_PASSWORD);
			try {
				mConnection = connectionFactory.newConnection();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} catch (TimeoutException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return mConnection;
	}

	public static void CloseConnection() throws IOException {
		if (mConnection != null) {
			mConnection.close();
		}
	}

	// 1、简单使用

	public void Producer() throws Exception {
		// 获取连接

		System.out.println(mConnection);
		// 创建通道
		Channel channel = mConnection.createChannel(1);
		/*
		 * 声明（创建）队列 参数1：队列名称 参数2：为true时server重启队列不会消失
		 * 参数3：队列是否是独占的，如果为true只能被一个connection使用，其他连接建立时会抛出异常
		 * 参数4：队列不再使用时是否自动删除（没有连接，并且没有未处理的消息) 参数5：建立队列时的其他参数
		 */
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		String message = "Hello World!";
		for (int i = 0; i < 20; i++) {
			message = message + i;
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			Thread.sleep(1000);
		}
		System.out.println("生产者 send ：" + message);
		channel.close();

	}

	public void Consumer() throws Exception {

		Channel channel = mConnection.createChannel(1);
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		StringBuffer message = new StringBuffer();
		// 自4.0+ 版本后无法再使用QueueingConsumer，而官方推荐使用DefaultConsumer
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				super.handleDelivery(consumerTag, envelope, properties, body);
				message.append(new String(body, "UTF-8"));
				System.out.println(new String(body, "UTF-8"));
			}
		};
		// 监听队列，当b为true时，为自动提交（只要消息从队列中获取，无论消费者获取到消息后是否成功消息，都认为是消息已经成功消费），
		// 当b为false时，为手动提交（消费者从队列中获取消息后，服务器会将该消息标记为不可用状态，等待消费者的反馈，
		// 如果消费者一直没有反馈，那么该消息将一直处于不可用状态。
		// 如果选用自动确认,在消费者拿走消息执行过程中出现宕机时,消息可能就会丢失！！）
		// 使用channel.basicAck(envelope.getDeliveryTag(),false);进行消息确认
		channel.basicConsume(QUEUE_NAME, true, consumer);
		System.out.println(message.toString());
	}

	// 2、Work Queue

	public void ProducerWorkQueue() throws Exception {

		Channel channel = mConnection.createChannel();
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		for (int i = 0; i < 50; i++) {
			String message = "" + i;
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			Thread.sleep(100 * i);
		}
		channel.close();

	}

	public void ConsumerWorkQueue() throws IOException {

		Channel channel = mConnection.createChannel();
		channel.basicQos(1);// 能者多劳模式
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		// 自4.0+ 版本后无法再使用QueueingConsumer，而官方推荐使用DefaultConsumer
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				super.handleDelivery(consumerTag, envelope, properties, body);
				String message = new String(body, "UTF-8");
				System.out.println(message);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					doWork(message);
				} finally {
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		// 监听队列，当b为true时，为自动提交（只要消息从队列中获取，无论消费者获取到消息后是否成功消息，都认为是消息已经成功消费），
		// 当b为false时，为手动提交（消费者从队列中获取消息后，服务器会将该消息标记为不可用状态，等待消费者的反馈，
		// 如果消费者一直没有反馈，那么该消息将一直处于不可用状态。
		// 如果选用自动确认,在消费者拿走消息执行过程中出现宕机时,消息可能就会丢失！！）
		// 使用channel.basicAck(envelope.getDeliveryTag(),false);进行消息确认
		channel.basicConsume(QUEUE_NAME, false, consumer);
	}

	/**
	 * @Description: 业务代码
	 */
	private void doWork(String task) {
		for (char ch : task.toCharArray()) {
			if (ch == '.') {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException _ignored) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	// 3、Publish/Subscribe（严格来说路由和通配符模式也是发布订阅）

	// 交换机名称
	private static final String EXCHANGE_NAME = "test_exchange_fanout";

	public void ProducerPublish() throws IOException, TimeoutException {

		Channel channel = mConnection.createChannel();
		/*
		 * 声明exchange交换机 参数1：交换机名称 参数2：交换机类型 参数3：交换机持久性，如果为true则服务器重启时不会丢失
		 * 参数4：交换机在不被使用时是否删除 参数5：交换机的其他属性
		 */
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true, true, null);

		String message = "订阅消息";
		channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
		System.out.println("生产者 send ：" + message);
		channel.close();

	}

	public void ConsumerSubscribe() throws IOException {

		Channel channel = mConnection.createChannel();

		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		/*
		 * 绑定队列到交换机（这个交换机名称一定要和生产者的交换机名相同） 参数1：队列名 参数2：交换机名 参数3：Routing key 路由键
		 */
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

		// 同一时刻服务器只会发一条数据给消费者
		channel.basicQos(1);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				super.handleDelivery(consumerTag, envelope, properties, body);
				String message = new String(body, "UTF-8");
				System.out.println("收到消息：" + message);
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		channel.basicConsume(QUEUE_NAME, false, consumer);
	}

	// 4、Routing（路由）

	public void ProducerRouting() throws Exception {
		// 获取到连接以及mq通道

		Channel channel = mConnection.createChannel();

		// 声明exchange,路由模式声明direct
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");

		// 消息内容
		String message = "这是消息B";
		channel.basicPublish(EXCHANGE_NAME, "B", null, message.getBytes());
		String messageA = "这是消息A";
		channel.basicPublish(EXCHANGE_NAME, "A", null, messageA.getBytes());
		System.out.println(" [生产者] Sent '" + message + "'");

		channel.close();

	}

	public void ConsumerRouting(String[] argv) throws Exception {

		// 获取到连接以及mq通道

		Channel channel = mConnection.createChannel();

		// 声明队列
		// channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		/*
		 * 绑定队列到交换机 参数1：队列的名称 参数2：交换机的名称 参数3：routingKey
		 */
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "A");

		// 同一时刻服务器只会发一条消息给消费者
		channel.basicQos(1);

		// 定义队列的消费者
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				super.handleDelivery(consumerTag, envelope, properties, body);
				System.out.println(new String(body, "UTF-8"));
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}

	// 5、Topics（主题通配符）

	public void ProducerTopics() throws IOException, TimeoutException {

		Channel channel = mConnection.createChannel();
		// 声明交换机
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		String message = "匹配insert";
		channel.basicPublish(EXCHANGE_NAME, "order.update", false, false, null, message.getBytes());

		channel.close();

	}

	public void ConsumerTopics() throws IOException {

		Channel channel = mConnection.createChannel();
		// channel.queueDeclare(QUEUE_NAME,false,false,false,null);
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		// order.#
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "order.*");

		channel.basicQos(1);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				super.handleDelivery(consumerTag, envelope, properties, body);
				System.out.println(new String(body, "UTF-8"));
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);

	}

	// 6、RPC（远程调用）

	private static final String RPC_QUEUE_NAME = "rpc_queue";

	public void RPCServer() throws IOException, TimeoutException {

		final Channel channel = mConnection.createChannel();
		channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
		channel.basicQos(1);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
					byte[] body) throws IOException {
				super.handleDelivery(consumerTag, envelope, properties, body);
				BasicProperties properties1 = new BasicProperties.Builder()
						.correlationId(properties.getCorrelationId()).build();
				String mes = new String(body, "UTF-8");
				int num = Integer.valueOf(mes);
				System.out.println("接收数据：" + num);
				num = SquareFunc(num);
				channel.basicPublish("", properties.getReplyTo(), properties1, String.valueOf(num).getBytes());
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
		while (true) {
			synchronized (consumer) {
				try {
					consumer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static int SquareFunc(int n) {
		return n * n;
	}

	public String RPCClient(String message) throws IOException, TimeoutException, InterruptedException {

		Channel channel = mConnection.createChannel();

		String replyQueueName = channel.queueDeclare().getQueue();

		final String corrId = UUID.randomUUID().toString();

		BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName)
				.build();

		channel.basicPublish("", RPC_QUEUE_NAME, props, message.getBytes("UTF-8"));

		final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

		channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
					byte[] body) throws IOException {
				if (properties.getCorrelationId().equals(corrId)) {
					response.offer(new String(body, "UTF-8"));
				}
			}
		});

		return response.take();
	}

}
