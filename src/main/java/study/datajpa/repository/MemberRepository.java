package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

/*
SpringDataJpa 사용하기
 */

@Repository //어노테이션 생략 가능
public interface MemberRepository extends JpaRepository<Member, Long> {
}
