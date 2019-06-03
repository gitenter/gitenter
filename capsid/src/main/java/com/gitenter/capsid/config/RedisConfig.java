package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
public class RedisConfig {

	@Profile("sts")
	@Bean
	public RedisConnectionFactory stsConfiguration() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("localhost", 6379);
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
}
