package de.caluga.test.mongo.suite;

import de.caluga.morphium.Morphium;
import de.caluga.morphium.MorphiumSingleton;
import de.caluga.morphium.NameProvider;
import de.caluga.morphium.ObjectMapper;
import de.caluga.morphium.annotations.Entity;
import de.caluga.morphium.annotations.Id;
import de.caluga.morphium.annotations.caching.NoCache;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 27.11.13
 * Time: 08:39
 * Test polymorphism mechanism in Morphium
 */
public class PolymorphismTest extends MongoTest {
    @Test
    public void polymorphTest() throws Exception {
        MorphiumSingleton.get().dropCollection(PolyTest.class);
        OtherSubClass p = new OtherSubClass();
        p.setPoly("poly");
        p.setOther("other");
        MorphiumSingleton.get().store(p);

        SubClass sb = new SubClass();
        sb.setPoly("poly super");
        sb.setSub("sub");
        MorphiumSingleton.get().store(sb);

        assert (MorphiumSingleton.get().createQueryFor(PolyTest.class).countAll() == 2);
        List<PolyTest> lst = MorphiumSingleton.get().createQueryFor(PolyTest.class).asList();
        for (PolyTest tst : lst) {
            log.info("Class " + tst.getClass().toString());
        }
    }

    public static class PolyNameProvider implements NameProvider {

        @Override
        public String getCollectionName(Class<?> type, ObjectMapper om, boolean translateCamelCase, boolean useFQN, String specifiedName, Morphium morphium) {
            return "poly";
        }
    }

    @Entity(polymorph = true, nameProvider = PolyNameProvider.class)
    @NoCache
    public static abstract class PolyTest {
        private String poly;

        @Id
        private ObjectId id;

        public String getPoly() {
            return poly;
        }

        public void setPoly(String poly) {
            this.poly = poly;
        }
    }

    public static class SubClass extends PolyTest {
        private String sub;

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }
    }

    public static class OtherSubClass extends PolyTest {
        private String other;

        public String getOther() {
            return other;
        }

        public void setOther(String o) {
            this.other = o;
        }
    }
}
