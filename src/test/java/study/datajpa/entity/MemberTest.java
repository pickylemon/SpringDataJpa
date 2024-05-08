package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {
    @PersistenceContext
    EntityManager em;
    
    @Test
    @DisplayName("")
    void EntityTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 15, teamA);
        Member member2 = new Member("member2", 25, teamA);
        Member member3 = new Member("member3", 35, teamB);
        Member member4 = new Member("member4", 45, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush(); //확실하게 영속성 컨텍스트 내의 쿼리를 모두 실행
        em.clear(); //영속성 컨텍스트 비우기

        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println(" -> member.team = " + member.getTeam());
        }

        em.flush();
        em.clear();
        List<Member> members2 = em.createQuery("select m from Member m join fetch m.team t", Member.class).getResultList();
        for (Member member : members2) {
            System.out.println("member = " + member2);
            System.out.println(" -> member.team = " + member2.getTeam());
        }
    }

}