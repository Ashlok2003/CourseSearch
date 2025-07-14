package com.ashlok.coursesearch.config;

import java.time.Duration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class BaseElasticsearchTest {

  @SuppressWarnings("resource")
  @Container
  static final ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
      DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.13.4"))
      .withExposedPorts(9200)
      .withEnv("discovery.type", "single-node")
      .withEnv("xpack.security.enabled", "false")
      .withEnv("xpack.security.transport.ssl.enabled", "false")
      .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
      .withStartupAttempts(3)
      .withStartupTimeout(Duration.ofMinutes(2));;

  @DynamicPropertySource
  static void elasticsearchProperties(DynamicPropertyRegistry registry) {
    String uri = String.format("http://%s:%d",
        elasticsearchContainer.getHost(),
        elasticsearchContainer.getMappedPort(9200));
    System.out.println("Elasticsearch Testcontainer URI → " + uri);

    registry.add("spring.elasticsearch.uris", () -> uri);
    registry.add("spring.data.elasticsearch.uris", () -> uri);
  }

}
