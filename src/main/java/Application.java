import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableAsync
@ComponentScan(basePackages = {"com.zhq.executor"})
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
//    new SpringApplicationBuilder(Application.class).web(true).run(args);
    SpringApplication.run(Application.class, args);
  }

}