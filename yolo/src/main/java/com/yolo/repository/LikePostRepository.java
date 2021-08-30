package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Account;
import com.yolo.entity.Post;
import com.yolo.entity.LikePost;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, Long> {
	// 특정 게시글의 추천 개수 가져오기
	int countByPost(Post post);
	boolean existsByPostAndAccount(Post post, Account account);
}
