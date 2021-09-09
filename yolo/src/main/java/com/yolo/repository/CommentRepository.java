package com.yolo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Comment;
import com.yolo.entity.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	// 특정 게시글의 댓글 개수 가져오기
	int countByPost(Post post);
	
	// 모든 댓글 가져오기
	List<Comment> findByPostIdOrderById(Long postId);
}
