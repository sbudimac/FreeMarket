package com.freemarket.platform.repository;

import com.freemarket.platform.entity.MarketActor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
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

    @Test
    void whenFindByInvalidUsername_thenReturnEmpty() {
        // when
        Optional<MarketActor> found = marketActorRepository.findByUsername("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void whenExistsByUsername_thenReturnTrue() {
        // given
        MarketActor actor = new MarketActor();
        actor.setUsername("existinguser");
        actor.setEmail("existing@example.com");
        actor.setPasswordHash("hashedpassword");
        entityManager.persistAndFlush(actor);

        // when
        boolean exists = marketActorRepository.existsByUsername("existinguser");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByUsername_thenReturnFalse() {
        // when
        boolean exists = marketActorRepository.existsByUsername("nonexistent");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void whenFindByEmail_thenReturnMarketActor() {
        // given
        MarketActor actor = new MarketActor();
        actor.setUsername("emailuser");
        actor.setEmail("unique@example.com");
        actor.setPasswordHash("hashedpassword");
        entityManager.persistAndFlush(actor);

        // when
        Optional<MarketActor> found = marketActorRepository.findByEmail("unique@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("unique@example.com");
    }

    @Test
    void whenFindByIsVerifiedTrue_thenReturnOnlyVerified() {
        // given
        MarketActor verified = new MarketActor();
        verified.setUsername("verifieduser");
        verified.setEmail("verified@example.com");
        verified.setPasswordHash("hashedpassword");
        verified.setIsVerified(true);
        entityManager.persistAndFlush(verified);

        MarketActor unverified = new MarketActor();
        unverified.setUsername("unverifieduser");
        unverified.setEmail("unverified@example.com");
        unverified.setPasswordHash("hashedpassword");
        unverified.setIsVerified(false);
        entityManager.persistAndFlush(unverified);

        // when
        List<MarketActor> verifiedUsers = marketActorRepository.findByIsVerifiedTrue();

        // then
        assertThat(verifiedUsers).hasSize(1);
        assertThat(verifiedUsers.get(0).getUsername()).isEqualTo("verifieduser");
    }

    @Test
    void whenFindByUsernameContaining_thenReturnMatching() {
        // given
        MarketActor actor1 = new MarketActor();
        actor1.setUsername("john_doe");
        actor1.setEmail("john@example.com");
        actor1.setPasswordHash("hashedpassword");
        entityManager.persistAndFlush(actor1);

        MarketActor actor2 = new MarketActor();
        actor2.setUsername("jane_doe");
        actor2.setEmail("jane@example.com");
        actor2.setPasswordHash("hashedpassword");
        entityManager.persistAndFlush(actor2);

        MarketActor actor3 = new MarketActor();
        actor3.setUsername("bob_smith");
        actor3.setEmail("bob@example.com");
        actor3.setPasswordHash("hashedpassword");
        entityManager.persistAndFlush(actor3);

        // when
        List<MarketActor> found = marketActorRepository.findByUsernameContainingIgnoreCase("doe");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(MarketActor::getUsername)
                .containsExactlyInAnyOrder("john_doe", "jane_doe");
    }

    @Test
    void whenCountByIsVerified_thenReturnCorrectCount() {
        // given
        MarketActor verified1 = new MarketActor();
        verified1.setUsername("verified1");
        verified1.setEmail("v1@example.com");
        verified1.setPasswordHash("hashedpassword");
        verified1.setIsVerified(true);
        entityManager.persistAndFlush(verified1);

        MarketActor verified2 = new MarketActor();
        verified2.setUsername("verified2");
        verified2.setEmail("v2@example.com");
        verified2.setPasswordHash("hashedpassword");
        verified2.setIsVerified(true);
        entityManager.persistAndFlush(verified2);

        MarketActor unverified = new MarketActor();
        unverified.setUsername("unverified");
        unverified.setEmail("u@example.com");
        unverified.setPasswordHash("hashedpassword");
        unverified.setIsVerified(false);
        entityManager.persistAndFlush(unverified);

        // when
        long verifiedCount = marketActorRepository.countByIsVerified(true);
        long unverifiedCount = marketActorRepository.countByIsVerified(false);

        // then
        assertThat(verifiedCount).isEqualTo(2);
        assertThat(unverifiedCount).isEqualTo(1);
    }
}
