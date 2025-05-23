package game_forum_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GameForumApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameForumApiApplication.class, args);
	}

}
