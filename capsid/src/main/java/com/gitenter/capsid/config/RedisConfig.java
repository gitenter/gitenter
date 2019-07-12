package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

@Configuration
public class RedisConfig {

	@Profile("sts")
	@Bean
	public RedisConnectionFactory stsConfiguration() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("localhost", 7200);
		return new JedisConnectionFactory(configuration);
	}

	@Profile("docker")
	@Bean
	public RedisConnectionFactory dockerConfiguration() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("redis-session", 6379);
		return new JedisConnectionFactory(configuration);
	}

	@Profile("qa")
	@Bean
	public RedisConnectionFactory qaConfiguration() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("qa-redis-session.vf1dmm.ng.0001.use1.cache.amazonaws.com", 6379);
		return new JedisConnectionFactory(configuration);
	}

	@Profile("production")
	@Bean
	public RedisConnectionFactory productionConfiguration() {
		/*
		 * TODO:
		 * Setup password.
		 */
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("localhost", 6379);
		return new JedisConnectionFactory(configuration);
	}

	/*
	 * The below setup is to fix the secured Redis error which appears in AWS but not locally. It will 
	 * cause ELB to keep restart and cannot pass the health_check endpoint (returns 503/404).
	 * https://github.com/spring-projects/spring-session/issues/124
	 * https://github.com/spring-projects/spring-session/issues/113

	Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'enableRedisKeyspaceNotificationsInitializer' defined in class path resource [org/springframework/boot/autoconfigure/session/RedisSessionConfiguration$SpringBootRedisHttpSessionConfiguration.class]: Invocation of init method failed; nested exception is java.lang.IllegalStateException: Unable to configure Redis to keyspace notifications. See http://docs.spring.io/spring-session/docs/current/reference/html5/#api-redisoperationssessionrepository-sessiondestroyedevent
		at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1745)
		at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:576)
		at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:498)
		at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:320)
		at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:222)
		at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:318)
		at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)
		at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:846)
		at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:863)
		at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:546)
		at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:140)
		at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:775)
		at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397)
		at org.springframework.boot.SpringApplication.run(SpringApplication.java:316)
		at org.springframework.boot.web.servlet.support.SpringBootServletInitializer.run(SpringBootServletInitializer.java:157)
		at org.springframework.boot.web.servlet.support.SpringBootServletInitializer.createRootApplicationContext(SpringBootServletInitializer.java:137)
		at org.springframework.boot.web.servlet.support.SpringBootServletInitializer.onStartup(SpringBootServletInitializer.java:91)
		at org.springframework.web.SpringServletContainerInitializer.onStartup(SpringServletContainerInitializer.java:171)
		at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5225)
		at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:150)
		... 10 more
	Caused by: java.lang.IllegalStateException: Unable to configure Redis to keyspace notifications. See http://docs.spring.io/spring-session/docs/current/reference/html5/#api-redisoperationssessionrepository-sessiondestroyedevent
		at org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction.getNotifyOptions(ConfigureNotifyKeyspaceEventsAction.java:83)
		at org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction.configure(ConfigureNotifyKeyspaceEventsAction.java:57)
		at org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration$EnableRedisKeyspaceNotificationsInitializer.afterPropertiesSet(RedisHttpSessionConfiguration.java:304)
		at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1804)
		at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1741)
		... 29 more
	Caused by: org.springframework.dao.InvalidDataAccessApiUsageException: ERR unknown command `CONFIG`, with args beginning with: `get`, `notify-keyspace-events`, ; nested exception is redis.clients.jedis.exceptions.JedisDataException: ERR unknown command `CONFIG`, with args beginning with: `get`, `notify-keyspace-events`,
		at org.springframework.data.redis.connection.jedis.JedisExceptionConverter.convert(JedisExceptionConverter.java:64)
		at org.springframework.data.redis.connection.jedis.JedisExceptionConverter.convert(JedisExceptionConverter.java:41)
		at org.springframework.data.redis.PassThroughExceptionTranslationStrategy.translate(PassThroughExceptionTranslationStrategy.java:44)
		at org.springframework.data.redis.FallbackExceptionTranslationStrategy.translate(FallbackExceptionTranslationStrategy.java:42)
		at org.springframework.data.redis.connection.jedis.JedisConnection.convertJedisAccessException(JedisConnection.java:142)
		at org.springframework.data.redis.connection.jedis.JedisServerCommands.convertJedisAccessException(JedisServerCommands.java:531)
		at org.springframework.data.redis.connection.jedis.JedisServerCommands.getConfig(JedisServerCommands.java:299)
		at org.springframework.data.redis.connection.DefaultedRedisConnection.getConfig(DefaultedRedisConnection.java:1197)
		at org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction.getNotifyOptions(ConfigureNotifyKeyspaceEventsAction.java:76)
		... 33 more
	Caused by: redis.clients.jedis.exceptions.JedisDataException: ERR unknown command `CONFIG`, with args beginning with: `get`, `notify-keyspace-events`,
		at redis.clients.jedis.Protocol.processError(Protocol.java:127)
		at redis.clients.jedis.Protocol.process(Protocol.java:161)
		at redis.clients.jedis.Protocol.read(Protocol.java:215)
		at redis.clients.jedis.Connection.readProtocolWithCheckingBroken(Connection.java:340)
		at redis.clients.jedis.Connection.getBinaryMultiBulkReply(Connection.java:276)
		at redis.clients.jedis.Connection.getMultiBulkReply(Connection.java:269)
		at redis.clients.jedis.Jedis.configGet(Jedis.java:2630)
		at org.springframework.data.redis.connection.jedis.JedisServerCommands.getConfig(JedisServerCommands.java:297)
		... 35 more
	 */
	@Bean
	public static ConfigureRedisAction configureRedisAction() {
	    return ConfigureRedisAction.NO_OP;
	}
}
