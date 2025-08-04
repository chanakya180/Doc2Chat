package com.chanakya.doc2chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsConfigurationProps {

  private String allowedOrigins;
  private String allowedMethods;
  private String allowedHeaders;
  private Boolean allowCredentials;
  private Long maxAge;
}
