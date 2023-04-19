package com.cli.gateway.config;

import com.cli.gateway.filter.MyRoundRobinLoadBalancer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * desc:
 * createBy: Ningjianjian
 */
// 特别注意此配置类不要加@Configuration
public class MyLoadBalancerConfig {

    @Bean
    @ConditionalOnMissingBean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(Environment environment,
                                                                                   LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new MyRoundRobinLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }

//    @Bean
//    @ConditionalOnBean(LoadBalancerClientFactory.class)
//    @ConditionalOnMissingBean(MyReactiveLoadBalancerClientFilter.class)
//    @ConditionalOnEnabledGlobalFilter
//    public MyReactiveLoadBalancerClientFilter gatewayLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory,
//                                                                            GatewayLoadBalancerProperties properties, LoadBalancerProperties loadBalancerProperties) {
//        return new MyReactiveLoadBalancerClientFilter(clientFactory, properties, loadBalancerProperties);
//    }

}
