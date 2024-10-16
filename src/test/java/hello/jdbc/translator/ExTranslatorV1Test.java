package hello.jdbc.translator;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MySqlException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {


    Service service;
    Repository repository;

    @BeforeEach
    void init() {
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void DT()  {
        service.create("aaa");
        service.create("aaa");
    }



    @RequiredArgsConstructor
    static class Service{
        private final Repository repository;

        public void create(String memberId) {

            try {
                Member member = new Member(memberId, 0);
                repository.save(member);
            }
            catch (MyDuplicateKeyException e){
                String newId =  generateNewId(memberId);
                log.info("아이디를 {}로 변경", newId);
                Member member = new Member(newId, 0);
                repository.save(member);
            }
            catch (MySqlException e){
                log.info("데이터 접근 계층 오류");
                throw e;
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }
        }


    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id, money) values(?, ?)";

            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);

                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;

            } catch (SQLException e) {
                log.info("SQL 오류 발생");
                if (e.getErrorCode() == 23505) {
                    log.info("중복 오류 발생");
                    throw new MyDuplicateKeyException(e);
                } else {
                    log.info("다른 오류 발생");
                    throw new MySqlException(e);
                }
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }}

    static class MyDuplicateKeyException extends RuntimeException{
        public MyDuplicateKeyException() {
        }

        public MyDuplicateKeyException(String message) {
            super(message);
        }

        public MyDuplicateKeyException(String message, Throwable cause) {
            super(message, cause);
        }

        public MyDuplicateKeyException(Throwable cause) {
            super(cause);
        }
    }


    }













