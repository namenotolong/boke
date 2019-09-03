package com.hy.boke;

import com.hy.boke.utils.ApplicationUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableCaching
public class BokeApplication {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public ApplicationUtil applicationUtil() {
        return new ApplicationUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(BokeApplication.class, args);
    }

}
