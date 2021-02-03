package org.zerock.club.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ClubMember extends BaseEntity {
    @Id
    private String email;

    private String password;

    private String name;

    private boolean fromSocial;

    //한 테이블에서 연관된 다른 테이블에 대한 정보를 다룬다. One-To-Many 관계를 다룬다.(ElementCollection)
    //ClubMember이 One ClubMemberRole이 Many이다
    //ElementCollection 정의로 인해서 db에 club_member_role_set이 만들어짐
    //club_member_role_set의 엔티티는 ClubMember의 PK값(String email)과 ClubMemeberRole의 enum 값을 가지고 있음
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<ClubMemberRole> roleSet;

    public void addMemberRole(ClubMemberRole clubMemberRole){
        roleSet.add(clubMemberRole);
    }
}
