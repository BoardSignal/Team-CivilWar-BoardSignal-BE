package com.civilwar.boardsignal.room;

import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Random;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MeetingInfoEasyRandomFixture {

    public static EasyRandom getMeetingInfo(LocalDate start, LocalDate last) {

        Predicate<Field> id = named("id")
            .and(ofType(Long.class))
            .and(inClass(MeetingInfo.class));

        Predicate<Field> peopleCount = named("peopleCount")
            .and(ofType(int.class))
            .and(inClass(MeetingInfo.class));

        EasyRandomParameters parameters = new EasyRandomParameters()
            .excludeField(id)
            .dateRange(start, last)
            .stringLengthRange(5, 20)
            .randomize(peopleCount, () -> new Random().nextInt(3, 8));

        return new EasyRandom(parameters);
    }
}
