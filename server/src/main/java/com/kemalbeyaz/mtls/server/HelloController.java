package com.kemalbeyaz.mtls.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kemal Beyaz
 * @date 3.12.2024
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Merhaba, güvenli bir bağlantıdasınız!";
    }
}
