package com.skillswap.skillswap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@Configuration
// -------------------- Used for timestamps like created at, updated at --------------------
@EnableJpaAuditing
public class JpaConfig {
}
