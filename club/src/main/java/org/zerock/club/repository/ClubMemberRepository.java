package org.zerock.club.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.club.entity.ClubMember;

import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember,String> {
    //EntityGraph는 엔티티의 특정한 속성을 같이 로딩하도록 표시하는 어노테이션이다.
    //attributePaths: 로딩 설정을 변경하고 싶은 속성의 이름을 배열로 명시
    //type: @EntityGraph를 어떤 방식으로 적용할 것인지를 설정
    //LOAD 속성값은 AttributePaths에 명시한 속성은 EAGER로 처리하고 나머지는 엔티티 클래스에 명시되거나 기본 방식으로 처리

    @EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
    //여기서 EntityGraph에서 roleSet은 ClubMember의 Set 타입 변수를 말한다.
    //ClubMember의 email기준으로 ClubMemeberRole를 left outer join를 시킨다.
    @Query("select m from ClubMember m where m.fromSocial = :social and m.email =:email")
    Optional<ClubMember> findByEmail(String email, boolean social);

}
