package cn.iocoder.yudao.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智造营销 SaaS 启动类（广东超悦科技有限公司）。
 * 包扫描仍使用 yudao.info.base-package，配置前缀迁移见 BASELINE.md。
 */
@SuppressWarnings("SpringComponentScan")
@SpringBootApplication(scanBasePackages = {"${yudao.info.base-package}.server", "${yudao.info.base-package}.module"})
public class ChaoyueServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChaoyueServerApplication.class, args);
    }
}
