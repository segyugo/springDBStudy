package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceV3_2Test {

    private MemberRepositoryV3 repository;
    private MemberServiceV3_2 service;

    @BeforeEach
    void init() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

        repository = new MemberRepositoryV3(dataSource);
        service = new MemberServiceV3_2(transactionManager, repository);
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

        service.transfer(member1.getMemberId(), member2.getMemberId(), 2000);

        Member findMemberA = repository.findById(member1.getMemberId());
        Member findMemberB = repository.findById(member2.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);

    }

    @Test
    @DisplayName("이체 오류")
    public void transferTest2() throws SQLException {
        Member member1 = new Member("A", 10000);
        Member member2 = new Member("XXX", 20000);
        repository.save(member1);
        repository.save(member2);


        Member findMemberA = repository.findById(member1.getMemberId());
        Member findMemberB = repository.findById(member2.getMemberId());

        assertThatThrownBy(() -> service.transfer("A", "XXX", 2000))
                .isInstanceOf(IllegalStateException.class);
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(20000);

    }
}