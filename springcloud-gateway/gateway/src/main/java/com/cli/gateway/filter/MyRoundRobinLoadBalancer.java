package com.cli.gateway.filter;

import com.alibaba.nacos.common.utils.StringUtils;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * desc:
 * createBy: Ningjianjian
 */
public class MyRoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer{

    private static final Log log = LogFactory.getLog(MyRoundRobinLoadBalancer.class);

    final AtomicInteger position;

    final String serviceId;

    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     */
    public MyRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                  String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     * @param seedPosition Round Robin element position marker
     */
    public MyRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                  String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(seedPosition);
    }

    @SuppressWarnings("rawtypes")
    @Override
    // see original
    // https://github.com/Netflix/ocelli/blob/master/ocelli-core/
    // src/main/java/netflix/ocelli/loadbalancer/RoundRobinLoadBalancer.java
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        RequestDataContext context = (RequestDataContext) request.getContext();
        String cookie = context.getClientRequest().getHeaders().getFirst("Cookie");
        List<String> userTagList = new ArrayList<>(1);
        if (StringUtils.isNotBlank(cookie)) {
            String[] split = cookie.split(";");
            for (String s : split) {
                if (s.startsWith("userTag=")) {
                    String replace = s.replace("userTag=", "");
                    userTagList.add(replace);
                    break;
                }
            }
        }
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, userTagList.get(0)));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances, String userTag) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances,userTag);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, String userTag) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }
        //根据userTag进行负载均衡
        for (ServiceInstance instance : instances) {
            String userTag1 = instance.getMetadata().get("userTag");
            if (StringUtils.isNotBlank(userTag1) && userTag1.equals(userTag)) {
                return new DefaultResponse(instance);
            }
        }
        //根据原有逻辑进行负载均衡
        // TODO: enforce order?
        int pos = Math.abs(this.position.incrementAndGet());

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }
}
