package org.onelab.config;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "org.onelab")
@EnableAspectJAutoProxy
public class AppConfig {
}
