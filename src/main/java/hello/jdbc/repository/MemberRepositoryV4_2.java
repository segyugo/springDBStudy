package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV4_2 implements MyRepository{

    private final DataSource dataSource;

    private final SQLExceptionTranslator exceptionTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw exceptionTranslator.translate("save", sql, e);
        }
        finally {
            close(con, pstmt, null);
        }
    }



    @Override
    public Member findById(String memberId){
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if (rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));

                return member;
            }

            else {
                throw new NoSuchElementException("member not found");
            }

        } catch (SQLException e) {
            throw exceptionTranslator.translate("findById", sql, e);
        }

        finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public void update(int money, String memberId) {
        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int result = pstmt.executeUpdate();
            log.info("result = {}", result);

        } catch (SQLException e) {
            throw exceptionTranslator.translate("update", sql, e);
        }
        finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            int result = pstmt.executeUpdate();
            log.info("result = {}", result);

        }  catch (SQLException e) {
            throw exceptionTranslator.translate("delete", sql, e);
    }
        finally {
            close(con, pstmt, null);
        }
    }


    private Connection getConnection() throws SQLException {
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("connection = {} ,class = {}", con, con.getClass());
        return con;
    }


    private void close(Connection con, Statement stmt, ResultSet rs){

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, dataSource);
    }

}

