package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.ObjectUtils;
import study.datajpa.entity.Member;

@AllArgsConstructor
@Data
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    //member가 team을 이미 가진 경우에만 사용할 수 있음(아니면 NPE)
    public MemberDto(Member member) {
            this(member.getId(), member.getUsername(), member.getTeam().getName());
    }
}
