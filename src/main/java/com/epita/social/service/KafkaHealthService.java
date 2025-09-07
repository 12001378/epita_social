package com.epita.social.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaHealthService {

    private final KafkaAdmin kafkaAdmin;

    public boolean isKafkaHealthy() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeClusterResult clusterResult = adminClient.describeCluster();
            
            // Try to get cluster info with a timeout
            clusterResult.nodes().get(5, TimeUnit.SECONDS);
            log.info("Kafka cluster is healthy");
            return true;
            
        } catch (Exception e) {
            log.error("Kafka cluster health check failed", e);
            return false;
        }
    }

    public String getKafkaClusterInfo() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeClusterResult clusterResult = adminClient.describeCluster();
            
            String clusterId = clusterResult.clusterId().get(5, TimeUnit.SECONDS);
            int nodeCount = clusterResult.nodes().get(5, TimeUnit.SECONDS).size();
            
            return String.format("Cluster ID: %s, Nodes: %d", clusterId, nodeCount);
            
        } catch (Exception e) {
            log.error("Failed to get Kafka cluster info", e);
            return "Kafka cluster information unavailable";
        }
    }
}
