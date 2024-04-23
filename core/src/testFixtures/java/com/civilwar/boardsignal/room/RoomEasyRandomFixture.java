package com.civilwar.boardsignal.room;

import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

import com.civilwar.boardsignal.room.domain.constants.RoomStatus;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Room;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomEasyRandomFixture {

    public static EasyRandom getNonFix() {

        Predicate<Field> id = named("id")
            .and(ofType(Long.class))
            .and(inClass(Room.class));

        Predicate<Field> minPar = named("minParticipants")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        Predicate<Field> maxPar = named("maxParticipants")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        Predicate<Field> status = named("status")
            .and(ofType(RoomStatus.class))
            .and(inClass(Room.class));

        Predicate<Field> minAge = named("minAge")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        Predicate<Field> maxAge = named("maxAge")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        Predicate<Field> headCount = named("headCount")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        EasyRandomParameters parameters = new EasyRandomParameters()
            .excludeField(id)
            .stringLengthRange(5, 20)
            .randomize(minPar, () -> new Random().nextInt(1, 5))
            .randomize(maxPar, () -> new Random().nextInt(6, 10))
            .randomize(status, () -> RoomStatus.NON_FIX)
            .randomize(minAge, () -> new Random().nextInt(20, 25))
            .randomize(maxAge, () -> new Random().nextInt(36, 38))
            .randomize(headCount, () -> new Random().nextInt(5, 7))
            .randomize(MeetingInfo.class, () -> null);

        return new EasyRandom(parameters);
    }

    public static EasyRandom getFix() {

        Predicate<Field> id = named("id")
            .and(ofType(Long.class))
            .and(inClass(Room.class));

        Predicate<Field> minPar = named("minParticipants")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        Predicate<Field> maxPar = named("maxParticipants")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        Predicate<Field> status = named("status")
            .and(ofType(RoomStatus.class))
            .and(inClass(Room.class));

        Predicate<Field> minAge = named("minAge")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        Predicate<Field> maxAge = named("maxAge")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        Predicate<Field> headCount = named("headCount")
            .and(ofType(int.class))
            .and(inClass(Room.class));

        EasyRandomParameters parameters = new EasyRandomParameters()
            .excludeField(id)
            .stringLengthRange(5, 20)
            .randomize(minPar, () -> new Random().nextInt(1, 5))
            .randomize(maxPar, () -> new Random().nextInt(6, 10))
            .randomize(status, () -> RoomStatus.FIX)
            .randomize(minAge, () -> new Random().nextInt(20, 25))
            .randomize(maxAge, () -> new Random().nextInt(36, 38))
            .randomize(headCount, () -> new Random().nextInt(5, 7));

        return new EasyRandom(parameters);
    }

}
