package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnCheckedException {

    static Logger log = LoggerFactory.getLogger(UnCheckedException.class);

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrow).isInstanceOf(MyUnCheckedException.class);
    }

    /**
     * RuntimeException을 상속받으면 Unchecked Exception
     */
    static class MyUnCheckedException extends RuntimeException {
        public MyUnCheckedException(String message) {
            super(message);
        }
    }

    /**
     * UnChecked 예외 처리는 해도 되고 안해도 된다.
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 필요한 경우 잡아도 됨
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUnCheckedException e) {
                log.error("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 잡지 않아도 자연스럽게 상위로 넘어간다.
         * Throws를 처리하지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        // throws를 사용 안해도 된다.
        public void call() {
            throw new MyUnCheckedException("ex");
        }
    }
}
