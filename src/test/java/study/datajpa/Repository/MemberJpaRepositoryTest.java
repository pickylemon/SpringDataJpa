package study.datajpa.Repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //JUnit5부터는 @RunWith등의 어노테이션 생략 가능
@Transactional
@Rollback(false)
//테스트에 건 @Transactional은 자동으로 rollback되고
//영속성 컨텍스트의 쿼리도 flush 안되어 DB에 반영이 안된다.
//테스트 확인을 위해 rollback false 설정
class MemberJpaRepositoryTest {
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private EntityManager em;

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
        Assertions.assertThat(findMember).isSameAs(member);

    }


}