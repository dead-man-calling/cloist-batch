package com.lcc.batch.job;

import com.lcc.batch.entity.Vote;
import com.lcc.batch.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Configuration
public class UnfinishedVotingConfig {
    private final VoteRepository voteRepository;

    @Bean
    public Job unfinishedVotingJob(
            JobBuilderFactory jobBuilderFactory,
            Step unfinishedVotingJopStep
    ) {
        return jobBuilderFactory.get("unfinishedVotingJob")
                .preventRestart()
                .start(unfinishedVotingJopStep)
                .build();
    }

    @Bean
    public Step unfinishedVotingJopStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("unfinishedVotingJobStep")
                .<Vote, Vote> chunk(100)
                .reader(unfinishedVotingReader())
                .processor(this.unfinishedVotingProcessor())
                .writer(this.unfinishedVotingWriter())
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<Vote> unfinishedVotingReader() {
        List<Vote> votes = voteRepository.findByCreatedAtBefore(LocalDateTime.now());
        return new ListItemReader<>(votes);
    }

    public ItemProcessor<Vote, Vote> unfinishedVotingProcessor() {
        return new ItemProcessor<Vote, Vote>() {
            @Override
            public Vote process(Vote item) throws Exception {
                System.out.println(item);
                item.setExistProperty(Vote.ExistProperty.FINISHED);
                return item;
            }
        };
    }

    public ItemWriter<Vote> unfinishedVotingWriter() {
        return (voteRepository::saveAll);
    }
}
