package com.hellozjf.project.shadowsocks.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 通过 http://server:port/swagger-ui.html 访问swagger页面
 *
 * @author zhaozw
 * @date 2019/9/29
 * @company 安人股份
 * @desc
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 令牌请求头属性名
     */
    @Value("${jwt.header:Authorization}")
    private String tokenHeader;

    @Bean
    public Docket api() {
//        ParameterBuilder ticketPar = new ParameterBuilder();
//        List<Parameter> pars = new ArrayList<Parameter>();
//        ticketPar.name(tokenHeader)
//                .description("token")
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .defaultValue("Bearer ")
//                .required(true)
//                .build();
//        pars.add(ticketPar.build());
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .pathMapping("/").select().apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .paths(Predicates.not(PathSelectors.regex("/actuator.*")))
                .paths(PathSelectors.regex("/.*"))
                .build()
//                .globalOperationParameters(pars)
                ;
    }

    /**
     * 文档基本信息
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("shadowsocks")
                .description("java版shadowsocks实现")
                .termsOfServiceUrl("https://www.cnblogs.com/hellozjf")
                .version("v1.0")
                .build();
    }

}
