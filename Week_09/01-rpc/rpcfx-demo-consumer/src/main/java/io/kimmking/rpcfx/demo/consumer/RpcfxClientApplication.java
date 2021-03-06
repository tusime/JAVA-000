package io.kimmking.rpcfx.demo.consumer;

import io.kimmking.rpcfx.client.Rpcfx;
import io.kimmking.rpcfx.client.RpcfxAop;
import io.kimmking.rpcfx.demo.api.Order;
import io.kimmking.rpcfx.demo.api.OrderService;
import io.kimmking.rpcfx.demo.api.User;
import io.kimmking.rpcfx.demo.api.UserService;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcfxClientApplication {

	// 二方库
	// 三方库 lib
	// nexus, userserivce -> userdao -> user

	public static void main(String[] args) {

		// UserService service = new xxx();
		// service.findById

		UserService userService = Rpcfx.create(UserService.class, "http://localhost:8080/");
		User user = userService.findById(1);
		System.out.println("find user id=1 from server: " + user.getName());

		OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8080/");
		Order order = orderService.findOrderById(1992129);
		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));

		System.out.println("====================================================================");

		UserService userService2 = Rpcfx.create(UserService.class, "http://localhost:8080/2");
		User user2 = userService2.findById(1);
		System.out.println("find user id=1 from server: " + user2.getName());

		System.out.println("====================================================================");

		UserService userService3 = RpcfxAop.create(UserService.class, "http://localhost:8080/");
		User user3 = userService3.findById(1);
		System.out.println("find user id=1 from server: " + user3.getName());

		System.out.println("====================================================================");

		UserService userService4 = RpcfxAop.create(UserService.class, "http://localhost:8080/2");
		User user4 = userService4.findById(1);
		System.out.println("find user id=1 from server: " + user4.getName());

		// 新加一个OrderService

//		SpringApplication.run(RpcfxClientApplication.class, args);
	}

}
