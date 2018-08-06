package com.emailtohl.hjk.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 基于STOMP代理的消息订阅
 * 
 * @author HeLei
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {
	/**
	 * 注册stomp的端点
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 访问点为webSocketServer，允许使用socketJs方式来模拟websocket，允许跨域访问
		// 前端则需要通过“/stomp”地址创建SockJS连接
		registry.addEndpoint("/stomp").setAllowedOrigins("*").withSockJS();
	}

	/**
	 * 配置信息代理
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/queue", "/topic");
		registry.setApplicationDestinationPrefixes("/socket");
	}

}
