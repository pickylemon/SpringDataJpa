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

    public List<Member> findByUsernameAndGreaterThan(String username, int age){
        return em.createQuery("select m from Member m " +
                "where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    //Named쿼리 사용법
    //1. Entity에 @NamedQuery작성
    //2. Repository에서 createNamedQuery메서드에 인자로 NamedQuery이름과 반환 타입 넣기
    public List<Member> findByUsername(String username){
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    //순수 JPQL로 페이징하기
    public List<Member> findByPage(int age, int offset, int limit){
        String query = "select m from Member m " +
                " where m.age = :age" +
                " order by m.username desc";
        return em.createQuery(query, Member.class)
                    .setParameter("age", age)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
    }

    //페이징에는 항상 total count가 필요함
    public long totalCount(int age){
        String query = "select count(m) from Member m " +
                " where m.age = :age";

        return em.createQuery(query, Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

}
