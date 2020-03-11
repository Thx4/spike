package com.donggugu.spike;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableTransactionManagement
@EnableKafka
@SpringBootApplication
public class SpikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpikeApplication.class, args);
/*        new SpringApplicationBuilder(SpikeApplication.class).
                listeners(new ApplicationPidFileWriter())
                .run(args);
        Consumer consumer = SpringBeanFactory.getBean(Consumer.class);
        new Thread(consumer, "消费者").start();
        log.info("消费者线程启动成功!");*/
    }

}
