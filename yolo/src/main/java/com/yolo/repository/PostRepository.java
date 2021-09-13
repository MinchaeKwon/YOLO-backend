package com.yolo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Account;
import com.yolo.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>  {

	// 특정 사용자가 작성한 게시글 가져오기
	Page<Post> findByAccount(Account account, Pageable pageable);
	
	// 게시글 페이징 처리 (인기순)
	@Query("SELECT p FROM Post p ORDER BY p.liked.size DESC, p.id DESC")
	Page<Post> findAlOrderByLiked(Pageable pageable);
	
}
