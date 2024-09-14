package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class MemberRepositoryV1Test {

    MemberRepositoryVO repository;

    @BeforeEach
    void setting() throws SQLException {
       // DriverManagerDataSource dataSource
       //         = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryVO();
    }


    @Test
    void crud() throws SQLException {
        Member member = new Member("member0", 10000);
        repository.save(member);
        log.info("member = {}", member);

        Member findMember = repository.findById(member.getMemberId());
        assertThat(findMember).isEqualTo(member);

        repository.update(20000, "member0");
        assertThat(repository.findById("member0").getMoney()).isEqualTo(20000);

        repository.delete("member0");
        assertThatThrownBy(() -> repository.findById("member0"))
                .isInstanceOf(NoSuchElementException.class);


    }


}
