package study.datajpa.Repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional //이걸 빼면 영속성 컨텍스트 내의 동일성 보장을 못하기 때문에 isEqualTo가 false가 나옴`
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("SpringDataJPA이용 memberRepository테스트")
    void MemberRepositoryTest() {
        //given
        Member member = new Member("memberA");
        memberRepository.save(member);
        //when
        Optional<Member> findMember = memberRepository.findById(member.getId());
//        Member findMember = memberRepository.findById(member.getId()).get();
        //then
//        Assertions.assertThat(findMember).isEqualTo(member);
        Assertions.assertThat(findMember).isNotEmpty();
        Assertions.assertThat(findMember.get().getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.get().getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember.get()).isEqualTo(member);
        //같은 Tx내이기때문에 영속성 컨텍스트에서 가져와서 같다.(동일성 보장)

    }

}