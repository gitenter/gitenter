package com.gitenter.post_receive_hook;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.gitenter.post_receive_hook", "com.gitenter.protease"})
public class PostReceiveConfig {

}
