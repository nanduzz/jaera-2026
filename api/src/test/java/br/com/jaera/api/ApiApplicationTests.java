package br.com.jaera.api;

import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApiApplicationTests {

	@MockitoBean
	private FirebaseAuth firebaseAuth;

	@Test
	void contextLoads() {
	}

}
