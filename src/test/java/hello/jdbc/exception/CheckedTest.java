package hello.jdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    static class MyCheckedException extends Exception{
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Repository{
        public void call() throws MyCheckedException {
            throw new MyCheckedException("오류");
        }
    }

    static class Service {
        Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("오류 발생 = {}", e.getMessage(), e);
            }
        }

        public void callThrows() throws MyCheckedException {
            repository.call();
        }
    }

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throws() throws MyCheckedException  {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrows())
                .isInstanceOf(MyCheckedException.class);

    }


}
