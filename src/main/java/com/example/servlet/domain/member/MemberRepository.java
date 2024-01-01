package com.example.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동시성 문제로 실무에서는 HashMap 대신 ConcurrentHashMap, AtomicLong 을 사용함
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
