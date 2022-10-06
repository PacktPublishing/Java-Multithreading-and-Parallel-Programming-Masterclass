import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Flowable<String> flow = getSource();

        flow.parallel(4)
                .runOn(Schedulers.computation())
                .map(el -> {
                    System.out.println(el);
                    return el
                })
                .sequential()
                .subscribe(el -> {
            System.out.println(el);
        });
    }

    private static Flowable<String> getSource() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader("./src/main/resources/file.txt"));

        return Flowable.generate(emitter -> {
            String line = reader.readLine();

            if (line != null) {
                emitter.onNext(line);
            } else {
                emitter.onComplete();
            }
        });
    }
}
