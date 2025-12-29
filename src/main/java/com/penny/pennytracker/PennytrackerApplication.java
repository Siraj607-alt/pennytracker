package com.penny.pennytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class PennytrackerApplication {

	@Autowired(required = false)
	private EmojiUtil emoji;

	public static void main(String[] args) {
		SpringApplication.run(PennytrackerApplication.class, args);
	}

	@PostConstruct
	public void testEmojiBean() {
		System.out.println("EMOJI BEAN LOADED? ---> " + emoji);
	}
}
