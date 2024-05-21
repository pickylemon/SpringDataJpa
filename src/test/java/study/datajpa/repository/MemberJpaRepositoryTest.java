package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberJpaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest //JUnit5부터는 @RunWith등의 어노테이션 생략 가능
@Transactional
@Rollback(false)
//테스트에 건 @Transactional은 자동으로 rollback되고
//영속성 컨텍스트의 쿼리도 flush 안되어 DB에 반영이 안된다.
//테스트 확인을 위해 rollback false 설정
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("member테스트")
    void memberTest() {
        //given
        Member member = new Member("memberA");
        memberJpaRepository.save(member);
        //when
        Member findMember = memberJpaRepository.find(member.getId());
        //then
        //같은 Tx내이기때문에 영속성 컨텍스트에서 가져와서 같다.(동일성 보장)
        assertThat(findMember).isSameAs(member);
    }

    @Test
    @DisplayName("Basic CRUD")
    public void CRUDtest(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        //단건 조회 검사
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검사
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);
        assertThat(all).contains(member1, member2);

        //count 검사
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //update 검사(변경 감지, dirty checking)
        member1.setUsername("member3");
        em.flush();

        //삭제 검사
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        List<Member> afterDelete = memberJpaRepository.findAll();
        assertThat(afterDelete.size()).isEqualTo(0);
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void findByUsernameAndGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsernameAndGreaterThan("AAA", 15);

        Assertions.assertThat(result).size().isEqualTo(1);
        Assertions.assertThat(result.get(0)).isEqualTo(m2);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> byUsername = memberJpaRepository.findByUsername("AAA");
        Assertions.assertThat(byUsername).size().isEqualTo(2);
        Assertions.assertThat(byUsername).contains(m1, m2);
    }
}