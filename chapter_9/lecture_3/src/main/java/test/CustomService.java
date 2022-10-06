package test;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CustomService {

    @Async
    public CompletableFuture<Boolean> backgroundWork() throws InterruptedException {
        Thread.sleep(4000);
        return CompletableFuture.completedFuture(true);
    }
}
