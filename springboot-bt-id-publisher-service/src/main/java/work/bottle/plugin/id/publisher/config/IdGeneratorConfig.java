package work.bottle.plugin.id.publisher.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.bottle.plugin.HighSwallowIdService;
import work.bottle.plugin.IdService;
import work.bottle.plugin.id.publisher.utils.HostUtils;

@Configuration
public class IdGeneratorConfig {
    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorConfig.class);
    public static boolean serverActive = false;

    @Bean
    public IdService idService(IdPublisherClusterProperties properties) {

        // 发送HTTP 请求, 请求数据, 确保服务可用
        // return new HighSwallowIdService(properties.getMachineId());
        logger.info("配置为: {}", properties.getCluster().getServices());
        int machineId = -1;
        int n = 0;
        HostUtils.getHostNameList().forEach(s -> {
            logger.info("HostName: {}", s);
        });
        for (IdPublisherClusterProperties.Service service : properties.getCluster().getServices()) {
            if (HostUtils.getHostNameList().contains(service.getHost())) {
                machineId = service.getMachineId();
                n++;
            }
        }
        if (-1 == machineId) {
            throw new RuntimeException("MachineId configuration failed, no valid configuration");
        }
        if (1 < n) {
            throw new RuntimeException("MachineId configuration failed, There are multiple configuration conflicts");
        }
        if (0 > machineId || 127 < machineId) {
            throw new RuntimeException("MachineId configuration failed, The machine ID is out of bounds, it can only take the value 1 - 127");
        }
        logger.info("Loaded configuration, machineId: {}", machineId);
        return new HighSwallowIdService(machineId);
    }
}
