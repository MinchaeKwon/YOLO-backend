package com.yolo.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="account", uniqueConstraints={@UniqueConstraint(columnNames={"email", "type"})})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
//@DynamicInsert
public class Account implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	private long id;
	
	@Column(name="email", nullable=false)
	private String email;
	
	// 일반 - normal, 소셜 - kakao, google, naver
	@Column(name="type", nullable=false)
	private String type;
	
	@Column(name="password")
	private String password;
	
	@Column(name="auth")
//	@ColumnDefault("ROLE_USER")
	private String auth;
	
	@Column(name="nickname", unique=true, nullable=false)
	private String nickname;
	
	@Column(name="phonenumber", nullable=false)
	private String phonenumber;
	
	@Column(name="createAt")
	@CreationTimestamp
	private LocalDateTime createAt;
	
	@Column(name="updateAt")
	@UpdateTimestamp
	private LocalDateTime updateAt;
	
	@Builder
	public Account(String email, String type, String password, String auth, String nickname, String phonenumber, LocalDateTime createAt) {
		this.email = email;
		this.type = type;
		this.password = password;
		this.auth = auth;
		this.nickname = nickname;
		this.phonenumber = phonenumber;
		this.createAt = createAt;
	}
	
	public void update(AccountUpdateDto infoDto) {
		this.nickname = infoDto.getNickname();
		this.phonenumber = infoDto.getPhonenumber();
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
        return email;
    }

    // 사용자의 password를 반환
    @Override
    public String getPassword() {
        return password;
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
