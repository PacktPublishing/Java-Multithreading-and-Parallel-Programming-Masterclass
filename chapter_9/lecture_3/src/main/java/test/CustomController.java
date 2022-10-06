package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CustomController {

    @Autowired
    private CustomService service;

    @GetMapping("/hello")
    public String hello() throws InterruptedException, ExecutionException {
        CompletableFuture<Boolean> future = service.backgroundWork();
        return "Hello Back! With response = " + future.get();
    }
}
