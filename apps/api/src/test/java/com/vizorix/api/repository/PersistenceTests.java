package com.vizorix.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vizorix.api.domain.ExecutionSession;
import com.vizorix.api.domain.ExecutionStep;
import com.vizorix.api.domain.Project;
import com.vizorix.api.domain.User;
import com.vizorix.api.domain.VariableSnapshot;
import com.vizorix.api.domain.enums.Language;
import com.vizorix.api.domain.enums.SessionStatus;
import com.vizorix.api.domain.enums.VariableScope;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests ensuring data persistence, validations, cascades, and constraints. */
@SpringBootTest
@Transactional
class PersistenceTests {

  @Autowired private UserRepository userRepository;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private ExecutionSessionRepository executionSessionRepository;

  @Autowired private ExecutionStepRepository executionStepRepository;

  @Autowired private VariableSnapshotRepository variableSnapshotRepository;

  /** Tests user entity save, update, auditing timestamps, and optimistic lock checks. */
  @Test
  void testUserPersistenceAndAuditing() {
    User user = new User();
    user.setEmail("test@vizorix.com");
    user.setUsername("testuser");
    user.setPasswordHash("hash123");

    User savedUser = userRepository.saveAndFlush(user);

    assertNotNull(savedUser.getId());
    assertNotNull(savedUser.getCreatedAt());
    assertNotNull(savedUser.getUpdatedAt());
    assertEquals(0L, savedUser.getVersion());

    // Test updating User triggers version and updatedAt update
    savedUser.setUsername("updateduser");
    User updatedUser = userRepository.saveAndFlush(savedUser);
    assertEquals(1L, updatedUser.getVersion());
    assertTrue(updatedUser.getUpdatedAt().isAfter(updatedUser.getCreatedAt()));
  }

  /** Tests that duplicate emails trigger database constraint validation failures. */
  @Test
  void testUniqueConstraintViolation() {
    User user1 = new User();
    user1.setEmail("dup@vizorix.com");
    user1.setUsername("user1");
    user1.setPasswordHash("hash1");
    userRepository.saveAndFlush(user1);

    User user2 = new User();
    user2.setEmail("dup@vizorix.com"); // Duplicate email
    user2.setUsername("user2");
    user2.setPasswordHash("hash2");

    assertThrows(
        DataIntegrityViolationException.class,
        () -> {
          userRepository.saveAndFlush(user2);
        });
  }

  /** Tests project references and relationships mappings. */
  @Test
  void testProjectPersistenceAndRelationalBindings() {
    User user = new User();
    user.setEmail("projectowner@vizorix.com");
    user.setUsername("owner");
    user.setPasswordHash("hash");
    User savedUser = userRepository.saveAndFlush(user);

    Project project = new Project();
    project.setName("Java Sandbox");
    project.setDescription("Testing Java trace output code flow");
    project.setLanguage(Language.JAVA);
    project.setSourceCode("public class Main {}");
    project.setUser(savedUser);

    Project savedProject = projectRepository.saveAndFlush(project);
    assertNotNull(savedProject.getId());
    assertEquals(savedUser.getId(), savedProject.getUser().getId());
  }

  /** Tests saving a project cascades items correctly, and cascades deletions recursively. */
  @Test
  void testCascadeAndOrphansRemoval() {
    User user = new User();
    user.setEmail("cascade@vizorix.com");
    user.setUsername("cascadeuser");
    user.setPasswordHash("hash");
    User savedUser = userRepository.saveAndFlush(user);

    Project project = new Project();
    project.setName("Cascade Demo");
    project.setLanguage(Language.JAVA);
    project.setSourceCode("class Test {}");
    project.setUser(savedUser);
    Project savedProject = projectRepository.saveAndFlush(project);

    ExecutionSession session = new ExecutionSession();
    session.setStatus(SessionStatus.PENDING);
    session.setProject(savedProject);
    savedProject.getSessions().add(session);

    ExecutionStep step = new ExecutionStep();
    step.setStepNumber(1);
    step.setLineNumber(10);
    step.setMethodName("main");
    step.setClassName("Test");
    step.setSession(session);
    session.getSteps().add(step);

    VariableSnapshot snapshot = new VariableSnapshot();
    snapshot.setName("x");
    snapshot.setValue("42");
    snapshot.setType("int");
    snapshot.setScope(VariableScope.LOCAL);
    snapshot.setStep(step);
    step.getSnapshots().add(snapshot);

    // Save project: Cascade should persist execution details
    projectRepository.saveAndFlush(savedProject);

    // Load from DB using repositories and assert persistence
    List<ExecutionSession> sessions =
        executionSessionRepository.findAllByProjectId(savedProject.getId());
    assertEquals(1, sessions.size());
    UUID sessionId = sessions.get(0).getId();

    List<ExecutionStep> steps =
        executionStepRepository.findAllBySessionIdOrderByStepNumberAsc(sessionId);
    assertEquals(1, steps.size());
    UUID stepId = steps.get(0).getId();

    List<VariableSnapshot> snapshots = variableSnapshotRepository.findAllByStepId(stepId);
    assertEquals(1, snapshots.size());

    // Test cascade delete
    projectRepository.delete(savedProject);
    projectRepository.flush();

    assertTrue(executionSessionRepository.findAllByProjectId(savedProject.getId()).isEmpty());
    assertTrue(executionStepRepository.findAllBySessionIdOrderByStepNumberAsc(sessionId).isEmpty());
    assertTrue(variableSnapshotRepository.findAllByStepId(stepId).isEmpty());
  }
}
