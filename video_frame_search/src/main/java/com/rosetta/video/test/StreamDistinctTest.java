package com.rosetta.video.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 测试 jdk8 stream之对象去重
 */
public class StreamDistinctTest {

    public static void main(String[] args) {

        List<PigPerson> pigPeople = new ArrayList<>();
        PigPerson pigPerson1 = new PigPerson();
        PigPerson pigPerson2 = new PigPerson();
        PigPerson pigPerson3 = new PigPerson();
        PigPerson pigPerson4 = new PigPerson();

        pigPerson1.setName("xiaoming");
        pigPerson1.setAge(11);
        pigPerson2.setName("xiaoming");
        pigPerson2.setAge(13);
        pigPerson3.setName("wang");
        pigPerson3.setAge(21);
        pigPerson4.setName("xiaoming");
        pigPerson4.setAge(11);

        pigPeople.add(pigPerson1);
        pigPeople.add(pigPerson2);
        pigPeople.add(pigPerson3);
        pigPeople.add(pigPerson4);

        List<PigPerson> collect = pigPeople.stream().filter(distinctByKey(PigPerson::getName)).sorted(Comparator.comparing(PigPerson::getAge)).limit(5).collect(Collectors.toList());
        System.out.println(collect);

    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return object -> seen.putIfAbsent(keyExtractor.apply(object), Boolean.TRUE) == null;
    }

    static class PigPerson{
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "PigPerson{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
