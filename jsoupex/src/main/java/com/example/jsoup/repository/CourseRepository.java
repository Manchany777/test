package com.example.jsoup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jsoup.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {

}
