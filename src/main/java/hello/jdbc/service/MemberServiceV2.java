package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepositoryV2;
    private final DataSource dataSource;

    public void transfer(String fromId, String toId, int transferMoney) throws SQLException {


        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false);

            bizLogic(fromId, toId, transferMoney, con);

            con.commit();
        }
        catch (Exception e){
            con.rollback();
            throw new IllegalStateException(e);
        }

        finally {
            release(con);
        }



    }

    private void bizLogic(String fromId, String toId, int transferMoney, Connection con) throws SQLException {
        Member fromMember = memberRepositoryV2.findById(con, fromId);
        Member toMember = memberRepositoryV2.findById(con, toId);

        memberRepositoryV2.update(con, (fromMember.getMoney() - transferMoney), fromId);
        detect(toMember);
        memberRepositoryV2.update(con, (toMember.getMoney() - transferMoney), toId);
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
