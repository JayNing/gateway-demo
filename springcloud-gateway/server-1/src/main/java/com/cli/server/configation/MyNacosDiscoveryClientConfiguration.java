package com.cli.server.configation;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.alibaba.nacos.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * desc: 自定义nacos元数据---nacos进行服务注册时，添加自定义元数据
 * createBy: Ningjianjian
 */
@Slf4j
@Configuration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({NacosDiscoveryClientConfiguration.class})
public class MyNacosDiscoveryClientConfiguration {

    @Value("${application.tag}")
    private String applicationTag;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "spring.cloud.nacos.discovery.watch.enabled",
            matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosDiscoveryProperties, ObjectProvider<ThreadPoolTaskScheduler> taskExecutorObjectProvider) {
        //原来的元数据全部不变
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        if(metadata==null){
            metadata = new HashMap<>();
            nacosDiscoveryProperties.setMetadata(metadata);
        }
        if (StringUtils.isNotBlank(applicationTag)) {
            metadata.put("userTag", applicationTag);
        }
        return new NacosWatch(nacosServiceManager,nacosDiscoveryProperties, taskExecutorObjectProvider);
    }
}
