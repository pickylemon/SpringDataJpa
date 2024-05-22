package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional //이걸 빼면 영속성 컨텍스트 내의 동일성 보장을 못하기 때문에 isEqualTo가 false가 나옴`
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("SpringDataJPA이용 memberRepository테스트")
    void MemberRepositoryTest() {
        //구현체의 정체가 무엇인지 확인
        System.out.println("memberRepository = " + memberRepository);
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());

        //given
        Member member = new Member("memberA");
        memberRepository.save(member);
        //when
        Optional<Member> findMember = memberRepository.findById(member.getId());
//        Member findMember = memberRepository.findById(member.getId()).get();
        //then
//        Assertions.assertThat(findMember).isEqualTo(member);
        assertThat(findMember).isNotEmpty();
        assertThat(findMember.get().getId()).isEqualTo(member.getId());
        assertThat(findMember.get().getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.get()).isEqualTo(member);
        //같은 Tx내이기때문에 영속성 컨텍스트에서 가져와서 같다.(동일성 보장)
    }

    @Test
    @DisplayName("Basic CRUD")
    public void CRUDtest() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        //단건 조회 검사
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검사
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);
        assertThat(all).contains(member1, member2);

        //count 검사
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //update 검사(변경 감지, dirty checking)
        member1.setUsername("member3");
        em.flush();

        //삭제 검사
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        List<Member> afterDelete = memberRepository.findAll();
        assertThat(afterDelete.size()).isEqualTo(0);
        assertThat(afterDelete).isEmpty();
    }
    @Test
    void findByUsernameAndGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result).size().isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(m2);
    }

    @Test
    public void findHelloBy(){
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();
        assertThat(top3HelloBy).size().isEqualTo(2);
    }

    @Test
    public void namedQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> byUsername = memberRepository.findByUsername("AAA");
        assertThat(byUsername).size()
                .isEqualTo(2);
        assertThat(byUsername).contains(m1, m2);
    }
    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> byUsername = memberRepository.findUser("AAA", 20);
        assertThat(byUsername).size().isEqualTo(1);
        assertThat(byUsername.get(0)).isEqualTo(m2);
    }

    @Test
    public void SimpleValueQueryTest(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        assertThat(usernameList).contains("AAA","BBB");
    }

    @Test
    public void DtoQueryTest(){
        Team t1 = new Team("team1");
        Team t2 = new Team("team2");
        teamRepository.save(t1);
        teamRepository.save(t2);

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        m1.changeTeam(t1);
        m2.changeTeam(t2);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        memberDto.forEach(System.out::println);
    }

    @Test
    @DisplayName("@Query에 in절 포함시키기")
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> names = List.of("AAA", "BBB");
        List<Member> byNames = memberRepository.findByNames(names);
        assertThat(byNames).size().isEqualTo(2);
        assertThat(byNames).contains(m1, m2);
    }

    @Test
    @DisplayName("유연한 반환타입")
    public void returnTypes(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("AAA", 30);

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        List<Member> findMemberList = memberRepository.findListByUsername("AAA");
        Member findMember = memberRepository.findMemberByUsername("BBB");
        Optional<Member> optional = memberRepository.findOptionalByUsername("BBB");


        //주목!!

        //1. 단건 조회 메서드 사용했는데 resultSize != 1 (resultSize > 1) : Exception 발생
        //Member findmember2 = memberRepository.findMemberByUsername("AAA");
        //IncorrectResultSizeDataAccessException: Query did not return a unique result: 2 results were returned
        //단건 조회가 보장이 되지 않는 상황에서 단건 반환 메서드를 사용하면 Exception발생
        //원래 발생하는 예외는 NonUniqueResultException. Spring이 한번 감싸서 IncorrectResultSizeDataAccessException으로 던짐
        //예외가 Repository vendor에 종속되지 않도록(예외를 받는 서비스 계층까지 vendor에 종속되므로) 추상화하는 것.
        assertThatThrownBy(()-> memberRepository.findMemberByUsername("AAA"))
                .isInstanceOf(IncorrectResultSizeDataAccessException.class);

        //2. 단건 조회 메서드 사용했는데 resultSize != 1 (resultSize == 0)
        //순수 JPA는 NoResultException이 발생하는데, SpringDataJPA는 내부적으로 try-catch로 예외처리 후 null을 반환
        Member findMember3 = memberRepository.findMemberByUsername("qweradsffadf");
        assertThat(findMember3).isNull();

        //3. 컬렉션 반환 메서드 사용시, 반환되는 데이터가 없어도 null 아님. 예외 발생하지 않음.
        //빈 컬렉션을 그대로 반환
        //조회 된 건이 없어도 컬렉션은 null을 반환하지 않는다. 빈 컬렉션을 반환할 뿐
        List<Member> findMemberList2 = memberRepository.findListByUsername("qwerqefadf");
        assertThat(findMemberList2).isNotNull();
        assertThat(findMemberList2).isEmpty();

    }

    @Test
    @DisplayName("springDataJPA 페이징 테스트 - Page")
    public void paging() {
        //given
        for (int i = 1; i <= 10; i++) {
            memberRepository.save(new Member("member" + i, i % 2));
        }

        int age = 1;
        //스프링 data JPA의 페이지는 0부터 시작이다! 주의하기.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        //String query = "select m from Member m " +
        //        " where m.age = :age" +
        //       " order by m.username desc";  기존 쿼리에 맞추기 위해 Sort.Direction.DESC 추가


        //totalCnt는 따로 가져올 필요 없다. 반환타입이 Page임을 보고 알아서 쿼리 날려줌

        //참고 엔티티는 절대 외부로 노출해서는 안된다. API에서 엔티티를 그대로 반환해서는 안됨!!!
        //Page<Member>도 Page<MemberDto>로 변환해야함
        Page<MemberDto> dtoPage = page.map(MemberDto::new); //memberDto에 Member를 받는 생성자 만들어 둠.
        Page<MemberDto> dtoPage2 = page.map(member ->
            new MemberDto(member.getId(), member.getUsername(), null)
        ); //member의 team이 아직 null이라면..

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();
        content.forEach(System.out::println);
        System.out.println("totalElements = " + totalElements);

        assertThat(content).size().isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(content
                .stream()
                .map(Member::getUsername)
                .collect(toList())).contains("member9", "member7", "member5");

        //여러가지 페이징 테스트도 가능
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }
    @Test
    @DisplayName("springDataJPA 페이징 테스트 - Slice")
    public void paging2() {
        //given
        for (int i = 1; i <= 10; i++) {
            memberRepository.save(new Member("member" + i, i % 2));
        }

        int age = 1;
        //1. 스프링 data JPA의 페이지는 0부터 시작이다! 주의하기.
        //2. Slice는 totalCnt 계산하지 않는다.
        //3. 실제 나가는 limit는 pageSize + 1 (하나 더 요청한다. -> 더보기가 +1에 해당)
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


        //when
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);
        //String query = "select m from Member m " +
        //        " where m.age = :age" +
        //       " order by m.username desc";  기존 쿼리에 맞추기 위해 Sort.Direction.DESC 추가

        //totalCnt는 따로 가져올 필요 없다. 반환타입이 Page임을 보고 알아서 쿼리 날려줌

        //then
        List<Member> content = slice.getContent();
        //long totalElements = slice.getTotalElements();
        //slice는 totalCnt 계산하지 않는다.

        content.forEach(System.out::println);
//        System.out.println("totalElements = " + totalElements);

        assertThat(content).size().isEqualTo(3);
//        assertThat(totalElements).isEqualTo(5);
        assertThat(content
                .stream()
                .map(Member::getUsername)
                .collect(toList())).contains("member9", "member7", "member5");

        //여러가지 페이징 테스트도 가능
        assertThat(slice.getNumber()).isEqualTo(0);
//        assertThat(slice.getTotalPages()).isEqualTo(2);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        //when
        int rowCnt = memberRepository.bulkAgePlus(30);

        Member findMember3 = memberRepository.findMemberByUsername("member3");

        //then
        assertThat(rowCnt).isEqualTo(3);
        assertThat(findMember3.getAge()).isEqualTo(30);
        assertThat(findMember3.getAge()).isNotEqualTo(31);
        //벌크성 수정 쿼리는 영속성 컨텍스트를 무시하고 바로 쿼리를 날려버리기 때문에
        //영속성 컨텍스트 상태와의 동기화가 깨질 수 있다.
        //update쿼리로 사실 member3의 age는 31로 수정되었으나
        //영속성 컨텍스트에서는 계속 30인 상태

        //@Modifying(clearAutomatically = true)로 설정하면 em.flush(), em.clear()해주지 않아도 됨.
    }

    @Test
    public void bulkUpdate2(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        //when
        int rowCnt = memberRepository.bulkAgePlus(30);

//        em.flush();
//        em.clear();
        //벌크성 쿼리를 수행한 후에는 강제로 영속성 컨텍스트를 초기화해주어야 한다.
        //아니면, 영속성 컨텍스트가 세팅되기 전에 벌크성 쿼리를 수행하든가.

        //@Modifying(clearAutomatically = true)로 설정하면 em.flush(), em.clear()해주지 않아도 됨.

        Member findMember3 = memberRepository.findMemberByUsername("member3");

        //then
        assertThat(rowCnt).isEqualTo(3);
        assertThat(findMember3.getAge()).isEqualTo(31);



    }
}