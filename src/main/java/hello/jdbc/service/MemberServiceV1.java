package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepositoryV1;

    public void transfer(String fromId, String toId, int transferMoney) throws SQLException {

        Member fromMember = memberRepositoryV1.findById(fromId);
        Member toMember = memberRepositoryV1.findById(toId);

        memberRepositoryV1.update((fromMember.getMoney() - transferMoney), fromId);
        detect(toMember);
        memberRepositoryV1.update((toMember.getMoney() - transferMoney), toId);
    }

    private static void detect(Member member) {
        if (member.getMemberId().equals("XXX")){
            throw new IllegalStateException();
        }
    }
}
