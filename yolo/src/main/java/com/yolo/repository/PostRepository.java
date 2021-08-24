package com.yolo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Account;
import com.yolo.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>  {

	// 특정 사용자가 작성한 게시글 가져오기
	List<Post> findByAccount(Account account);
	
}
