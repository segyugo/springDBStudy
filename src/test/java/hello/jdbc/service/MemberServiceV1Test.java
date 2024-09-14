package hello.jdbc.service;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceV1Test {

    private MemberRepositoryV1 repository;
    private MemberServiceV1 service;

    @BeforeEach
    void init() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new MemberRepositoryV1(driverManagerDataSource);
        service = new MemberServiceV1(repository);
    }

    @AfterEach
    void after() throws SQLException {
        repository.delete("A");
        repository.delete("B");
        repository.delete("XXX");
    }

    @Test
    @DisplayName("정상이체")
    public void transferTest() throws SQLException {
        Member member1 = new Member("A", 10000);
        Member member2 = new Member("B", 20000);
        repository.save(member1);
        repository.save(member2);

        service.transfer("A", "B", 2000);

        assertThat(repository.findById("A").getMoney()).isEqualTo(8000);

    }

    @Test
    @DisplayName("이체 오류")
    public void transferTest2() throws SQLException {
        Member member1 = new Member("A", 10000);
        Member member2 = new Member("XXX", 20000);
        repository.save(member1);
        repository.save(member2);


        assertThatThrownBy(() -> service.transfer("A", "XXX", 2000))
                .isInstanceOf(IllegalStateException.class);
        assertThat(repository.findById("A").getMoney()).isEqualTo(8000);
        assertThat(repository.findById("XXX").getMoney()).isEqualTo(20000);

    }
}