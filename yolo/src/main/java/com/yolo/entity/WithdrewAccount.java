package com.yolo.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="withdrew_account", uniqueConstraints={@UniqueConstraint(columnNames={"socialId", "type"})})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrewAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private long id;
	
	@Column(name="socailId")
	private String socialId;
	
	@Column(name="type")
	private String type;
	
	@Builder
	public WithdrewAccount(String socialId, String type) {
		this.socialId = socialId;
		this.type = type;
	}
	
}
