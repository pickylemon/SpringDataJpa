package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

/*
SpringDataJpa 사용하기
 */

@Repository //어노테이션 생략 가능
public interface MemberRepository extends JpaRepository<Member, Long> {

    //쿼리 자동생성
    //단점 : 조건이 많아지면 메서드 명이 너무 복잡해짐
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    List<Member> findTop3HelloBy(); //by뒤에 아무것도 적지 않으면 where조건 안 주는 효과(전체에 대한 조회)

    //SpringDataJpa의 @Query
    //Entity에 작성한 NamedQuery를 편리하게 불러올 수 있다.
    //작성한 NamedQuery에 작성한 JPQL에 파라미터를 작성한 경우에는
    //@Param 어노테이션으로 명시해주기
    //참고 @Query적지 않아도(주석처리해도) 동작함.
    //관례에 따라 (JpaRepository<>에 타입으로 명시한 도메인) Member.findByUsername이라는 NamedQuery가 있는지 먼저 찾는다.
    //[정리]
    //즉, 엔티티명.메서드명으로 NamedQuery가 있는지 찾고
    //없으면 메서드 명으로 쿼리를 만들어서 실행
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    //실무에서 가장 많이 쓰이는 형태
    //리포지토리의 메서드에 바로 @Query작성
    //일종의 이름이 없는 NamedQuery
    //NamedQuery처럼 역시 애플리케이션 로딩 시점에 파싱을 해서 문법적 오류가 있을 때 애플리케이션 로딩 시점에 에러를 띄운다.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);


    //@Query를 사용해서 단순 값, DTO 조회하기 (실무에서 많이 쓰임)
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //Dto를 조회시에는 new Operation 적어주어야 한다.
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
            " from Member m " +
            " join m.team t")
    List<MemberDto> findMemberDto();

    //파라미터 바인딩 - IN절의 활용
    @Query("select m from Member m " +
            " where m.username in :names") //IN절이니까 = 대신 IN키워드
    List<Member> findByNames(@Param("names") List<String> names);

    //유연한 반환타입 - 1. List 또는 컬렉션
    List<Member> findListByUsername(String username);
    //유연한 반환타입 - 2. 객체 단건 조회
    Member findMemberByUsername(String username);
    //유연한 반환타입 - 3. 단건을 Optional로 반환
    Optional<Member> findOptionalByUsername(String username);

    //스프링 Data JPA의 페이징 추상화
    //검색 조건과 Pageable인터페이스만 넘기면된다. (PageRequest는 Pageable의 구현체)
    @Query(countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    //참고 엔티티는 절대 외부로 노출해서는 안된다. API에서 엔티티를 그대로 반환해서는 안됨!!!
    //Page<Member>도 Page<MemberDto>로 변환해야함
    Slice<Member> findSliceByAge(int age, Pageable pageable);


}
