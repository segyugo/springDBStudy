package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV4;
import hello.jdbc.repository.MemberRepositoryV4_2;
import hello.jdbc.repository.MyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * 트랜잭션 - @Transactional AOP
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4_2 {

    private final MyRepository memberRepository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromMember.getMoney() - money, fromId);
        validation(toMember);
        memberRepository.update(toMember.getMoney() + money, toId);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("XXX")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}