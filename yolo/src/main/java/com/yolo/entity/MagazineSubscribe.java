package com.yolo.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="magazine_subscribe")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MagazineSubscribe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private long id;
	
	@OneToOne
    @JoinColumn(name = "account_id")
    private Account account;
	
	@Builder
	public MagazineSubscribe(Account account) {
		this.account = account;
	}
}
