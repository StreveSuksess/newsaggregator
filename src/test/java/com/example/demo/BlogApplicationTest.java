package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BlogApplicationTest {

    @Test
    void contextLoads() {
        assertNotNull(BlogApplication.class);
    }

    @Test
    void main() {
        assertNotNull(BlogApplication.class.getDeclaredMethods());
    }
} 