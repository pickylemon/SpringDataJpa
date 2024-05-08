package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {
    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }
    public Member find(Long id){
        return em.find(Member.class, id);
    }

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public List<Member> findAll(){
        return em.createQuery(
                        "select m from Member m", Member.class)
                .getResultList();
    }

    public long count(){
        return em.createQuery(
                "select count(m) from Member m", Long.class)
                .getSingleResult();
        //단건은 getSingleResult, 리스트는 getResultList
    }
    public void delete(Member member){
        em.remove(member);
    }

}
