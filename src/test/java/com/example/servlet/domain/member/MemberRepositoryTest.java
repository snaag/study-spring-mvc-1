package com.example.servlet.domain.member;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
