package com.tastyhouse.core.repository.follow;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.follow.Follow;
import com.tastyhouse.core.entity.follow.QFollow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId) {
        QFollow follow = QFollow.follow;

        Follow result = queryFactory
            .selectFrom(follow)
            .where(
                follow.followerId.eq(followerId),
                follow.followingId.eq(followingId)
            )
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId) {
        QFollow follow = QFollow.follow;

        Long count = queryFactory
            .select(follow.count())
            .from(follow)
            .where(
                follow.followerId.eq(followerId),
                follow.followingId.eq(followingId)
            )
            .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public List<Long> findFollowingIdsByFollowerId(Long followerId) {
        QFollow follow = QFollow.follow;

        return queryFactory
            .select(follow.followingId)
            .from(follow)
            .where(follow.followerId.eq(followerId))
            .fetch();
    }

    @Override
    public long countByFollowerId(Long followerId) {
        QFollow follow = QFollow.follow;

        Long count = queryFactory
            .select(follow.count())
            .from(follow)
            .where(follow.followerId.eq(followerId))
            .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public long countByFollowingId(Long followingId) {
        QFollow follow = QFollow.follow;

        Long count = queryFactory
            .select(follow.count())
            .from(follow)
            .where(follow.followingId.eq(followingId))
            .fetchOne();

        return count != null ? count : 0L;
    }
}
