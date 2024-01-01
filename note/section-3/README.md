# 섹션3. 서블릿, JSP, MVC 패턴 
## 3.1 회원가입 웹 애플리케이션 요구사항 안내 
- 기본 기능
  - 회원 조회 (Retrieve)
  - 회원 저장 (Create)
- 개발 순서 
  - core 모듈 -> 핵심 비즈니스 로직 
- 개선 순서  
  1. Servlet 으로 개발
  2. JSP 로 개발 
  3. MVC 패턴으로 개발

## 3.2 Member *C*.reate, *R*.etrieve 구현 

### Member class 생성 
- Lombok 의 @Getter, @Setter 를 사용하였으므로 Member class 의 getter, setter 를 별도로 만들지 않아도 됨  

```java
@Getter @Setter // Lombok 사용 
public class Member {
    private Long id;
    private String username;
    private int age;

    public Member() { }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

### MemberRepository (구현) 클래스 구현
- 동시성 문제로 실무에서는 HashMap 대신 ConcurrentHashMap, Long 대신 AtomicLong 을 사용함
- singleton 
  - singleton 으로 만들기 위해 `private 생성자` 로 MemberRepository 를 선언하였음 
  - 외부에서 사용할 때에는 미리 만들어둔 `instance` 를 사용하도록 `getInstance()` 함수를 구현해둠  

```java
/**
 * 동시성 문제로 실무에서는 HashMap -> ConcurrentHashMap, Long -> AtomicLong 을 사용함
 */
public class MemberRepository {

    // private 생성자라서 static 하지 않아도 되지만, 일단 해두도록 함
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    // singleton 으로 만듦
    private static final MemberRepository instance = new MemberRepository();

    public static MemberRepository getInstance() {
        return instance;
    }

    // singleton 으로 만들 때에는 private 생성자를 만들어서, 외부에서 생성할 수 없도록 해야 함
    private MemberRepository() { }

    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public List<Member> findAll() {
        // 굳이 새로 선언해서 주는 이유는, store 를 보호하기 위해서임 (불변성?)
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
```

### MemberRepositoryTest 
- 테스트 작성 방법
  - `given` 이런게 주어졌을 때
  - `when` 이렇게 하였을 때
  - `then` 이런 결과가 나와야 해
- `@AfterEach`
  - 매 @Test 가 완료되면 수행되는 함수 

```java
class MemberRepositoryTest {

    // MemberRepository memberRepository = new MemberRepository(); // ! X ( private 생성자 이므로 안됨 )
    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void save() {
        // given (이런게 주어졌을 때)
        Member member = new Member("hello", 20);

        // when (이렇게 하였을 때)
        Member savedMember = memberRepository.save(member);

        // then (이런 결과가 나와야 해)
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void findAll() {
        // given (이런게 주어졌을 때)
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when (이렇게 하였을 때)
        List<Member> result = memberRepository.findAll();

        // then (이런 결과가 나와야 해)
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);
    }
}
```