package study.datajpa.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

/*
SpringDataJpa 사용하기
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
