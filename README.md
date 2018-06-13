# # hystrix-demo

主要希望通过本demo来介绍[Hystrix](https://github.com/Netflix/Hystrix/wiki)的使用, 在我看来, 这个类库可能是当前最为重要的类库之一

不管是其引入的断路器的概念还是其使用, 都是一个现代技术堆栈中必须的, 也是当下程序员需要熟练掌握的技术.


## Hystrix  介绍

简单来说, hystrix提供了一个简单且优雅的方案来让你的系统实现自动降级和恢复.


以环信客服系统为例, 有两个典型的场景, *排队*和*调度*


当有访客请求坐席服务的时候, 会根据访客请求的服务类型不同, 把访客放到某一个服务队列中进行排队.

每个服务队列都对应了一个技能组(为了讨论方便进行了简化), 每个技能组又包括了一个到多个坐席人员.

当服务队列中排队的人数增加的时候( 例如有新的访客到达了 ), 系统会通知在这个技能组中的所有的坐席人员(通过websocket或者app端的长连接), 坐席工作台收到这个通知之后, 会调用系统的 `/count`接口来请求当前这个服务队列中等待服务的访客人数, 从而在界面上显示给坐席人员看到当前的队列长度.


当坐席人员有能力接待的时候, 系统又会根据不同的调度算法, 从当前的排队队列中, 选择一个等待的访客分配给这个坐席, 从而会把这个访客从服务队列中移除掉, 进而又会通知到坐席工作台, 坐席工作台又会请求`/count`接口来刷新当前的待服务人数.

所以, 假设只有一个服务队列( 即一个技能组), 这个服务队列每秒有100个访客到达, 这个技能组有50个坐席在线提供服务, 那么可知, 每秒100服务队列变化的通知发送到这50个坐席的工作台, 然后这50个坐席工作台会发出 `50*100`, 即 *5000* 个对`/count`的请求.


(当然实际情况并不是如此, 例如, 前端可以做throttle限流, 每秒发出一个相同的请求即可, 因为即使每秒发出50个, 那么变化的数字太快也没有意义, 这里为了demo讲解做了简化)


假设所有数据都是存放在一个数据库中的, 从上面的描述, 我们可以看到对数据库的操作分为:

* 写入服务队列
* 从服务队列删除
* 查询队列长度
* 调度(由于调度算法复杂, 会涉及比较复杂数据库查询)

另外, 从业务的角度, 我们还知道如下的信息

* 系统的核心是入队, 出队和调度
* 查询服务队列长度为非核心功能

即, 如果系统压力大, 优先需要保障业务进行, 查看待服务人数的功能可以放弃




## Hystrix Dashboard

![dashboard](./doc/images/dashboard.jpg)

Hystrix还提供了一个非常精致美观的[dashboard](https://github.com/Netflix-Skunkworks/hystrix-dashboard),可以让我们用来观察每个hystrix command的执行情况, 具体的界面的信息可以参考上面链接中的介绍

并且, spring cloud还提供了对hystrix dashboard的支持, 只需要在pom文件中加上下面这两个依赖

```

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
		</dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

```


并且加上`@EnableHystrixDashboard`这个annotation就可以看到了

```
@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
public class HystrixDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixDemoApplication.class, args);
    }
}

```

然后可以访问`http://127.0.0.1:8080/hystrix`, 在打开的界面上填写上`http://127.0.0.1:8080/hystrix.stream`就可以看到精美的dashboard了

需要注意的是, 如果使用上面的方法, 那么只能在dashboard中看到本机这个单个实例的hystrix command执行状况, 对于生产环境上, 部署多个实例的时候, 意义就
不那么大了, 这个时候, 我们需要使用[Netflix Trubin](https://cloud.spring.io/spring-cloud-static/Edgware.SR3/single/spring-cloud.html#_turbine)
先来聚合这些hystrix command的数据, 并在上面dashboard的地址中填写trubin的地址就可以了.

详细信息可以参考[spring cloud文档](https://cloud.spring.io/spring-cloud-static/Edgware.SR3/single/spring-cloud.html#_circuit_breaker_hystrix_dashboard)
