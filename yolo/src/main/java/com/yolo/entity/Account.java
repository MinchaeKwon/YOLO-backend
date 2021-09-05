package com.yolo.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.yolo.dto.AccountUpdateDto;

@Entity
@Table(name="account", uniqueConstraints={@UniqueConstraint(columnNames={"socialId", "type"})})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private long id;
	
	// 소셜 로그인하고 받아오는 고유한 id
	@Column(name="socialId", nullable=false)
	private String socialId;
	
	// 소셜 로그인 타입 - kakao, naver
	@Column(name="type", nullable=false)
	private String type;
	
	@Column(name="auth")
	private String auth;
	
	@Column(name="nickname", nullable=false)
	private String nickname;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy="account", orphanRemoval=true)
	private Image image;
	
	@Column(name="createAt")
	@CreationTimestamp
	private LocalDateTime createAt;
	
	@Column(name="updateAt")
	@UpdateTimestamp
	private LocalDateTime updateAt;
	
	@Builder
	public Account(String socialId, String type, String auth, String nickname) {
		this.socialId = socialId;
		this.type = type;
		this.auth = auth;
		this.nickname = nickname;
	}
	
	public void update(AccountUpdateDto infoDto) {
		this.nickname = infoDto.getNickname();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> roles = new HashSet<>();
        for (String role : auth.split(",")) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        return roles;
	}

	// 사용자의 id를 반환 (unique한 값)
    @Override
    public String getUsername() {
        return socialId;
    }

    // 사용자의 password를 반환 -> password 사용하지 않기 때문에 null 반환
    @Override
    public String getPassword() {
//        return password;
    	return null;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true -> 잠금되지 않았음
    }

    // 패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true -> 사용 가능
    }

}
