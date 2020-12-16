package io.kimmking.rpcfx.demo.provider;

import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.client.Rpcfx;
import io.kimmking.rpcfx.demo.api.OrderService;
import io.kimmking.rpcfx.demo.api.UserService;
import io.kimmking.rpcfx.server.RpcfxInvoker;
import io.kimmking.rpcfx.server.RpcfxReflectInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RpcfxServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpcfxServerApplication.class, args);
	}

	@Autowired
	RpcfxInvoker invoker;

	// 入口
	@PostMapping("/")
	public RpcfxResponse invoke(@RequestBody RpcfxRequest request) {
		return invoker.invoke(request);
	}

	@Bean
	public RpcfxInvoker createInvoker(@Autowired RpcfxResolver resolver){
		return new RpcfxInvoker(resolver);
	}

	@Bean
	public RpcfxResolver createResolver(){
		return new DemoResolver();
	}

	// 能否去掉name？可以，根据类型获取bean
//	Class klass = Class.forName(serviceClass);
//	this.applicationContext.getBean(klass);
	@Bean(name = "io.kimmking.rpcfx.demo.api.UserService")
	public UserService createUserService(){
		return new UserServiceImpl();
	}

	@Bean(name = "io.kimmking.rpcfx.demo.api.OrderService")
	public OrderService createOrderService(){
		return new OrderServiceImpl();
	}


	@Autowired
	RpcfxReflectInvoker rpcfxReflectInvoker;
	// 入口2
	@PostMapping("/2")
	public RpcfxResponse invoke2(@RequestBody RpcfxRequest request) {
		return rpcfxReflectInvoker.invoke(request);
	}
	@Bean()
	public RpcfxReflectInvoker createInvoker2(@Autowired RpcfxResolver resolver){
		return new RpcfxReflectInvoker(resolver);
	}

}
