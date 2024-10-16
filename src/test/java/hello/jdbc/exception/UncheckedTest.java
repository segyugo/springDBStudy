package hello.jdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    static class MyUncheckedException extends RuntimeException{

        public MyUncheckedException(String message) {
            super(message);
        }
    }

    static class Repository {
        public void call() {
            throw new MyUncheckedException("예외");
        }
    }

    static class Service {

        Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            }
            catch (Exception e){
                log.info("예외 발생 {}", e.getMessage(), e);
            }

        }

        public void callThrows() {
            repository.call();
        }
    }

    @Test
    void A() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void B() {
        Service service = new Service();

        Assertions.assertThatThrownBy(() -> service.callThrows())
                .isInstanceOf(MyUncheckedException.class);

    }

}
