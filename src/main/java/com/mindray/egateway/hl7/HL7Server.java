package com.mindray.egateway.hl7;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HL7Server {
	private static final Logger logger = LoggerFactory.getLogger(HL7Server.class);
	private final int port;
    private final IoAcceptor acceptor;

	public HL7Server(int port, String charset) {
		this.port = port;
        acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("hl7codec", new ProtocolCodecFilter(new HL7CodeFactory(charset)));
		acceptor.getSessionConfig().setReadBufferSize(1024 * 1024 * 50);
		acceptor.setHandler(new HL7ServerHandler());
	}

	public void start() throws IOException {
		acceptor.bind(new InetSocketAddress(port));
		logger.info("[mindray info]HL7Server started.port:{}",port);
	}

	public void stop() {
		if (null != acceptor) {
			acceptor.unbind();
			logger.info("HL7Server stopped.");
		}
	}

}
