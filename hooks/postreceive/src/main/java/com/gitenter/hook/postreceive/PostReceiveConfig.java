package com.gitenter.hook.postreceive;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.gitenter.hook.postreceive", "com.gitenter.protease"})
public class PostReceiveConfig {

}
