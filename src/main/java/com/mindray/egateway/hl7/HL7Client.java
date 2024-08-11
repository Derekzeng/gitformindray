package com.mindray.egateway.hl7;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class HL7Client {
	private static final Logger logger = LoggerFactory.getLogger(HL7Client.class);
	private final String charset;
	private IoConnector connector;
	private IoSession session = null;
	private boolean enabled = true;

	public HL7Client() {
		this.charset = "UTF-8";
	}

	public void connect(String host, int port) {
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(2000);
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("hl7codec", new ProtocolCodecFilter(new HL7CodeFactory(this.charset)));
		connector.getFilterChain().addLast("reconnection", new IoFilterAdapter() {
			@Override
			public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
				asynConnect(host, port);
			}
		});
		connector.getSessionConfig().setReadBufferSize(1024 * 1024);
		connector.setHandler(new HL7ClientHandler());

		asynConnect(host, port);
	}
	
	private void asynConnect(String host, int port) {
		new Thread(new Runnable() {
			@Override
			public void run() {				
				tryConnect(host, port);
			}
		}).start();
	}

	private void tryConnect(String host, int port) {
		while (enabled ) {
			if(session != null && session.isConnected()) {
				return;
			}
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
				future.awaitUninterruptibly();
				session = future.getSession();
				if (session.isConnected()) {
					logger.info("HL7Client connection to {}", session.getRemoteAddress());
				}
			} catch (Exception e) {
				logger.info("try connect to {}:{}", host, port);
			}

			try {
				Thread.sleep(1000);
			} catch (Exception ignored) {
			}
		}
	}

	public void disconnect() {
		enabled = false;
		if (session != null) {
			session.closeNow();
			session.getService().dispose(false);
		}

		if (connector != null) {
			connector.dispose();
		}

	}

	public void send(String hl7message) {
		if(session != null) {
			session.write(hl7message);
		}
	}

}
