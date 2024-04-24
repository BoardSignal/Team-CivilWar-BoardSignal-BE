package com.civilwar.boardsignal.user;

import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipantEasyRandomFixture {

    public static EasyRandom getParticipants() {

        Predicate<Field> id = named("id")
            .and(ofType(Long.class))
            .and(inClass(Participant.class));

        Predicate<Field> userId = named("userId")
            .and(ofType(Long.class))
            .and(inClass(Participant.class));

        Predicate<Field> roomId = named("roomId")
            .and(ofType(Long.class))
            .and(inClass(Participant.class));

        EasyRandomParameters parameters = new EasyRandomParameters()
            .excludeField(id)
            .randomize(userId, () -> new Random().nextLong(1, 2500000))
            .randomize(roomId, () -> new Random().nextLong(1, 1042794));

        return new EasyRandom(parameters);

    }

}
