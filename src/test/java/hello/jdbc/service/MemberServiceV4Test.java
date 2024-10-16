package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    @Autowired
    private MyRepository memberRepository;
    @Autowired
    private MemberServiceV4_2 memberService;

    @TestConfiguration
    static class TestConfig {

        private final DataSource dataSource;


        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }


            @Bean
            MyRepository memberRepository() {
                //return new MemberRepositoryV4_2(dataSource);
                return new MemberRepositoryV5(dataSource);
            }
            @Bean
            MemberServiceV4_2 memberService() {
                return new MemberServiceV4_2(memberRepository());
            }
    }




    @AfterEach
    void after() {
        memberRepository.delete("A");
        memberRepository.delete("B");
        memberRepository.delete("XXX");
    }



    @Test
    @DisplayName("정상이체")
    public void transferTest(){
        Member member1 = new Member("A", 10000);
        Member member2 = new Member("B", 10000);
        memberRepository.save(member1);
        memberRepository.save(member2);

        memberService.accountTransfer(member1.getMemberId(), member2.getMemberId(), 2000);

        Member findMemberA = memberRepository.findById(member1.getMemberId());
        Member findMemberB = memberRepository.findById(member2.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);

    }

    @Test
    @DisplayName("이체 오류")
    public void transferTest2() {
        Member member1 = new Member("A", 10000);
        Member member2 = new Member("XXX", 10000);
        memberRepository.save(member1);
        memberRepository.save(member2);


        assertThatThrownBy(() -> memberService.accountTransfer("A", "XXX", 2000))
                .isInstanceOf(IllegalStateException.class);

        Member findMemberA = memberRepository.findById(member1.getMemberId());
        Member findMemberB = memberRepository.findById(member2.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}