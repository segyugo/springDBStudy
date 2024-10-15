package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3 {

    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepositoryV3;

    public void transfer(String fromId, String toId, int transferMoney) throws SQLException {

        TransactionStatus transactionStatus =  transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {

            bizLogic(fromId, toId, transferMoney);
            transactionManager.commit(transactionStatus);

        }
        catch (Exception e){
            transactionManager.rollback(transactionStatus);
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int transferMoney) throws SQLException {
        Member fromMember = memberRepositoryV3.findById(fromId);
        Member toMember = memberRepositoryV3.findById(toId);

        memberRepositoryV3.update((fromMember.getMoney() - transferMoney), fromId);
        detect(toMember);
        memberRepositoryV3.update((toMember.getMoney() - transferMoney), toId);
    }

    private static void release(Connection con) {
        if (con != null){
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private static void detect(Member member) {
        if (member.getMemberId().equals("XXX")){
            throw new IllegalStateException();
        }
    }
}
