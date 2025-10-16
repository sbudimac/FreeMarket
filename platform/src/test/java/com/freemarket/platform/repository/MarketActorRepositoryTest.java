package com.freemarket.platform.repository;

import com.freemarket.platform.entity.MarketActor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class MarketActorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MarketActorRepository marketActorRepository;

    @Test
    void whenFindByUsername_thenReturnMarketActor() {
        // given
        MarketActor actor = new MarketActor();
        actor.setUsername("testuser");
        actor.setEmail("test@example.com");
        actor.setPasswordHash("hashedpassword");
        entityManager.persistAndFlush(actor);

        // when
        Optional<MarketActor> found = marketActorRepository.findByUsername("testuser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }
}
