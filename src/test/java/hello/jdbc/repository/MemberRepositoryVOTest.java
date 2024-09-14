package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;


@Slf4j
class MemberRepositoryVOTest {

    private MemberRepositoryVO repository = new MemberRepositoryVO();


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