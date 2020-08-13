package cn.xmlly.retuenableoperate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication(scanBasePackages = "cn.xmlly")
@EnableAsync
@ServletComponentScan
@MapperScan(basePackages = "cn.xmlly.returnableoperate.*.dao")
public class RetuenableoperateApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetuenableoperateApplication.class, args);
    }

}
