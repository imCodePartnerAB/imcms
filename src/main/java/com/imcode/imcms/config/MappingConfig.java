package com.imcode.imcms.config;

import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.mapping.dto.LoopEntryDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MappingConfig {
    @Bean
    public Function<LoopEntryDTO, Loop.Entry> loopEntryDtoToEntry() {
        return loopEntryDTO -> new Loop.Entry(loopEntryDTO.getNo(), loopEntryDTO.getEnabled());
    }

    @Bean
    public BiFunction<LoopDTO, Version, Loop> loopDtoToLoop(Function<LoopEntryDTO, Loop.Entry> loopEntryDtoToEntry) {
        return (loopDTO, version) -> {
            final List<Loop.Entry> entries = Objects.requireNonNull(loopDTO)
                    .getEntries()
                    .stream()
                    .map(loopEntryDtoToEntry)
                    .collect(Collectors.toList());

            return new Loop(version, loopDTO.getLoopId(), entries.size(), entries);
        };
    }
}
