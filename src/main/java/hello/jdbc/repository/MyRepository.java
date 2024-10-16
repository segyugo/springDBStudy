package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.*;
import java.util.NoSuchElementException;

public interface MyRepository {
    public Member save(Member member);
    public Member findById(String memberId);
    public void update(int money, String memberId);
    public void delete(String memberId);
}
