ALTER TABLE comment_votes
    ADD is_upvote BIT NULL DEFAULT 0;
ALTER TABLE comment_votes
    DROP COLUMN is_upvote;
ALTER TABLE comment_votes
    DROP CONSTRAINT DF__comment_v__is_up__5AA92E00;
