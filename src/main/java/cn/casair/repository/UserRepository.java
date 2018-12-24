package cn.casair.repository;

import cn.casair.domain.User;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.Instant;

/**
 * Spring Data JPA repository for the User entity.
 */
//@Repository
@Mapper
@Repository
public interface UserRepository {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    User findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

    User findOneByResetKey(String resetKey);

    User findOneByEmailIgnoreCase(String email);

    User findOneByLogin(String login);

    User findOneByLogin2(String login);

/*
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesById(Long id);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByEmail(String email);

    Page<User> findAllByLoginNot(Pageable pageable, String login);
*/

    Long insert(User user);

    Long insertSelective(User user);

    Long insertAll(List<User> users);

    void deleteById(Long id);

    void deleteByObject(User user);

    void deleteByIds(List<Long> ids);

    void deleteAll();

    Long updateById(User user);

    Long updateByIdSelective(User user);

    User selectOneById(Long id);

    User selectOneByObject(User user);

    List<User> selectByObject(User user);

    List<User> selectAll();

    Long count(User user);

    Long countAll();
}
